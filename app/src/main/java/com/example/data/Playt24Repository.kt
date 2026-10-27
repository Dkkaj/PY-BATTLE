package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class Playt24Repository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- 1. USER PROFILE & AUTH INITIALIZATION ---

    fun observeUserProfile(userId: String): Flow<UserProfile?> =
        db.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                } else {
                    null
                }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.GET, "users/$userId")
                throw error
            }

    suspend fun getOrCreateUserProfile(): Result<UserProfile> = runCatching {
        val uid = requireUserId()
        val currentUser = auth.currentUser
        val docRef = db.collection("users").document(uid)
        try {
            val snap = docRef.get().await()
            if (snap.exists()) {
                snap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    ?: UserProfile(id = uid, userId = uid)
            } else {
                val defaultName = currentUser?.displayName?.takeIf { it.isNotBlank() }
                    ?: currentUser?.email?.substringBefore("@")?.takeIf { it.isNotBlank() }
                    ?: "Prince"
                val refCode = "PYB" + uid.take(4).uppercase()
                val newProfile = UserProfile(
                    id = uid,
                    userId = uid,
                    username = defaultName.take(30),
                    email = (currentUser?.email ?: "player@pybattle.in").take(100),
                    phone = "6201393486",
                    ffUid = "284910384",
                    ffIgn = "${defaultName.take(12).uppercase()}_FF",
                    stateName = "Delhi",
                    ageVerified = true,
                    kycStatus = "VERIFIED",
                    kycPanNumber = "ABCDE1234F",
                    depositedCoins = 5L,
                    winningCoins = 0L,
                    bonusCoins = 0L,
                    matchesPlayed = 4L,
                    totalKills = 0L,
                    totalCoinsWon = 0L,
                    referralCode = refCode,
                    referredBy = "",
                    role = "PLAYER",
                    isBanned = false,
                    selfExcludedUntil = 0L,
                    dailyDepositLimit = 2000L,
                    language = "EN",
                    importantNoticeEnabled = true
                )
                docRef.set(newProfile.toWriteMap(isNew = true)).await()
                val createdSnap = docRef.get().await()
                createdSnap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    ?: newProfile
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    suspend fun getUserProfileById(userId: String): Result<UserProfile?> = runCatching {
        val docRef = db.collection("users").document(userId)
        try {
            val snap = docRef.get().await()
            snap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            throw e
        }
    }

    suspend fun updateUserProfile(updated: UserProfile): Result<Unit> = runCatching {
        val uid = requireUserId()
        val targetUid = updated.userId.ifBlank { uid }
        val docRef = db.collection("users").document(targetUid)
        try {
            val snap = docRef.get().await()
            val existingCreatedAt = snap.getTimestamp("createdAt") ?: updated.createdAt
            val payload = updated.copy(userId = targetUid, createdAt = existingCreatedAt).toWriteMap(isNew = existingCreatedAt == null)
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- 2. TOURNAMENTS & REGISTRATIONS ---

    fun observeTournamentsByStatus(status: String): Flow<List<Tournament>> =
        db.collection("tournaments")
            .whereEqualTo("status", status)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Tournament::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedBy { it.startTimeEpochMillis }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, "tournaments")
                throw error
            }

    fun observeUserRegistrations(userId: String): Flow<List<Registration>> =
        db.collection("registrations")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Registration::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedByDescending { it.createdAt ?: com.google.firebase.Timestamp(0, 0) }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, "registrations")
                throw error
            }

    fun observeRegistrationsForTournament(tournamentId: String): Flow<List<Registration>> =
        db.collection("registrations")
            .whereEqualTo("tournamentId", tournamentId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Registration::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedBy { it.slotNumber }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, "registrations")
                throw error
            }

    suspend fun createTournament(tournament: Tournament): Result<String> = runCatching {
        val uid = requireUserId()
        val tournamentId = tournament.id.ifBlank { "tourney_${UUID.randomUUID().toString().replace("-", "").take(12)}" }
        val docRef = db.collection("tournaments").document(tournamentId)
        try {
            val model = tournament.copy(id = tournamentId, createdByUid = uid)
            docRef.set(model.toWriteMap(isNew = true)).await()
            tournamentId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun joinTournament(
        tournament: Tournament,
        profile: UserProfile,
        ffUid: String,
        ffIgn: String,
        teammateInfo: String,
        preferredSlot: Long,
        bonusUsagePercent: Long
    ): Result<String> = runCatching {
        val uid = requireUserId()
        require(!profile.isBanned) { "Account is suspended from joining tournaments." }
        require(profile.selfExcludedUntil < System.currentTimeMillis()) { "Self-exclusion period is active." }
        require(tournament.filledSlots < tournament.totalSlots) { "Tournament slots are already full." }

        val breakdown = calculateEntryFeeBreakdown(
            entryFee = tournament.entryFee,
            depositedCoins = profile.depositedCoins,
            winningCoins = profile.winningCoins,
            bonusCoins = profile.bonusCoins,
            bonusUsagePercent = bonusUsagePercent
        )
        require(breakdown.canAfford) {
            "Insufficient PlayCoins. Need ${breakdown.shortfall} more coins."
        }

        val regId = "reg_${tournament.id}_${uid}"
        val regRef = db.collection("registrations").document(regId)
        val userRef = db.collection("users").document(uid)
        val tourneyRef = db.collection("tournaments").document(tournament.id)
        val txId = "tx_entry_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val txRef = db.collection("wallet_transactions").document(txId)

        try {
            val existingReg = regRef.get().await()
            require(!existingReg.exists()) { "You have already joined this tournament!" }

            val userSnap = userRef.get().await()
            val userCreatedAt = userSnap.getTimestamp("createdAt") ?: profile.createdAt
            val tourneySnap = tourneyRef.get().await()
            val tourneyCreatedAt = tourneySnap.getTimestamp("createdAt") ?: tournament.createdAt

            val newSlot = preferredSlot.coerceIn(1L, tournament.totalSlots)
            val registration = Registration(
                id = regId,
                tournamentId = tournament.id,
                tournamentTitle = tournament.title,
                gameMode = tournament.gameMode,
                userId = uid,
                username = profile.username,
                ffUid = ffUid.trim(),
                ffIgn = ffIgn.trim(),
                teammateInfo = teammateInfo.trim(),
                slotNumber = newSlot,
                entryFeePaid = breakdown.totalFee,
                depositedUsed = breakdown.depositedUsed,
                winningUsed = breakdown.winningUsed,
                bonusUsed = breakdown.bonusUsed,
                kills = 0L,
                rankAchieved = 0L,
                prizeWon = 0L,
                proofText = "",
                disputeReason = "",
                status = "REGISTERED"
            )

            val updatedProfile = profile.copy(
                depositedCoins = (profile.depositedCoins - breakdown.depositedUsed).coerceAtLeast(0L),
                winningCoins = (profile.winningCoins - breakdown.winningUsed).coerceAtLeast(0L),
                bonusCoins = (profile.bonusCoins - breakdown.bonusUsed).coerceAtLeast(0L),
                matchesPlayed = profile.matchesPlayed + 1L,
                createdAt = userCreatedAt
            )

            val tx = WalletTransaction(
                id = txId,
                userId = uid,
                username = profile.username,
                type = "MATCH_ENTRY",
                amountCoins = breakdown.totalFee,
                depositedDelta = -breakdown.depositedUsed,
                winningDelta = -breakdown.winningUsed,
                bonusDelta = -breakdown.bonusUsed,
                status = "COMPLETED",
                utrNumber = tournament.id,
                paymentMethod = "WALLET_SPLIT",
                gstAmountInr = 0L,
                tdsAmountInr = 0L,
                note = "Joined ${tournament.title} (Slot #$newSlot)"
            )

            val batch = db.batch()
            batch.set(regRef, registration.toWriteMap(isNew = true))
            batch.set(userRef, updatedProfile.toWriteMap(isNew = userCreatedAt == null))
            if (tourneyCreatedAt != null) {
                batch.update(
                    tourneyRef,
                    mapOf(
                        "filledSlots" to (tournament.filledSlots + 1L).coerceAtMost(tournament.totalSlots),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
            }
            batch.set(txRef, tx.toWriteMap(isNew = true))
            batch.commit().await()
            regId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, regRef.path)
            throw e
        }
    }

    suspend fun submitMatchProofOrDispute(
        registration: Registration,
        proofText: String,
        disputeReason: String
    ): Result<Unit> = runCatching {
        val docRef = db.collection("registrations").document(registration.id)
        try {
            val newStatus = if (disputeReason.isNotBlank()) "DISPUTED" else registration.status
            docRef.update(
                mapOf(
                    "proofText" to proofText.take(500),
                    "disputeReason" to disputeReason.take(500),
                    "status" to newStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // --- 3. WALLET: MANUAL QR UTR DEPOSIT & KYC WITHDRAWAL ---

    fun observeUserWalletTransactions(userId: String): Flow<List<WalletTransaction>> =
        db.collection("wallet_transactions")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(WalletTransaction::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedByDescending { it.createdAt ?: com.google.firebase.Timestamp(0, 0) }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, "wallet_transactions")
                throw error
            }

    suspend fun submitManualUtrDeposit(
        profile: UserProfile,
        amountInr: Long,
        bonusCoins: Long,
        utrNumber: String,
        paymentApp: String
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val cleanUtr = utrNumber.trim()
        require(cleanUtr.length >= 6) { "Please enter a valid 12-digit UTR / Reference Number." }
        require(amountInr >= 10L) { "Minimum deposit amount is ₹10." }
        require(amountInr <= profile.dailyDepositLimit) {
            "Amount exceeds your Responsible Gaming daily deposit limit of ₹${profile.dailyDepositLimit}."
        }

        val txId = "utr_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        val docRef = db.collection("wallet_transactions").document(txId)
        val gstInr = ((amountInr * 28L) / 128L)
        val tx = WalletTransaction(
            id = txId,
            userId = uid,
            username = profile.username,
            type = "DEPOSIT_UTR",
            amountCoins = amountInr + bonusCoins,
            depositedDelta = amountInr,
            winningDelta = 0L,
            bonusDelta = bonusCoins,
            status = "PENDING",
            utrNumber = cleanUtr,
            paymentMethod = paymentApp,
            gstAmountInr = gstInr,
            tdsAmountInr = 0L,
            note = "Manual QR Deposit ₹$amountInr (+${bonusCoins} Bonus) • UTR: $cleanUtr [Pending Admin Approval]"
        )
        try {
            docRef.set(tx.toWriteMap(isNew = true)).await()
            txId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    suspend fun requestWithdrawal(
        profile: UserProfile,
        amountCoins: Long,
        payoutAddress: String,
        minWithdrawalCoins: Long
    ): Result<String> = runCatching {
        val uid = requireUserId()
        require(profile.kycStatus == "VERIFIED") { "KYC Verification is required before your first withdrawal." }
        require(amountCoins >= minWithdrawalCoins) { "Minimum withdrawal is $minWithdrawalCoins Winning PlayCoins." }
        require(profile.winningCoins >= amountCoins) { "Only Winning PlayCoins can be withdrawn. Available: ${profile.winningCoins}" }
        require(payoutAddress.isNotBlank()) { "Please enter a valid UPI ID or Bank Account." }

        val tdsInr = if (amountCoins >= 100L) (amountCoins * 30L) / 100L else 0L
        val netPayoutInr = amountCoins - tdsInr
        val txId = "wd_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        val txRef = db.collection("wallet_transactions").document(txId)
        val userRef = db.collection("users").document(uid)

        try {
            val userSnap = userRef.get().await()
            val userCreatedAt = userSnap.getTimestamp("createdAt") ?: profile.createdAt
            val updatedProfile = profile.copy(
                winningCoins = (profile.winningCoins - amountCoins).coerceAtLeast(0L),
                createdAt = userCreatedAt
            )
            val tx = WalletTransaction(
                id = txId,
                userId = uid,
                username = profile.username,
                type = "WITHDRAWAL",
                amountCoins = amountCoins,
                depositedDelta = 0L,
                winningDelta = -amountCoins,
                bonusDelta = 0L,
                status = "PENDING",
                utrNumber = payoutAddress.trim().take(50),
                paymentMethod = "UPI_PAYOUT",
                gstAmountInr = 0L,
                tdsAmountInr = tdsInr,
                note = "Withdraw $amountCoins Coins to ${payoutAddress.trim()} (Net ₹$netPayoutInr after ₹$tdsInr TDS)"
            )
            val batch = db.batch()
            batch.set(userRef, updatedProfile.toWriteMap(isNew = userCreatedAt == null))
            batch.set(txRef, tx.toWriteMap(isNew = true))
            batch.commit().await()
            txId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, txRef.path)
            throw e
        }
    }

    // --- 4. SUPPORT TICKETS (1-OPEN-TICKET-PER-CATEGORY RULE) ---

    fun observeUserSupportTickets(userId: String): Flow<List<SupportTicket>> =
        db.collection("support_tickets")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(SupportTicket::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedByDescending { it.createdAt ?: com.google.firebase.Timestamp(0, 0) }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, "support_tickets")
                throw error
            }

    suspend fun createSupportTicket(
        profile: UserProfile,
        category: String,
        subject: String,
        message: String,
        proofRef: String,
        existingTickets: List<SupportTicket>
    ): Result<String> = runCatching {
        val uid = requireUserId()
        val hasOpenInSameCategory = existingTickets.any {
            it.category == category && (it.status == "OPEN" || it.status == "IN_PROGRESS")
        }
        require(!hasOpenInSameCategory) {
            "Anti-Spam Rule: You already have an active ticket in this category. Please wait for resolution before opening another."
        }
        val ticketId = "tkt_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        val docRef = db.collection("support_tickets").document(ticketId)
        val ticket = SupportTicket(
            id = ticketId,
            userId = uid,
            username = profile.username,
            category = category,
            subject = subject.trim().take(100),
            message = message.trim().take(800),
            proofRef = proofRef.trim().take(250),
            adminReply = "",
            status = "OPEN"
        )
        try {
            docRef.set(ticket.toWriteMap(isNew = true)).await()
            ticketId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    // --- 5. LEADERBOARDS & PLATFORM CONFIG ---

    fun observeLeaderboard(period: String): Flow<List<LeaderboardEntry>> =
        db.collection("leaderboard_entries")
            .whereEqualTo("period", period)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(LeaderboardEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedBy { it.position }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, "leaderboard_entries")
                throw error
            }

    fun observePlatformConfig(): Flow<PlatformConfig> =
        db.collection("platform_config").document("global")
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(PlatformConfig::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        ?: PlatformConfig()
                } else {
                    PlatformConfig()
                }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.GET, "platform_config/global")
                throw error
            }

    // --- 6. REWARDS & PROMOTIONS ---

    suspend fun claimOfferReward(
        profile: UserProfile,
        rewardBonusCoins: Long,
        offerTitle: String
    ): Result<Unit> = runCatching {
        val uid = requireUserId()
        val userRef = db.collection("users").document(uid)
        val txId = "bns_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        val txRef = db.collection("wallet_transactions").document(txId)
        try {
            val snap = userRef.get().await()
            val createdAt = snap.getTimestamp("createdAt") ?: profile.createdAt
            val updated = profile.copy(
                bonusCoins = profile.bonusCoins + rewardBonusCoins,
                createdAt = createdAt
            )
            val tx = WalletTransaction(
                id = txId,
                userId = uid,
                username = profile.username,
                type = "BONUS_CREDIT",
                amountCoins = rewardBonusCoins,
                depositedDelta = 0L,
                winningDelta = 0L,
                bonusDelta = rewardBonusCoins,
                status = "COMPLETED",
                utrNumber = "PROMO",
                paymentMethod = "OFFER_REWARD",
                gstAmountInr = 0L,
                tdsAmountInr = 0L,
                note = "Claimed $offerTitle (+$rewardBonusCoins Bonus Coins)"
            )
            val batch = db.batch()
            batch.set(userRef, updated.toWriteMap(isNew = createdAt == null))
            batch.set(txRef, tx.toWriteMap(isNew = true))
            batch.commit().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, userRef.path)
            throw e
        }
    }

    // --- 7. INITIAL SEEDING FOR REALISTIC PYBATTLE TOURNAMENTS & LEADERBOARDS ---

    suspend fun seedInitialDataIfNeeded() {
        val uid = auth.currentUser?.uid ?: return
        try {
            val configRef = db.collection("platform_config").document("global")
            val configSnap = configRef.get().await()
            if (!configSnap.exists()) {
                configRef.set(PlatformConfig().toWriteMap()).await()
            } else {
                val existingMerchant = configSnap.getString("merchantName") ?: ""
                if (existingMerchant.contains("PLAYT24", ignoreCase = true)) {
                    configRef.set(PlatformConfig().toWriteMap()).await()
                }
            }

            val existingTourneys = db.collection("tournaments")
                .whereEqualTo("status", "UPCOMING")
                .limit(1)
                .get()
                .await()
            if (existingTourneys.isEmpty) {
                val now = System.currentTimeMillis()
                val sampleTournaments = listOf(
                    Tournament(
                        id = "ff_max_br_solo_101",
                        title = "FF MAX Booyah Royale #101",
                        gameMode = "FULL_MAP",
                        mapName = "Bermuda",
                        teamSize = "SOLO",
                        entryFee = 15L,
                        prizePool = 600L,
                        perKillPrize = 8L,
                        booyahPrize = 220L,
                        rank2Prize = 110L,
                        rank3Prize = 70L,
                        totalSlots = 48L,
                        filledSlots = 34L,
                        startTimeEpochMillis = now + 25 * 60 * 1000L,
                        status = "UPCOMING",
                        roomId = "88492019",
                        roomPassword = "2424",
                        roomPublished = true,
                        createdByUid = uid
                    ),
                    Tournament(
                        id = "ff_max_1rs_mega_102",
                        title = "₹1 Entry ₹10,000 Mega Booyah Special",
                        gameMode = "FULL_MAP",
                        mapName = "Bermuda",
                        teamSize = "SOLO",
                        entryFee = 1L,
                        prizePool = 10000L,
                        perKillPrize = 50L,
                        booyahPrize = 3000L,
                        rank2Prize = 1500L,
                        rank3Prize = 800L,
                        totalSlots = 48L,
                        filledSlots = 45L,
                        startTimeEpochMillis = now + 50 * 60 * 1000L,
                        status = "UPCOMING",
                        roomId = "",
                        roomPassword = "",
                        roomPublished = false,
                        createdByUid = uid
                    ),
                    Tournament(
                        id = "ff_cs_4v4_pro_201",
                        title = "Clash Squad 4v4 Unlimited Ammo #201",
                        gameMode = "CLASH_SQUAD",
                        mapName = "Purgatory",
                        teamSize = "SQUAD",
                        entryFee = 40L,
                        prizePool = 300L,
                        perKillPrize = 0L,
                        booyahPrize = 300L,
                        rank2Prize = 0L,
                        rank3Prize = 0L,
                        totalSlots = 8L,
                        filledSlots = 6L,
                        startTimeEpochMillis = now + 40 * 60 * 1000L,
                        status = "UPCOMING",
                        roomId = "77410922",
                        roomPassword = "9911",
                        roomPublished = true,
                        createdByUid = uid
                    ),
                    Tournament(
                        id = "ff_lw_1v1_headshot_301",
                        title = "Lone Wolf 1v1 Desert Eagle Only #301",
                        gameMode = "LONE_WOLF",
                        mapName = "Iron Cage",
                        teamSize = "SOLO",
                        entryFee = 20L,
                        prizePool = 36L,
                        perKillPrize = 0L,
                        booyahPrize = 36L,
                        rank2Prize = 0L,
                        rank3Prize = 0L,
                        totalSlots = 2L,
                        filledSlots = 1L,
                        startTimeEpochMillis = now + 15 * 60 * 1000L,
                        status = "UPCOMING",
                        roomId = "55201944",
                        roomPassword = "1122",
                        roomPublished = true,
                        createdByUid = uid
                    ),
                    Tournament(
                        id = "ff_free_daily_practice_401",
                        title = "PYBattle Daily Free Match — Zero Entry",
                        gameMode = "FREE_MATCH",
                        mapName = "Alpine",
                        teamSize = "SOLO",
                        entryFee = 0L,
                        prizePool = 100L,
                        perKillPrize = 2L,
                        booyahPrize = 40L,
                        rank2Prize = 25L,
                        rank3Prize = 15L,
                        totalSlots = 48L,
                        filledSlots = 41L,
                        startTimeEpochMillis = now + 35 * 60 * 1000L,
                        status = "UPCOMING",
                        roomId = "99012834",
                        roomPassword = "0000",
                        roomPublished = true,
                        createdByUid = uid
                    ),
                    Tournament(
                        id = "ff_live_survival_501",
                        title = "Full Map Survival Pro Scrims #501",
                        gameMode = "FULL_MAP",
                        mapName = "Kalahari",
                        teamSize = "SQUAD",
                        entryFee = 25L,
                        prizePool = 1000L,
                        perKillPrize = 10L,
                        booyahPrize = 400L,
                        rank2Prize = 200L,
                        rank3Prize = 100L,
                        totalSlots = 48L,
                        filledSlots = 48L,
                        startTimeEpochMillis = now - 10 * 60 * 1000L,
                        status = "ONGOING",
                        roomId = "66391827",
                        roomPassword = "7788",
                        roomPublished = true,
                        createdByUid = uid
                    )
                )
                val batch = db.batch()
                for (t in sampleTournaments) {
                    batch.set(db.collection("tournaments").document(t.id), t.toWriteMap(isNew = true))
                }
                batch.commit().await()
            }

            val existingLb = db.collection("leaderboard_entries")
                .whereEqualTo("period", "DAILY")
                .limit(1)
                .get()
                .await()
            if (existingLb.isEmpty) {
                val dailyRows = listOf(
                    Triple(1L, "deepak deepak", 500L),
                    Triple(2L, "bloody chinmay", 300L),
                    Triple(3L, "LUFFY GAMING", 300L),
                    Triple(4L, "Ankit shah", 206L),
                    Triple(5L, "manik deb", 200L),
                    Triple(6L, "aman sahu", 200L),
                    Triple(7L, "Kabita Modak", 200L),
                    Triple(8L, "gokuhere goku", 200L),
                    Triple(9L, "sakshi ghosh", 179L),
                    Triple(10L, "Fani Khatua", 168L),
                    Triple(11L, "Supreme x", 137L),
                    Triple(12L, "om prakash", 124L),
                    Triple(13L, "manna yadav", 116L),
                    Triple(14L, "habol habol", 103L),
                    Triple(15L, "JR DARPAN13", 100L)
                )
                val weeklyRows = listOf(
                    Triple(1L, "deepak deepak", 1045L),
                    Triple(2L, "kabir demon", 1037L),
                    Triple(3L, "desi sarthak", 900L),
                    Triple(4L, "gokuhere goku", 848L),
                    Triple(5L, "CYBER RITESH...", 691L),
                    Triple(6L, "rahull kuswaah", 690L),
                    Triple(7L, "bloody chinmay", 620L),
                    Triple(8L, "LUFFY GAMING", 610L),
                    Triple(9L, "HASAN BOOS...", 585L),
                    Triple(10L, "Sachin Kumar", 554L),
                    Triple(11L, "gaurav xx", 553L),
                    Triple(12L, "MindFlayer DA...", 552L),
                    Triple(13L, "Vishal chauhan", 545L),
                    Triple(14L, "aman sahu", 522L),
                    Triple(15L, "ABHI GG", 510L)
                )
                val monthlyRows = listOf(
                    Triple(1L, "AJAY NAG", 52369L),
                    Triple(2L, "Huzaifa ff", 42438L),
                    Triple(3L, "exoff2.0 exoff2.0", 40216L),
                    Triple(4L, "Naymar jr", 34383L),
                    Triple(5L, "kabir demon", 32197L),
                    Triple(6L, "Dhruv Raj", 30468L),
                    Triple(7L, "JR DARPAN13", 28254L),
                    Triple(8L, "Mohit Verma", 25941L),
                    Triple(9L, "Sunil Thope", 25058L),
                    Triple(10L, "Safik Hussain", 22834L),
                    Triple(11L, "fyyd 55fft", 22272L),
                    Triple(12L, "desi sarthak", 19390L),
                    Triple(13L, "yash sam", 19370L),
                    Triple(14L, "spider man", 18158L),
                    Triple(15L, "Mohit Bawankar", 17861L)
                )

                val batch = db.batch()
                dailyRows.forEach { (pos, name, coins) ->
                    val id = "lb_daily_$pos"
                    val entry = LeaderboardEntry(
                        id = id,
                        period = "DAILY",
                        position = pos,
                        playerName = name,
                        ffUid = "${10000000 + pos * 731}",
                        winningCoins = coins,
                        kills = (coins / 10L).coerceAtLeast(3L),
                        antiCheatClean = true
                    )
                    batch.set(db.collection("leaderboard_entries").document(id), entry.toWriteMap())
                }
                weeklyRows.forEach { (pos, name, coins) ->
                    val id = "lb_weekly_$pos"
                    val entry = LeaderboardEntry(
                        id = id,
                        period = "WEEKLY",
                        position = pos,
                        playerName = name,
                        ffUid = "${20000000 + pos * 819}",
                        winningCoins = coins,
                        kills = (coins / 12L).coerceAtLeast(10L),
                        antiCheatClean = true
                    )
                    batch.set(db.collection("leaderboard_entries").document(id), entry.toWriteMap())
                }
                monthlyRows.forEach { (pos, name, coins) ->
                    val id = "lb_monthly_$pos"
                    val entry = LeaderboardEntry(
                        id = id,
                        period = "MONTHLY",
                        position = pos,
                        playerName = name,
                        ffUid = "${30000000 + pos * 947}",
                        winningCoins = coins,
                        kills = (coins / 20L).coerceAtLeast(45L),
                        antiCheatClean = true
                    )
                    batch.set(db.collection("leaderboard_entries").document(id), entry.toWriteMap())
                }
                batch.commit().await()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "seedInitialDataIfNeeded")
        }
    }
}
