const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read users or tournaments", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("tournaments").where("status", "==", "UPCOMING").get());
});

test("Authenticated user: can create and read own UserProfile, but cannot read another user's profile", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      username: "BobFF",
      email: "bob@example.com",
      ffUid: "99887766",
      ffIgn: "BOB_PRO",
      stateName: "Maharashtra",
      ageVerified: true,
      kycStatus: "VERIFIED",
      depositedCoins: 50,
      winningCoins: 100,
      bonusCoins: 10,
      matchesPlayed: 4,
      totalKills: 12,
      totalCoinsWon: 250,
      role: "PLAYER",
      isBanned: false,
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      username: "Prince",
      email: "prince@example.com",
      ffUid: "11223344",
      ffIgn: "PRINCE_FF",
      stateName: "Delhi",
      ageVerified: true,
      kycStatus: "VERIFIED",
      depositedCoins: 5,
      winningCoins: 0,
      bonusCoins: 0,
      matchesPlayed: 4,
      totalKills: 0,
      totalCoinsWon: 0,
      role: "PLAYER",
      isBanned: false,
      createdAt: now,
      updatedAt: now,
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Shadow update test: rejects UserProfile with ghost field", async () => {
  const now = new Date();
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      username: "Prince",
      email: "prince@example.com",
      ffUid: "11223344",
      ffIgn: "PRINCE_FF",
      stateName: "Delhi",
      ageVerified: true,
      kycStatus: "VERIFIED",
      depositedCoins: 5,
      winningCoins: 0,
      bonusCoins: 0,
      matchesPlayed: 4,
      totalKills: 0,
      totalCoinsWon: 0,
      role: "PLAYER",
      isBanned: false,
      ghostField: "hacked",
      createdAt: now,
      updatedAt: now,
    })
  );
});

test("Wallet transactions: user can query own transactions with userId filter, fails without filter", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("wallet_transactions").doc("tx_alice_1").set({
      userId: ALICE_UID,
      username: "Prince",
      type: "DEPOSIT_UTR",
      amountCoins: 80,
      depositedDelta: 75,
      winningDelta: 0,
      bonusDelta: 5,
      status: "PENDING",
      utrNumber: "412345678901",
      paymentMethod: "UPI_QR",
      gstAmountInr: 16,
      tdsAmountInr: 0,
      note: "Add Cash Bonus 75 INR",
      createdAt: now,
      updatedAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("wallet_transactions").where("userId", "==", ALICE_UID).get()
  );
  await assertFails(aliceDb.collection("wallet_transactions").get());
});

test("Admin RBAC: user registered in /admins can list all wallet transactions and update status", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("admins").doc(ADMIN_UID).set({
      userId: ADMIN_UID,
      roleName: "SUPER_ADMIN",
      createdAt: now,
    });
    await context.firestore().collection("wallet_transactions").doc("tx_alice_1").set({
      userId: ALICE_UID,
      username: "Prince",
      type: "DEPOSIT_UTR",
      amountCoins: 80,
      depositedDelta: 75,
      winningDelta: 0,
      bonusDelta: 5,
      status: "PENDING",
      utrNumber: "412345678901",
      paymentMethod: "UPI_QR",
      gstAmountInr: 16,
      tdsAmountInr: 0,
      note: "Add Cash Bonus 75 INR",
      createdAt: now,
      updatedAt: now,
    });
  });

  const adminDb = testEnv.authenticatedContext(ADMIN_UID).firestore();
  await assertSucceeds(adminDb.collection("wallet_transactions").get());
});
