package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ActiveOverlayScreen
import com.example.ui.MainBottomTab
import com.example.ui.Playt24ViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Playt24Theme {
                Playt24RootApp()
            }
        }
    }
}

@Composable
fun Playt24RootApp(
    viewModel: Playt24ViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val overlayScreen by viewModel.overlayScreen.collectAsState()
    val feedbackBanner by viewModel.feedbackBanner.collectAsState()
    val isErrorBanner by viewModel.isErrorBanner.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    BackHandler(enabled = overlayScreen != ActiveOverlayScreen.NONE || selectedTab != MainBottomTab.HOME) {
        viewModel.navigateBack()
    }

    if (currentUser == null) {
        Scaffold(
            containerColor = PlaytBgObsidian,
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Playt24AuthScreen(
                    onSignedIn = { user -> viewModel.onGoogleSignedIn(user) }
                )
            }
        }
        return
    }

    Scaffold(
        containerColor = PlaytBgObsidian,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (overlayScreen == ActiveOverlayScreen.NONE) {
                Playt24TopHeader(
                    profile = profile,
                    onWalletClick = { viewModel.selectBottomTab(MainBottomTab.WALLET) },
                    onAdminClick = { viewModel.openAndEnableAdminConsole() },
                    onSupportClick = { viewModel.openOverlay(ActiveOverlayScreen.SUPPORT_CENTER) }
                )
            }
        },
        bottomBar = {
            if (overlayScreen == ActiveOverlayScreen.NONE) {
                NavigationBar(
                    containerColor = PlaytSurfaceDark,
                    tonalElevation = 8.dp
                ) {
                    val items = listOf(
                        Triple(MainBottomTab.OFFER, "Offer", Icons.Default.CardGiftcard),
                        Triple(MainBottomTab.RANKING, "Ranking", Icons.Default.Leaderboard),
                        Triple(MainBottomTab.HOME, "Home", Icons.Default.Home),
                        Triple(MainBottomTab.WALLET, "Wallet", Icons.Default.AccountBalanceWallet),
                        Triple(MainBottomTab.PROFILE, "Profile", Icons.Default.Person)
                    )
                    items.forEach { (tab, label, icon) ->
                        val isSelected = selectedTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectBottomTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PlaytCrimson,
                                selectedTextColor = PlaytCrimson,
                                indicatorColor = PlaytCrimson.copy(alpha = 0.16f),
                                unselectedIconColor = PlaytTextSecondary,
                                unselectedTextColor = PlaytTextSecondary
                            ),
                            modifier = Modifier.testTag("bottom_nav_${label.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PlaytBgObsidian)
        ) {
            if (isLoading) {
                LinearProgressIndicator(
                    color = PlaytCoinGold,
                    trackColor = PlaytSurfaceElevated,
                    modifier = Modifier.fillMaxWidth().height(3.dp)
                )
            }

            AnimatedVisibility(visible = feedbackBanner != null) {
                Surface(
                    color = if (isErrorBanner) PlaytDanger else PlaytVerifiedGreen,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable { viewModel.dismissBanner() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = feedbackBanner ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (overlayScreen) {
                    ActiveOverlayScreen.TOURNAMENT_LIST -> TournamentListScreen(viewModel)
                    ActiveOverlayScreen.TOURNAMENT_DETAIL -> TournamentDetailScreen(viewModel)
                    ActiveOverlayScreen.MY_CONTESTS -> MyContestsScreen(viewModel)
                    ActiveOverlayScreen.MANUAL_QR_PAY -> ManualQrAddMoneyScreen(viewModel)
                    ActiveOverlayScreen.WITHDRAW_COINS -> WithdrawCoinsScreen(viewModel)
                    ActiveOverlayScreen.TRANSACTION_HISTORY -> TransactionHistoryScreen(viewModel)
                    ActiveOverlayScreen.SUPPORT_CENTER -> SupportCenterScreen(viewModel)
                    ActiveOverlayScreen.ADMIN_CONSOLE -> AdminConsoleScreen(viewModel)
                    ActiveOverlayScreen.COMPLIANCE_LEGAL -> ComplianceAndGrievanceScreen(viewModel)
                    ActiveOverlayScreen.NONE -> {
                        when (selectedTab) {
                            MainBottomTab.OFFER -> Playt24OffersTab(viewModel)
                            MainBottomTab.RANKING -> Playt24RankingTab(viewModel)
                            MainBottomTab.HOME -> Playt24HomeTab(viewModel)
                            MainBottomTab.WALLET -> Playt24WalletTab(viewModel)
                            MainBottomTab.PROFILE -> Playt24ProfileTab(viewModel)
                        }
                    }
                }
            }
        }
    }
}
