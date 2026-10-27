package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.GoogleAuthHelper
import com.example.data.*
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainBottomTab {
    OFFER, RANKING, HOME, WALLET, PROFILE
}

enum class ActiveOverlayScreen {
    NONE,
    TOURNAMENT_LIST,
    TOURNAMENT_DETAIL,
    MY_CONTESTS,
    MANUAL_QR_PAY,
    WITHDRAW_COINS,
    TRANSACTION_HISTORY,
    SUPPORT_CENTER,
    ADMIN_CONSOLE,
    COMPLIANCE_LEGAL
}

class Playt24ViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Playt24Repository(application.applicationContext)
    private val authHelper = GoogleAuthHelper(application.applicationContext)
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    private val _currentUser = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _platformConfig = MutableStateFlow(PlatformConfig())
    val platformConfig: StateFlow<PlatformConfig> = _platformConfig.asStateFlow()

    private val _upcomingTournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val upcomingTournaments: StateFlow<List<Tournament>> = _upcomingTournaments.asStateFlow()

    private val _ongoingTournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val ongoingTournaments: StateFlow<List<Tournament>> = _ongoingTournaments.asStateFlow()

    private val _completedTournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val completedTournaments: StateFlow<List<Tournament>> = _completedTournaments.asStateFlow()

    private val _userRegistrations = MutableStateFlow<List<Registration>>(emptyList())
    val userRegistrations: StateFlow<List<Registration>> = _userRegistrations.asStateFlow()

    private val _selectedTournamentRegistrations = MutableStateFlow<List<Registration>>(emptyList())
    val selectedTournamentRegistrations: StateFlow<List<Registration>> = _selectedTournamentRegistrations.asStateFlow()

    private val _walletTransactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val walletTransactions: StateFlow<List<WalletTransaction>> = _walletTransactions.asStateFlow()

    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(emptyList())
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    private val _dailyLeaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val dailyLeaderboard: StateFlow<List<LeaderboardEntry>> = _dailyLeaderboard.asStateFlow()

    private val _weeklyLeaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val weeklyLeaderboard: StateFlow<List<LeaderboardEntry>> = _weeklyLeaderboard.asStateFlow()

    private val _monthlyLeaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val monthlyLeaderboard: StateFlow<List<LeaderboardEntry>> = _monthlyLeaderboard.asStateFlow()

    // Admin Console Streams
    private val _adminTransactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val adminTransactions: StateFlow<List<WalletTransaction>> = _adminTransactions.asStateFlow()

    private val _adminTickets = MutableStateFlow<List<SupportTicket>>(emptyList())
    val adminTickets: StateFlow<List<SupportTicket>> = _adminTickets.asStateFlow()

    private val _adminUsers = MutableStateFlow<List<UserProfile>>(emptyList())
    val adminUsers: StateFlow<List<UserProfile>> = _adminUsers.asStateFlow()

    // Navigation & UI State
    private val _selectedTab = MutableStateFlow(MainBottomTab.HOME)
    val selectedTab: StateFlow<MainBottomTab> = _selectedTab.asStateFlow()

    private val _overlayScreen = MutableStateFlow(ActiveOverlayScreen.NONE)
    val overlayScreen: StateFlow<ActiveOverlayScreen> = _overlayScreen.asStateFlow()

    private val _selectedGameModeFilter = MutableStateFlow("ALL")
    val selectedGameModeFilter: StateFlow<String> = _selectedGameModeFilter.asStateFlow()

    private val _selectedTeamSizeFilter = MutableStateFlow("ALL")
    val selectedTeamSizeFilter: StateFlow<String> = _selectedTeamSizeFilter.asStateFlow()

    private val _selectedTournamentId = MutableStateFlow<String?>(null)
    val selectedTournamentId: StateFlow<String?> = _selectedTournamentId.asStateFlow()

    private val _selectedMyContestsTab = MutableStateFlow("ONGOING") // ONGOING, UPCOMING, COMPLETED
    val selectedMyContestsTab: StateFlow<String> = _selectedMyContestsTab.asStateFlow()

    private val _pendingAddCashTier = MutableStateFlow(DEFAULT_ADD_CASH_TIERS[1])
    val pendingAddCashTier: StateFlow<AddCashTier> = _pendingAddCashTier.asStateFlow()

    private val _claimedOffers = MutableStateFlow<Set<String>>(emptySet())
    val claimedOffers: StateFlow<Set<String>> = _claimedOffers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _feedbackBanner = MutableStateFlow<String?>(null)
    val feedbackBanner: StateFlow<String?> = _feedbackBanner.asStateFlow()

    private val _isErrorBanner = MutableStateFlow(false)
    val isErrorBanner: StateFlow<Boolean> = _isErrorBanner.asStateFlow()

    private val dataJobs = mutableListOf<Job>()
    private var tournamentRegJob: Job? = null
    private var adminJobsStarted = false

    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        _currentUser.value = user
        if (user != null) {
            startAuthenticatedStreams(user.uid)
        } else {
            clearStreams()
        }
    }

    init {
        firebaseAuth.addAuthStateListener(authListener)
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeAuthStateListener(authListener)
        clearStreams()
    }

    private fun clearStreams() {
        dataJobs.forEach { it.cancel() }
        dataJobs.clear()
        tournamentRegJob?.cancel()
        adminJobsStarted = false
        _userProfile.value = null
    }

    fun onGoogleSignedIn(user: FirebaseUser) {
        _currentUser.value = user
        startAuthenticatedStreams(user.uid)
    }

    private fun startAuthenticatedStreams(uid: String) {
        clearStreams()
        viewModelScope.launch {
            _isLoading.value = true
            repository.getOrCreateUserProfile()
                .onSuccess { profile ->
                    _userProfile.value = profile
                    if (profile.role == "ADMIN") {
                        startAdminStreams()
                    }
                }
                .onFailure { e ->
                    showMessage(e.localizedMessage ?: "Failed to load profile", isError = true)
                }
            repository.seedInitialDataIfNeeded()
            _isLoading.value = false
        }

        dataJobs += viewModelScope.launch {
            repository.observeUserProfile(uid)
                .catch { }
                .collect { profile ->
                    if (profile != null) {
                        _userProfile.value = profile
                        if (profile.role == "ADMIN" && !adminJobsStarted) {
                            startAdminStreams()
                        }
                    }
                }
        }

        dataJobs += viewModelScope.launch {
            repository.observePlatformConfig()
                .catch { }
                .collect { _platformConfig.value = it }
        }

        dataJobs += viewModelScope.launch {
            repository.observeTournamentsByStatus("UPCOMING")
                .catch { }
                .collect { _upcomingTournaments.value = it }
        }

        dataJobs += viewModelScope.launch {
            repository.observeTournamentsByStatus("ONGOING")
                .catch { }
                .collect { _ongoingTournaments.value = it }
        }

        dataJobs += viewModelScope.launch {
            repository.observeTournamentsByStatus("COMPLETED")
                .catch { }
                .collect { _completedTournaments.value = it }
        }

        dataJobs += viewModelScope.launch {
            repository.observeUserRegistrations(uid)
                .catch { }
                .collect { _userRegistrations.value = it }
        }

        dataJobs += viewModelScope.launch {
            repository.observeUserWalletTransactions(uid)
                .catch { }
                .collect { _walletTransactions.value = it }
        }

        dataJobs += viewModelScope.launch {
            repository.observeUserSupportTickets(uid)
                .catch { }
                .collect { _supportTickets.value = it }
        }

        dataJobs += viewModelScope.launch {
            repository.observeLeaderboard("DAILY")
                .catch { }
                .collect { _dailyLeaderboard.value = it }
        }

        dataJobs += viewModelScope.launch {
            repository.observeLeaderboard("WEEKLY")
                .catch { }
                .collect { _weeklyLeaderboard.value = it }
        }

        dataJobs += viewModelScope.launch {
            repository.observeLeaderboard("MONTHLY")
                .catch { }
                .collect { _monthlyLeaderboard.value = it }
        }
    }

    private fun startAdminStreams() {
        if (adminJobsStarted) return
        adminJobsStarted = true
        dataJobs += viewModelScope.launch {
            repository.observeAllWalletTransactionsForAdmin()
                .catch { }
                .collect { _adminTransactions.value = it }
        }
        dataJobs += viewModelScope.launch {
            repository.observeAllSupportTicketsForAdmin()
                .catch { }
                .collect { _adminTickets.value = it }
        }
        dataJobs += viewModelScope.launch {
            repository.observeAllUsersForAdmin()
                .catch { }
                .collect { _adminUsers.value = it }
        }
    }

    // --- NAVIGATION & FILTERS ---

    fun selectBottomTab(tab: MainBottomTab) {
        _selectedTab.value = tab
        _overlayScreen.value = ActiveOverlayScreen.NONE
    }

    fun openTournamentList(gameMode: String = "ALL") {
        _selectedGameModeFilter.value = gameMode
        _overlayScreen.value = ActiveOverlayScreen.TOURNAMENT_LIST
    }

    fun setGameModeFilter(gameMode: String) {
        _selectedGameModeFilter.value = gameMode
    }

    fun setTeamSizeFilter(teamSize: String) {
        _selectedTeamSizeFilter.value = teamSize
    }

    fun openTournamentDetail(tournamentId: String) {
        _selectedTournamentId.value = tournamentId
        _overlayScreen.value = ActiveOverlayScreen.TOURNAMENT_DETAIL
        observeTournamentRegistrations(tournamentId)
    }

    fun observeTournamentRegistrations(tournamentId: String) {
        tournamentRegJob?.cancel()
        tournamentRegJob = viewModelScope.launch {
            repository.observeRegistrationsForTournament(tournamentId)
                .catch { }
                .collect { _selectedTournamentRegistrations.value = it }
        }
    }

    fun openMyContests(tab: String = "ONGOING") {
        _selectedMyContestsTab.value = tab
        _overlayScreen.value = ActiveOverlayScreen.MY_CONTESTS
    }

    fun openManualQrScreen(tier: AddCashTier = DEFAULT_ADD_CASH_TIERS[1]) {
        _pendingAddCashTier.value = tier
        _overlayScreen.value = ActiveOverlayScreen.MANUAL_QR_PAY
    }

    fun openOverlay(screen: ActiveOverlayScreen) {
        _overlayScreen.value = screen
    }

    fun navigateBack(): Boolean {
        return if (_overlayScreen.value != ActiveOverlayScreen.NONE) {
            _overlayScreen.value = ActiveOverlayScreen.NONE
            true
        } else if (_selectedTab.value != MainBottomTab.HOME) {
            _selectedTab.value = MainBottomTab.HOME
            true
        } else {
            false
        }
    }

    fun showMessage(msg: String, isError: Boolean = false) {
        _feedbackBanner.value = msg
        _isErrorBanner.value = isError
    }

    fun dismissBanner() {
        _feedbackBanner.value = null
    }

    // --- BILINGUAL HELPER (EN / HINGLISH) ---
    fun tr(en: String, hi: String): String {
        return if (_userProfile.value?.language == "HI") hi else en
    }

    // --- PLAYER ACTIONS ---

    fun updateProfileSettings(
        username: String,
        phone: String,
        ffUid: String,
        ffIgn: String,
        stateName: String,
        kycPanNumber: String,
        dailyDepositLimit: Long,
        language: String,
        importantNoticeEnabled: Boolean,
        selfExclusionHours: Int = 0
    ) {
        val current = _userProfile.value ?: return
        val blockedStates = _platformConfig.value.blockedStatesCsv
            .split(",")
            .map { it.trim().lowercase() }
        if (stateName.trim().lowercase() in blockedStates) {
            showMessage(
                "Compliance Alert: Paid skill tournaments are restricted in $stateName as per state regulations. Free practice matches remain available.",
                isError = true
            )
        }
        val selfExcludedUntil = if (selfExclusionHours > 0) {
            System.currentTimeMillis() + selfExclusionHours * 3600_000L
        } else {
            current.selfExcludedUntil
        }
        val kycStatus = if (kycPanNumber.trim().length == 10) "VERIFIED" else current.kycStatus
        val updated = current.copy(
            username = username.trim().ifBlank { current.username }.take(30),
            phone = phone.trim().take(15),
            ffUid = ffUid.trim().take(20),
            ffIgn = ffIgn.trim().take(30),
            stateName = stateName.trim().take(40),
            kycPanNumber = kycPanNumber.trim().uppercase().take(12),
            kycStatus = kycStatus,
            dailyDepositLimit = dailyDepositLimit.coerceIn(100L, 50000L),
            language = language,
            importantNoticeEnabled = importantNoticeEnabled,
            selfExcludedUntil = selfExcludedUntil
        )
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateUserProfile(updated)
                .onSuccess {
                    _userProfile.value = updated
                    showMessage(tr("Profile & Compliance settings updated!", "Profile aur Compliance settings save ho gayi!"))
                }
                .onFailure { e ->
                    showMessage(e.localizedMessage ?: "Failed to update profile", isError = true)
                }
            _isLoading.value = false
        }
    }

    fun joinTournament(
        tournament: Tournament,
        ffUid: String,
        ffIgn: String,
        teammateInfo: String,
        preferredSlot: Long
    ) {
        val profile = _userProfile.value ?: return
        val config = _platformConfig.value

        if (tournament.entryFee > 0L && !config.paidContestsEnabled) {
            showMessage("Paid contests are temporarily paused by Admin Compliance Switch. Join Free Matches instead!", isError = true)
            return
        }

        val blockedStates = config.blockedStatesCsv.split(",").map { it.trim().lowercase() }
        if (tournament.entryFee > 0L && profile.stateName.trim().lowercase() in blockedStates) {
            showMessage("State Geo-Restriction: Paid tournaments are blocked in ${profile.stateName}. You can play Free Matches.", isError = true)
            return
        }

        if (ffUid.trim().length < 5 || ffIgn.trim().length < 2) {
            showMessage("Please enter a valid Free Fire UID and In-Game Name (IGN).", isError = true)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            // Save FF UID & IGN to profile if updated
            if (profile.ffUid != ffUid.trim() || profile.ffIgn != ffIgn.trim()) {
                repository.updateUserProfile(profile.copy(ffUid = ffUid.trim(), ffIgn = ffIgn.trim()))
            }
            repository.joinTournament(
                tournament = tournament,
                profile = profile.copy(ffUid = ffUid.trim(), ffIgn = ffIgn.trim()),
                ffUid = ffUid,
                ffIgn = ffIgn,
                teammateInfo = teammateInfo,
                preferredSlot = preferredSlot,
                bonusUsagePercent = config.bonusUsagePercent
            ).onSuccess {
                showMessage(tr("Successfully joined ${tournament.title} (Slot #$preferredSlot)!", "${tournament.title} mein Slot #$preferredSlot confirm ho gaya!"))
            }.onFailure { e ->
                showMessage(e.localizedMessage ?: "Could not join tournament", isError = true)
            }
            _isLoading.value = false
        }
    }

    fun submitMatchProofOrDispute(
        registration: Registration,
        proofText: String,
        disputeReason: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.submitMatchProofOrDispute(registration, proofText, disputeReason)
                .onSuccess {
                    showMessage(
                        if (disputeReason.isNotBlank()) "Dispute & POV proof submitted to Admin!"
                        else "Match screenshot/POV proof uploaded!"
                    )
                }
                .onFailure { e ->
                    showMessage(e.localizedMessage ?: "Failed to submit proof", isError = true)
                }
            _isLoading.value = false
        }
    }

    fun submitManualQrUtr(
        amountInr: Long,
        bonusCoins: Long,
        utrNumber: String,
        paymentApp: String
    ) {
        val profile = _userProfile.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.submitManualUtrDeposit(
                profile = profile,
                amountInr = amountInr,
                bonusCoins = bonusCoins,
                utrNumber = utrNumber,
                paymentApp = paymentApp
            ).onSuccess {
                showMessage(
                    tr(
                        "UTR #$utrNumber submitted! Admin Finance Queue will verify & credit ${amountInr + bonusCoins} PlayCoins.",
                        "UTR #$utrNumber jama ho gaya! Admin verify karke ${amountInr + bonusCoins} PlayCoins add karenge."
                    )
                )
                _overlayScreen.value = ActiveOverlayScreen.TRANSACTION_HISTORY
            }.onFailure { e ->
                showMessage(e.localizedMessage ?: "UTR submission failed", isError = true)
            }
            _isLoading.value = false
        }
    }

    fun requestWithdrawal(amountCoins: Long, payoutAddress: String) {
        val profile = _userProfile.value ?: return
        val minCoins = _platformConfig.value.minWithdrawalCoins
        viewModelScope.launch {
            _isLoading.value = true
            repository.requestWithdrawal(profile, amountCoins, payoutAddress, minCoins)
                .onSuccess {
                    showMessage("Withdrawal request for $amountCoins Winning Coins submitted to Finance Queue!")
                    _overlayScreen.value = ActiveOverlayScreen.TRANSACTION_HISTORY
                }
                .onFailure { e ->
                    showMessage(e.localizedMessage ?: "Withdrawal request failed", isError = true)
                }
            _isLoading.value = false
        }
    }

    fun claimOfferBonus(offerKey: String, bonusCoins: Long, offerTitle: String) {
        val profile = _userProfile.value ?: return
        if (offerKey in _claimedOffers.value) {
            showMessage("You have already claimed '$offerTitle' today!", isError = true)
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            repository.claimOfferReward(profile, bonusCoins, offerTitle)
                .onSuccess {
                    _claimedOffers.value = _claimedOffers.value + offerKey
                    showMessage("+$bonusCoins Bonus PlayCoins credited for $offerTitle!")
                }
                .onFailure { e ->
                    showMessage(e.localizedMessage ?: "Could not claim reward", isError = true)
                }
            _isLoading.value = false
        }
    }

    fun createSupportTicket(
        category: String,
        subject: String,
        message: String,
        proofRef: String
    ) {
        val profile = _userProfile.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.createSupportTicket(
                profile = profile,
                category = category,
                subject = subject,
                message = message,
                proofRef = proofRef,
                existingTickets = _supportTickets.value
            ).onSuccess {
                showMessage("Support Ticket created under $category! Our team will respond shortly.")
            }.onFailure { e ->
                showMessage(e.localizedMessage ?: "Could not create ticket", isError = true)
            }
            _isLoading.value = false
        }
    }

    // --- ADMIN CONSOLE OPERATIONS ---

    fun openAndEnableAdminConsole() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.enableOperatorAdminRole()
                .onSuccess {
                    startAdminStreams()
                    _overlayScreen.value = ActiveOverlayScreen.ADMIN_CONSOLE
                    showMessage("Operator Admin Role Activated • Full Audit Trail Enabled")
                }
                .onFailure { e ->
                    showMessage(e.localizedMessage ?: "Failed to activate Admin Console", isError = true)
                }
            _isLoading.value = false
        }
    }

    fun adminVerifyTransaction(tx: WalletTransaction, approve: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.approveOrRejectUtrDeposit(tx, approve)
                .onSuccess {
                    showMessage(if (approve) "Approved & Credited ${tx.amountCoins} Coins to ${tx.username}!" else "Transaction Rejected.")
                }
                .onFailure { e ->
                    showMessage(e.localizedMessage ?: "Verification failed", isError = true)
                }
            _isLoading.value = false
        }
    }

    fun adminCreateNewTournament(tournament: Tournament) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createTournament(tournament)
                .onSuccess {
                    showMessage("Published new tournament: ${tournament.title}")
                }
                .onFailure { e ->
                    showMessage(e.localizedMessage ?: "Failed to create tournament", isError = true)
                }
            _isLoading.value = false
        }
    }

    fun adminPublishRoomCredentials(
        tournament: Tournament,
        roomId: String,
        roomPassword: String,
        newStatus: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.publishRoomCredentials(tournament, roomId, roomPassword, newStatus)
                .onSuccess {
                    showMessage("Updated Room ID ($roomId) & Status ($newStatus) for ${tournament.title}!")
                }
                .onFailure { e ->
                    showMessage(e.localizedMessage ?: "Failed to update room", isError = true)
                }
            _isLoading.value = false
        }
    }

    fun adminCancelTournamentWithAutoRefund(tournament: Tournament) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.cancelTournamentAndRefundAll(tournament)
                .onSuccess { refundedCount ->
                    showMessage("Cancelled ${tournament.title} & auto-refunded $refundedCount registered players.")
                }
                .onFailure { e ->
                    showMessage(e.localizedMessage ?: "Cancel/Refund failed", isError = true)
                }
            _isLoading.value = false
        }
    }

    fun adminSettlePlayerResult(
        tournament: Tournament,
        registration: Registration,
        kills: Long,
        rank: Long
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.settleMatchResultForPlayer(tournament, registration, kills, rank)
                .onSuccess { totalPrize ->
                    showMessage("Credited $totalPrize Winning Coins to ${registration.username} (Rank #$rank, $kills Kills)!")
                }
                .onFailure { e ->
                    showMessage(e.localizedMessage ?: "Failed to settle result", isError = true)
                }
            _isLoading.value = false
        }
    }

    fun adminReplyToTicket(ticket: SupportTicket, reply: String, status: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.replyToSupportTicket(ticket, reply, status)
                .onSuccess {
                    showMessage("Replied to ticket #${ticket.id.take(8)} ($status)")
                }
                .onFailure { e ->
                    showMessage(e.localizedMessage ?: "Failed to reply to ticket", isError = true)
                }
            _isLoading.value = false
        }
    }

    fun adminManageUser(
        targetUser: UserProfile,
        newKycStatus: String,
        newBannedState: Boolean,
        depDelta: Long,
        winDelta: Long,
        bonusDelta: Long,
        reason: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.adminUpdateUserAndAdjustWallet(
                targetUser = targetUser,
                newKycStatus = newKycStatus,
                newBannedState = newBannedState,
                depositedDelta = depDelta,
                winningDelta = winDelta,
                bonusDelta = bonusDelta,
                reason = reason
            ).onSuccess {
                showMessage("Updated user ${targetUser.username} (KYC: $newKycStatus, Banned: $newBannedState)")
            }.onFailure { e ->
                showMessage(e.localizedMessage ?: "Failed to update user", isError = true)
            }
            _isLoading.value = false
        }
    }

    fun adminUpdatePlatformConfig(updated: PlatformConfig) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updatePlatformConfig(updated)
                .onSuccess {
                    _platformConfig.value = updated
                    showMessage("Platform Compliance & Config updated globally!")
                }
                .onFailure { e ->
                    showMessage(e.localizedMessage ?: "Failed to update platform config", isError = true)
                }
            _isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authHelper.signOut()
            _currentUser.value = null
            _overlayScreen.value = ActiveOverlayScreen.NONE
        }
    }
}
