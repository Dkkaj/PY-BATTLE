package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun OriginOsAmbientCrystalBackdrop(
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "originOsAurora")
    val drift by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auroraDrift"
    )

    Canvas(modifier = modifier.fillMaxSize().background(PlaytBgObsidian)) {
        val w = size.width
        val h = size.height

        // Top-Left Crystal Cyan Aurora Orb
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    CrystalAuroraBlue.copy(alpha = 0.20f),
                    CrystalAuroraViolet.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = Offset(w * (0.18f + 0.15f * drift), h * (0.12f + 0.06f * drift)),
                radius = w * 0.72f
            ),
            radius = w * 0.72f,
            center = Offset(w * (0.18f + 0.15f * drift), h * (0.12f + 0.06f * drift))
        )

        // Center-Right OriginOS Rose-Violet Crystal Orb
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    CrystalAuroraRose.copy(alpha = 0.16f),
                    CrystalAuroraViolet.copy(alpha = 0.09f),
                    Color.Transparent
                ),
                center = Offset(w * (0.85f - 0.12f * drift), h * (0.46f - 0.08f * drift)),
                radius = w * 0.68f
            ),
            radius = w * 0.68f,
            center = Offset(w * (0.85f - 0.12f * drift), h * (0.46f - 0.08f * drift))
        )

        // Bottom Spatial Emerald/Blue Crystal Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    CrystalAuroraViolet.copy(alpha = 0.18f),
                    CrystalMint.copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = Offset(w * 0.45f, h * (0.88f - 0.05f * drift)),
                radius = w * 0.75f
            ),
            radius = w * 0.75f,
            center = Offset(w * 0.45f, h * (0.88f - 0.05f * drift))
        )
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

    Box(modifier = Modifier.fillMaxSize()) {
        // Spatial OriginOS 7 Liquid Crystal Ambient Backdrop
        OriginOsAmbientCrystalBackdrop()

        if (currentUser == null) {
            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Playt24AuthScreen(
                        onSignedIn = { user -> viewModel.onGoogleSignedIn(user) }
                    )
                }
            }
            return@Box
        }

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                if (overlayScreen == ActiveOverlayScreen.NONE) {
                    Playt24TopHeader(
                        profile = profile,
                        onWalletClick = { viewModel.selectBottomTab(MainBottomTab.WALLET) },
                        onSupportClick = { viewModel.openOverlay(ActiveOverlayScreen.SUPPORT_CENTER) }
                    )
                }
            },
            bottomBar = {
                if (overlayScreen == ActiveOverlayScreen.NONE) {
                    OriginOsFloatingCrystalDock(
                        selectedTab = selectedTab,
                        onSelectTab = { viewModel.selectBottomTab(it) }
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isLoading) {
                    LinearProgressIndicator(
                        color = CrystalAuroraBlue,
                        trackColor = CrystalGlassElevated,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(3.dp)
                            .clip(CircleShape)
                    )
                }

                // OriginOS 7 Dynamic Island Toast / Atomic Notification Capsule
                AnimatedVisibility(
                    visible = feedbackBanner != null,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Surface(
                        color = CrystalGlassElevated,
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.2.dp,
                            if (isErrorBanner) PlaytDanger else PlaytVerifiedGreen
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable { viewModel.dismissBanner() }
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            (if (isErrorBanner) PlaytDanger else PlaytVerifiedGreen).copy(alpha = 0.22f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .padding(horizontal = 14.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isErrorBanner) PlaytDanger else PlaytVerifiedGreen)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = feedbackBanner ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PlaytTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = PlaytTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // OriginOS 7 Elastic Spring Window Transitions
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = Pair(overlayScreen, selectedTab),
                        transitionSpec = {
                            (scaleIn(
                                initialScale = 0.94f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            ) + fadeIn(animationSpec = tween(220))) togetherWith
                                (scaleOut(
                                    targetScale = 0.97f,
                                    animationSpec = tween(150)
                                ) + fadeOut(animationSpec = tween(150)))
                        },
                        label = "originOs7SpatialTransition"
                    ) { (activeOverlay, activeTab) ->
                        when (activeOverlay) {
                            ActiveOverlayScreen.TOURNAMENT_LIST -> TournamentListScreen(viewModel)
                            ActiveOverlayScreen.TOURNAMENT_DETAIL -> TournamentDetailScreen(viewModel)
                            ActiveOverlayScreen.MY_CONTESTS -> MyContestsScreen(viewModel)
                            ActiveOverlayScreen.MANUAL_QR_PAY -> ManualQrAddMoneyScreen(viewModel)
                            ActiveOverlayScreen.WITHDRAW_COINS -> WithdrawCoinsScreen(viewModel)
                            ActiveOverlayScreen.TRANSACTION_HISTORY -> TransactionHistoryScreen(viewModel)
                            ActiveOverlayScreen.SUPPORT_CENTER -> SupportCenterScreen(viewModel)
                            ActiveOverlayScreen.COMPLIANCE_LEGAL -> ComplianceAndGrievanceScreen(viewModel)
                            ActiveOverlayScreen.NONE -> {
                                when (activeTab) {
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
    }
}

@Composable
private fun OriginOsFloatingCrystalDock(
    selectedTab: MainBottomTab,
    onSelectTab: (MainBottomTab) -> Unit
) {
    val items = listOf(
        Triple(MainBottomTab.OFFER, "Offers", Icons.Default.AutoAwesome),
        Triple(MainBottomTab.RANKING, "Podium", Icons.Default.EmojiEvents),
        Triple(MainBottomTab.HOME, "Arena", Icons.Default.SportsEsports),
        Triple(MainBottomTab.WALLET, "Vault", Icons.Default.AccountBalanceWallet),
        Triple(MainBottomTab.PROFILE, "Space", Icons.Default.Person)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = CrystalGlassSurface,
            shape = RoundedCornerShape(32.dp),
            tonalElevation = 16.dp,
            shadowElevation = 20.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.2.dp,
                brush = crystalSpecularBorderBrush(CrystalAuroraBlue)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.12f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { (tab, label, icon) ->
                    val isSelected = selectedTab == tab
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.06f else 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "dockScale"
                    )
                    val activeAccent = when (tab) {
                        MainBottomTab.HOME -> PlaytCrimson
                        MainBottomTab.WALLET -> PlaytVerifiedGreen
                        MainBottomTab.RANKING -> PlaytCoinGold
                        else -> CrystalAuroraBlue
                    }

                    Box(
                        modifier = Modifier
                            .scale(scale)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                if (isSelected) {
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.22f),
                                            activeAccent.copy(alpha = 0.24f)
                                        )
                                    )
                                } else {
                                    Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                                }
                            )
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        width = 1.dp,
                                        brush = Brush.verticalGradient(
                                            listOf(CrystalSpecularHighlight, activeAccent.copy(alpha = 0.5f))
                                        ),
                                        shape = RoundedCornerShape(22.dp)
                                    )
                                } else Modifier
                            )
                            .clickable { onSelectTab(tab) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("bottom_nav_${label.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) Color.White else PlaytTextSecondary,
                                modifier = Modifier.size(21.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) Color.White else PlaytTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
