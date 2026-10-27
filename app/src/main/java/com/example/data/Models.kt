package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val username: String = "Player",
    val email: String = "",
    val phone: String = "",
    val ffUid: String = "",
    val ffIgn: String = "",
    val stateName: String = "Delhi",
    val ageVerified: Boolean = true,
    val kycStatus: String = "VERIFIED", // UNVERIFIED, PENDING, VERIFIED, REJECTED
    val kycPanNumber: String = "",
    val depositedCoins: Long = 5L,
    val winningCoins: Long = 0L,
    val bonusCoins: Long = 0L,
    val matchesPlayed: Long = 4L,
    val totalKills: Long = 0L,
    val totalCoinsWon: Long = 0L,
    val referralCode: String = "",
    val referredBy: String = "",
    val role: String = "PLAYER", // PLAYER, ADMIN
    val isBanned: Boolean = false,
    val selfExcludedUntil: Long = 0L,
    val dailyDepositLimit: Long = 2000L,
    val language: String = "EN", // EN, HI
    val importantNoticeEnabled: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val totalPlayCoins: Long
        get() = depositedCoins + winningCoins + bonusCoins

    val kdRatio: String
        get() = if (matchesPlayed <= 0L) "0.00" else String.format("%.2f", totalKills.toDouble() / matchesPlayed.toDouble())

    fun toWriteMap(isNew: Boolean = false): Map<String, Any> {
        val raw = mapOf<String, Any?>(
            "userId" to userId,
            "username" to username,
            "email" to email,
            "phone" to phone,
            "ffUid" to ffUid,
            "ffIgn" to ffIgn,
            "stateName" to stateName,
            "ageVerified" to ageVerified,
            "kycStatus" to kycStatus,
            "kycPanNumber" to kycPanNumber,
            "depositedCoins" to depositedCoins,
            "winningCoins" to winningCoins,
            "bonusCoins" to bonusCoins,
            "matchesPlayed" to matchesPlayed,
            "totalKills" to totalKills,
            "totalCoinsWon" to totalCoinsWon,
            "referralCode" to referralCode,
            "referredBy" to referredBy,
            "role" to role,
            "isBanned" to isBanned,
            "selfExcludedUntil" to selfExcludedUntil,
            "dailyDepositLimit" to dailyDepositLimit,
            "language" to language,
            "importantNoticeEnabled" to importantNoticeEnabled,
            "createdAt" to if (isNew || createdAt == null) FieldValue.serverTimestamp() else createdAt,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

@IgnoreExtraProperties
data class Tournament(
    @DocumentId val id: String = "",
    val title: String = "",
    val gameMode: String = "FULL_MAP", // FULL_MAP, CLASH_SQUAD, LONE_WOLF, FREE_MATCH, CUSTOM_ROOM
    val mapName: String = "Bermuda",
    val teamSize: String = "SOLO", // SOLO, DUO, SQUAD
    val entryFee: Long = 10L,
    val prizePool: Long = 500L,
    val perKillPrize: Long = 5L,
    val booyahPrize: Long = 200L,
    val rank2Prize: Long = 100L,
    val rank3Prize: Long = 60L,
    val totalSlots: Long = 48L,
    val filledSlots: Long = 0L,
    val startTimeEpochMillis: Long = 0L,
    val status: String = "UPCOMING", // UPCOMING, ONGOING, COMPLETED, CANCELLED
    val roomId: String = "",
    val roomPassword: String = "",
    val roomPublished: Boolean = false,
    val rulesText: String = "No Emulators, No Hacks/Scripts, Level 40+ Free Fire UID required. Room ID & Password shared 10 mins before match.",
    val createdByUid: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toWriteMap(isNew: Boolean = false): Map<String, Any> {
        val raw = mapOf<String, Any?>(
            "title" to title,
            "gameMode" to gameMode,
            "mapName" to mapName,
            "teamSize" to teamSize,
            "entryFee" to entryFee,
            "prizePool" to prizePool,
            "perKillPrize" to perKillPrize,
            "booyahPrize" to booyahPrize,
            "rank2Prize" to rank2Prize,
            "rank3Prize" to rank3Prize,
            "totalSlots" to totalSlots,
            "filledSlots" to filledSlots,
            "startTimeEpochMillis" to startTimeEpochMillis,
            "status" to status,
            "roomId" to roomId,
            "roomPassword" to roomPassword,
            "roomPublished" to roomPublished,
            "rulesText" to rulesText,
            "createdByUid" to createdByUid,
            "createdAt" to if (isNew || createdAt == null) FieldValue.serverTimestamp() else createdAt,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

@IgnoreExtraProperties
data class Registration(
    @DocumentId val id: String = "",
    val tournamentId: String = "",
    val tournamentTitle: String = "",
    val gameMode: String = "FULL_MAP",
    val userId: String = "",
    val username: String = "",
    val ffUid: String = "",
    val ffIgn: String = "",
    val teammateInfo: String = "",
    val slotNumber: Long = 1L,
    val entryFeePaid: Long = 0L,
    val depositedUsed: Long = 0L,
    val winningUsed: Long = 0L,
    val bonusUsed: Long = 0L,
    val kills: Long = 0L,
    val rankAchieved: Long = 0L,
    val prizeWon: Long = 0L,
    val proofText: String = "",
    val disputeReason: String = "",
    val status: String = "REGISTERED", // REGISTERED, PLAYED, REFUNDED, DISPUTED
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toWriteMap(isNew: Boolean = false): Map<String, Any> {
        val raw = mapOf<String, Any?>(
            "tournamentId" to tournamentId,
            "tournamentTitle" to tournamentTitle,
            "gameMode" to gameMode,
            "userId" to userId,
            "username" to username,
            "ffUid" to ffUid,
            "ffIgn" to ffIgn,
            "teammateInfo" to teammateInfo,
            "slotNumber" to slotNumber,
            "entryFeePaid" to entryFeePaid,
            "depositedUsed" to depositedUsed,
            "winningUsed" to winningUsed,
            "bonusUsed" to bonusUsed,
            "kills" to kills,
            "rankAchieved" to rankAchieved,
            "prizeWon" to prizeWon,
            "proofText" to proofText,
            "disputeReason" to disputeReason,
            "status" to status,
            "createdAt" to if (isNew || createdAt == null) FieldValue.serverTimestamp() else createdAt,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

@IgnoreExtraProperties
data class WalletTransaction(
    @DocumentId val id: String = "",
    val userId: String = "",
    val username: String = "",
    val type: String = "DEPOSIT_UTR", // DEPOSIT_UTR, MATCH_ENTRY, MATCH_REFUND, MATCH_WINNING, WITHDRAWAL, BONUS_CREDIT, ADMIN_ADJUST
    val amountCoins: Long = 0L,
    val depositedDelta: Long = 0L,
    val winningDelta: Long = 0L,
    val bonusDelta: Long = 0L,
    val status: String = "PENDING", // PENDING, APPROVED, COMPLETED, REJECTED
    val utrNumber: String = "",
    val paymentMethod: String = "UPI_QR",
    val gstAmountInr: Long = 0L,
    val tdsAmountInr: Long = 0L,
    val note: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toWriteMap(isNew: Boolean = false): Map<String, Any> {
        val raw = mapOf<String, Any?>(
            "userId" to userId,
            "username" to username,
            "type" to type,
            "amountCoins" to amountCoins,
            "depositedDelta" to depositedDelta,
            "winningDelta" to winningDelta,
            "bonusDelta" to bonusDelta,
            "status" to status,
            "utrNumber" to utrNumber,
            "paymentMethod" to paymentMethod,
            "gstAmountInr" to gstAmountInr,
            "tdsAmountInr" to tdsAmountInr,
            "note" to note,
            "createdAt" to if (isNew || createdAt == null) FieldValue.serverTimestamp() else createdAt,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

@IgnoreExtraProperties
data class SupportTicket(
    @DocumentId val id: String = "",
    val userId: String = "",
    val username: String = "",
    val category: String = "BR_SUPPORT", // BR_SUPPORT, CS_LW_SUPPORT, HACKER_POV, PAYMENT_SUPPORT
    val subject: String = "",
    val message: String = "",
    val proofRef: String = "",
    val adminReply: String = "",
    val status: String = "OPEN", // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toWriteMap(isNew: Boolean = false): Map<String, Any> {
        val raw = mapOf<String, Any?>(
            "userId" to userId,
            "username" to username,
            "category" to category,
            "subject" to subject,
            "message" to message,
            "proofRef" to proofRef,
            "adminReply" to adminReply,
            "status" to status,
            "createdAt" to if (isNew || createdAt == null) FieldValue.serverTimestamp() else createdAt,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

@IgnoreExtraProperties
data class LeaderboardEntry(
    @DocumentId val id: String = "",
    val period: String = "DAILY", // DAILY, WEEKLY, MONTHLY
    val position: Long = 1L,
    val playerName: String = "",
    val ffUid: String = "",
    val winningCoins: Long = 0L,
    val kills: Long = 0L,
    val antiCheatClean: Boolean = true,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toWriteMap(): Map<String, Any> {
        val raw = mapOf<String, Any?>(
            "period" to period,
            "position" to position,
            "playerName" to playerName,
            "ffUid" to ffUid,
            "winningCoins" to winningCoins,
            "kills" to kills,
            "antiCheatClean" to antiCheatClean,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

@IgnoreExtraProperties
data class PlatformConfig(
    @DocumentId val id: String = "global",
    val configId: String = "global",
    val paidContestsEnabled: Boolean = true,
    val announcementTicker: String = "📢 PYBattle MUST FOLLOW OFFICIAL RULES • ROOM ID & PASSWORD REVEALED 10 MIN BEFORE START • 100% FAIR PLAY",
    val upiId: String = "pybattle.verified@zapupi",
    val merchantName: String = "PYBattle",
    val minWithdrawalCoins: Long = 50L,
    val bonusUsagePercent: Long = 10L,
    val blockedStatesCsv: String = "Assam,Odisha,Telangana,Nagaland,Sikkim,Andhra Pradesh",
    val whatsappChannelUrl: String = "https://whatsapp.com/channel/pybattleofficial",
    val telegramChannelUrl: String = "https://t.me/pybattleesports",
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun toWriteMap(): Map<String, Any> {
        val raw = mapOf<String, Any?>(
            "configId" to configId,
            "paidContestsEnabled" to paidContestsEnabled,
            "announcementTicker" to announcementTicker,
            "upiId" to upiId,
            "merchantName" to merchantName,
            "minWithdrawalCoins" to minWithdrawalCoins,
            "bonusUsagePercent" to bonusUsagePercent,
            "blockedStatesCsv" to blockedStatesCsv,
            "whatsappChannelUrl" to whatsappChannelUrl,
            "telegramChannelUrl" to telegramChannelUrl,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

data class AddCashTier(
    val amountInr: Long,
    val baseCoins: Long,
    val bonusCoins: Long,
    val tag: String
) {
    val totalCoins: Long get() = baseCoins + bonusCoins
}

val DEFAULT_ADD_CASH_TIERS = listOf(
    AddCashTier(amountInr = 10L, baseCoins = 10L, bonusCoins = 0L, tag = "STARTER"),
    AddCashTier(amountInr = 75L, baseCoins = 75L, bonusCoins = 5L, tag = "+5 BONUS"),
    AddCashTier(amountInr = 140L, baseCoins = 140L, bonusCoins = 10L, tag = "+10 BONUS"),
    AddCashTier(amountInr = 270L, baseCoins = 270L, bonusCoins = 30L, tag = "+30 BONUS"),
    AddCashTier(amountInr = 500L, baseCoins = 500L, bonusCoins = 70L, tag = "BEST VALUE +70")
)

data class FeeBreakdown(
    val totalFee: Long,
    val bonusUsed: Long,
    val depositedUsed: Long,
    val winningUsed: Long,
    val canAfford: Boolean,
    val shortfall: Long
)

fun calculateEntryFeeBreakdown(
    entryFee: Long,
    depositedCoins: Long,
    winningCoins: Long,
    bonusCoins: Long,
    bonusUsagePercent: Long = 10L
): FeeBreakdown {
    if (entryFee <= 0L) {
        return FeeBreakdown(0L, 0L, 0L, 0L, true, 0L)
    }
    val maxBonusAllowed = (entryFee * bonusUsagePercent) / 100L
    val bonusUsed = minOf(bonusCoins, maxBonusAllowed)
    val remainingAfterBonus = entryFee - bonusUsed
    val depositedUsed = minOf(depositedCoins, remainingAfterBonus)
    val remainingAfterDeposit = remainingAfterBonus - depositedUsed
    val winningUsed = minOf(winningCoins, remainingAfterDeposit)
    val totalCovered = bonusUsed + depositedUsed + winningUsed
    val shortfall = (entryFee - totalCovered).coerceAtLeast(0L)
    return FeeBreakdown(
        totalFee = entryFee,
        bonusUsed = bonusUsed,
        depositedUsed = depositedUsed,
        winningUsed = winningUsed,
        canAfford = shortfall == 0L,
        shortfall = shortfall
    )
}
