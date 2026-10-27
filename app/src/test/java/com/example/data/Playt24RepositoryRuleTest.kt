package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Playt24RepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun getOrCreateUserProfile_authenticatedOwner_succeedsAndPreventsCrossUserRead() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = Playt24Repository(firestore, auth)

        val aliceProfileResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.getOrCreateUserProfile()
        }
        assertTrue("Expected profile creation to succeed: ${aliceProfileResult.exceptionOrNull()}", aliceProfileResult.isSuccess)
        val aliceProfile = aliceProfileResult.getOrThrow()
        assertEquals(aliceUid, aliceProfile.userId)
        assertEquals(5L, aliceProfile.depositedCoins)

        // Sign in as Bob and verify Bob cannot read Alice's PII UserProfile
        signInTestUser(BOB_EMAIL)
        val bobRepo = Playt24Repository(firestore, auth)
        val crossReadResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getUserProfileById(aliceUid)
        }
        assertTrue("Expected cross-user profile read to fail", crossReadResult.isFailure)
        val fsEx = crossReadResult.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull("Expected FirebaseFirestoreException", fsEx)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsEx?.code)
    }

    @Test
    fun submitManualUtrDeposit_andObserveWalletTransactions_succeedsForOwner() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repo = Playt24Repository(firestore, auth)

        val profile = withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.getOrCreateUserProfile().getOrThrow()
        }

        val txIdResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.submitManualUtrDeposit(
                profile = profile,
                amountInr = 75L,
                bonusCoins = 5L,
                utrNumber = "412398765432",
                paymentApp = "GPay"
            )
        }
        assertTrue("Expected UTR deposit creation to succeed: ${txIdResult.exceptionOrNull()}", txIdResult.isSuccess)
        val txId = txIdResult.getOrThrow()

        val emittedTxs = withTimeout(FLOW_TIMEOUT_MS) {
            repo.observeUserWalletTransactions(aliceUid).first { list -> list.any { it.id == txId } }
        }
        assertTrue(emittedTxs.any { it.id == txId && it.amountCoins == 80L })
    }

    @Test
    fun observeUserWalletTransactions_unauthenticatedCaller_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repo = Playt24Repository(firestore, auth)

        var caught: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repo.observeUserWalletTransactions("unauth_uid").first()
            }
        } catch (e: Throwable) {
            caught = e
        }
        assertNotNull("Expected unauthenticated query to throw", caught)
        val fsEx = caught?.unwrapFirestoreException()
        assertNotNull("Expected nested FirebaseFirestoreException, got $caught", fsEx)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsEx?.code)
    }

    private companion object {
        const val ALICE_EMAIL = "alice_playt24@test.com"
        const val BOB_EMAIL = "bob_playt24@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}
