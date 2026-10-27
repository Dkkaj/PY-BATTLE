package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.auth.GoogleAuthHelper
import com.example.data.*
import com.example.ui.ActiveOverlayScreen
import com.example.ui.MainBottomTab
import com.example.ui.Playt24ViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Playt24AuthScreen(
    onSignedIn: (com.google.firebase.auth.FirebaseUser) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authHelper = remember { GoogleAuthHelper(context) }

    var ageConfirmed by remember { mutableStateOf(true) }
    var skillComplianceAccepted by remember { mutableStateOf(true) }
    var selectedState by remember { mutableStateOf("Maharashtra") }
    var isSigningIn by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val indianStates = listOf(
        "Maharashtra", "Delhi NCR", "Uttar Pradesh", "Karnataka",
        "Gujarat", "Rajasthan", "West Bengal", "Bihar", "Madhya Pradesh",
        "Punjab", "Haryana", "Tamil Nadu", "Assam (Restricted)", "Telangana (Restricted)"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "crystalOrb")
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // OriginOS 7 Floating Crystal Icon Sphere
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(bottom = 14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(134.dp)
                    .scale(orbScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                CrystalAuroraBlue.copy(alpha = 0.35f),
                                CrystalAuroraViolet.copy(alpha = 0.16f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Image(
                painter = painterResource(id = R.drawable.img_pybattle_logo),
                contentDescription = "PYBattle Crystal Crest",
                modifier = Modifier
                    .size(104.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .border(
                        width = 1.8.dp,
                        brush = crystalSpecularBorderBrush(CrystalAuroraBlue),
                        shape = RoundedCornerShape(30.dp)
                    ),
                contentScale = ContentScale.Crop
            )
        }

        // OriginOS 7 Dynamic Capsule Tag
        Surface(
            color = CrystalGlassElevated,
            shape = RoundedCornerShape(50),
            border = androidx.compose.foundation.BorderStroke(1.dp, crystalSpecularBorderBrush(CrystalAuroraBlue))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CrystalAuroraBlue,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ORIGIN CRYSTAL • ESPORTS OS 7",
                    style = MaterialTheme.typography.labelSmall,
                    color = PlaytTextPrimary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "PYBattle",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            ),
            color = PlaytTextPrimary
        )
        Text(
            text = "SPATIAL FREE FIRE & FF MAX TOURNAMENT HUB",
            style = MaterialTheme.typography.labelMedium,
            color = CrystalAuroraBlue,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        // OriginOS 7 Frosted Crystal Compliance Widget
        Surface(
            color = CrystalGlassSurface,
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.2.dp,
                crystalSpecularBorderBrush(CrystalAuroraViolet)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .background(crystalSurfaceBrush(CrystalAuroraViolet))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Esports Compliance Shield",
                            tint = PlaytVerifiedGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SPATIAL CLEARANCE GATE",
                            style = MaterialTheme.typography.titleSmall,
                            color = PlaytTextPrimary,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Surface(
                        color = Color.White.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(50),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSpecularHighlight)
                    ) {
                        Text(
                            text = "18+ SKILL",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytVerifiedGreen,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Select Home State (Geo-Compliance)",
                    style = MaterialTheme.typography.labelMedium,
                    color = PlaytTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    indianStates.forEach { stateItem ->
                        val isSelected = selectedState == stateItem
                        Surface(
                            color = if (isSelected) CrystalAuroraBlue.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.06f),
                            shape = RoundedCornerShape(50),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CrystalAuroraBlue else CrystalSubtleEdge
                            ),
                            modifier = Modifier.clickable { selectedState = stateItem }
                        ) {
                            Text(
                                text = stateItem,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White else PlaytTextSecondary,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .clickable { ageConfirmed = !ageConfirmed }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Checkbox(
                        checked = ageConfirmed,
                        onCheckedChange = { ageConfirmed = it },
                        colors = CheckboxDefaults.colors(checkedColor = CrystalAuroraBlue),
                        modifier = Modifier.testTag("auth_age_checkbox")
                    )
                    Text(
                        text = "I confirm I am 18+ years old and eligible for skill-based esports tournaments in my state.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaytTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .clickable { skillComplianceAccepted = !skillComplianceAccepted }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Checkbox(
                        checked = skillComplianceAccepted,
                        onCheckedChange = { skillComplianceAccepted = it },
                        colors = CheckboxDefaults.colors(checkedColor = PlaytVerifiedGreen),
                        modifier = Modifier.testTag("auth_skill_checkbox")
                    )
                    Text(
                        text = "I agree to PYBattle Fair-Play Rules: fixed prize pools, zero betting/odds, and zero emulator/hack usage.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaytTextPrimary
                    )
                }
            }
        }

        if (errorMsg != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = PlaytDanger.copy(alpha = 0.18f),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PlaytDanger),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMsg ?: "",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // OriginOS 7 Liquid Crystal Sign-In Pill Button
        Surface(
            color = if (ageConfirmed && skillComplianceAccepted && !isSigningIn) {
                PlaytCrimson
            } else {
                CrystalGlassElevated
            },
            shape = RoundedCornerShape(28.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.2.dp,
                crystalSpecularBorderBrush(PlaytCrimson)
            ),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clip(RoundedCornerShape(28.dp))
                .clickable(enabled = ageConfirmed && skillComplianceAccepted && !isSigningIn) {
                    val activity = context as? Activity
                    if (activity == null) {
                        errorMsg = "Activity context unavailable for Google Sign-In."
                        return@clickable
                    }
                    isSigningIn = true
                    errorMsg = null
                    scope.launch {
                        authHelper.signIn(activity)
                            .onSuccess { user ->
                                isSigningIn = false
                                onSignedIn(user)
                            }
                            .onFailure { err ->
                                isSigningIn = false
                                errorMsg = err.localizedMessage ?: "Sign-in failed. Please try again."
                            }
                    }
                }
                .testTag("google_sign_in_button")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.18f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.10f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSigningIn) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "AUTHENTICATING...",
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "CONTINUE WITH GOOGLE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Playt24TopHeader(
    profile: UserProfile?,
    onWalletClick: () -> Unit,
    onSupportClick: () -> Unit
) {
    val totalCoins = (profile?.depositedCoins ?: 0L) +
        (profile?.winningCoins ?: 0L) +
        (profile?.bonusCoins ?: 0L)

    // OriginOS 7 Floating Crystal Island Header
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Surface(
            color = CrystalGlassSurface,
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 12.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.1.dp,
                brush = crystalSpecularBorderBrush(CrystalAuroraBlue)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.10f),
                                Color.Transparent,
                                CrystalAuroraBlue.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Crystal Squircle Logo + OriginOS Atomic Status Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Image(
                            painter = painterResource(id = R.drawable.img_pybattle_logo),
                            contentDescription = "PYBattle Logo",
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .border(1.2.dp, CrystalSpecularHighlight, RoundedCornerShape(15.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .clip(CircleShape)
                                .background(PlaytVerifiedGreen)
                                .border(1.5.dp, PlaytBgObsidian, CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PYBattle",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = PlaytTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Color.White.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(50),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSpecularHighlight)
                            ) {
                                Text(
                                    text = "LV.${((profile?.matchesPlayed ?: 0L) / 2L) + 1L}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CrystalAuroraBlue,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = profile?.ffIgn?.ifBlank { profile.username } ?: "Crystal Player",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Right: Frosted Support Orb + OriginOS Liquid Crystal Wallet Capsule
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrystalSpecularHighlight),
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { onSupportClick() }
                            .testTag("top_support_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = "Support Desk",
                                tint = CrystalAuroraBlue,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(50),
                        border = androidx.compose.foundation.BorderStroke(
                            1.2.dp,
                            Brush.horizontalGradient(listOf(CrystalSpecularHighlight, PlaytCoinGold))
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { onWalletClick() }
                            .testTag("top_wallet_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(listOf(PlaytCoinGold, PlaytOrange))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "₹",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$totalCoins",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(19.dp)
                                    .clip(CircleShape)
                                    .background(CrystalAuroraBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Coins",
                                    tint = Color.Black,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Playt24HomeTab(
    viewModel: Playt24ViewModel
) {
    val upcoming by viewModel.upcomingTournaments.collectAsState()
    val ongoing by viewModel.ongoingTournaments.collectAsState()
    val completed by viewModel.completedTournaments.collectAsState()
    val registrations by viewModel.userRegistrations.collectAsState()
    val config by viewModel.platformConfig.collectAsState()

    val banners = listOf(
        Triple(
            R.drawable.img_pybattle_banner_booyah,
            "₹1 ENTRY -> ₹10,000 MEGA BOOYAH",
            "Bermuda Full Map Survival • Instant Prize Pool"
        ),
        Triple(
            R.drawable.img_pybattle_banner_add_cash,
            "CRYSTAL VAULT: +70 BONUS COINS",
            "Scan UPI QR & Submit 12-Digit UTR for Admin Approval"
        ),
        Triple(
            R.drawable.img_pybattle_banner_leaderboard,
            "HALL OF CHAMPIONS LEADERBOARD",
            "Daily, Weekly & Monthly Verified Podium Rewards"
        ),
        Triple(
            R.drawable.img_pybattle_banner_telegram,
            "ROOM ID UNLOCKS 10M BEFORE START",
            "Instant Custom Room Credentials Inside Match Bunker"
        )
    )
    var activeBannerIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000L)
            activeBannerIndex = (activeBannerIndex + 1) % banners.size
        }
    }

    val ongoingJoinedCount = registrations.count { reg ->
        ongoing.any { it.id == reg.tournamentId }
    }
    val upcomingJoinedCount = registrations.count { reg ->
        upcoming.any { it.id == reg.tournamentId }
    }
    val completedJoinedCount = registrations.count { reg ->
        completed.any { it.id == reg.tournamentId } || reg.status == "PLAYED"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_lazy_column"),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        // 1. OriginOS 7 Atomic Broadcast Capsule
        item {
            Surface(
                color = CrystalGlassSurface,
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    crystalSpecularBorderBrush(CrystalAuroraViolet)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = PlaytCrimson.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "NOTICE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = config.announcementTicker,
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaytTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. OriginOS 7 Spatial Hero Window Card (28.dp Crystal Squircle)
        item {
            val (bannerRes, bannerHeadline, bannerSub) = banners[activeBannerIndex]
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.4.dp,
                        crystalSpecularBorderBrush(CrystalAuroraBlue)
                    ),
                    shadowElevation = 18.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(182.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .clickable {
                            when (activeBannerIndex) {
                                0 -> viewModel.openTournamentList("FULL_MAP")
                                1 -> viewModel.selectBottomTab(MainBottomTab.WALLET)
                                2 -> viewModel.selectBottomTab(MainBottomTab.RANKING)
                                else -> viewModel.openOverlay(ActiveOverlayScreen.SUPPORT_CENTER)
                            }
                        }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AnimatedContent(
                            targetState = bannerRes,
                            label = "crystalHeroBanner"
                        ) { targetRes ->
                            Image(
                                painter = painterResource(id = targetRes),
                                contentDescription = bannerHeadline,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        // Frosted glass bottom sheet overlay inside hero card
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0x66060913),
                                            Color(0xEB080D1D)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(18.dp)
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(50),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSpecularHighlight)
                            ) {
                                Text(
                                    text = "SPATIAL SPOTLIGHT #${activeBannerIndex + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = bannerHeadline,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = bannerSub,
                                style = MaterialTheme.typography.bodySmall,
                                color = PlaytTextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    banners.indices.forEach { idx ->
                        val isSelected = idx == activeBannerIndex
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .height(6.dp)
                                .width(if (isSelected) 24.dp else 7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) CrystalAuroraBlue else Color.White.copy(alpha = 0.22f)
                                )
                                .clickable { activeBannerIndex = idx }
                        )
                    }
                }
            }
        }

        // 3. OriginOS 7 Atomic Widgets Row (Quick Actions + My Contests)
        item {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OriginOsAtomicStatusTile(
                        title = "Ongoing",
                        subtitle = "Live Rooms",
                        count = ongoingJoinedCount,
                        accentColor = PlaytCrimson,
                        icon = Icons.Default.Whatshot,
                        onClick = { viewModel.openMyContests("ONGOING") },
                        modifier = Modifier.weight(1f)
                    )
                    OriginOsAtomicStatusTile(
                        title = "Upcoming",
                        subtitle = "Joined Slots",
                        count = upcomingJoinedCount,
                        accentColor = CrystalAuroraBlue,
                        icon = Icons.Default.Schedule,
                        onClick = { viewModel.openMyContests("UPCOMING") },
                        modifier = Modifier.weight(1f)
                    )
                    OriginOsAtomicStatusTile(
                        title = "Results",
                        subtitle = "Booyah Log",
                        count = completedJoinedCount,
                        accentColor = PlaytVerifiedGreen,
                        icon = Icons.Default.MilitaryTech,
                        onClick = { viewModel.openMyContests("COMPLETED") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. OriginOS 7 Spatial Bento Arena Cards
        item {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.tr("CRYSTAL ARENA MODES", "ESPORTS MODE CHUNEIN"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = PlaytTextPrimary
                    )
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(50),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSpecularHighlight)
                    ) {
                        Text(
                            text = "${upcoming.size + ongoing.size} Live Lobbies",
                            style = MaterialTheme.typography.labelSmall,
                            color = CrystalAuroraBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CrystalArenaPosterCard(
                        title = "FF MAX FULL MAP",
                        subtitle = "Bermuda • Kalahari • 48 Slots",
                        badge = "PRO POOL",
                        badgeColor = PlaytCrimson,
                        imageRes = R.drawable.img_card_ff_max,
                        matchCount = upcoming.count { it.gameMode == "FULL_MAP" },
                        onClick = { viewModel.openTournamentList("FULL_MAP") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mode_card_full_map")
                    )
                    CrystalArenaPosterCard(
                        title = "FREE MATCH",
                        subtitle = "₹0 Entry • Daily Skill Scrims",
                        badge = "ZERO FEE",
                        badgeColor = PlaytVerifiedGreen,
                        imageRes = R.drawable.img_card_free_match,
                        matchCount = upcoming.count { it.gameMode == "FREE_MATCH" || it.entryFee == 0L },
                        onClick = { viewModel.openTournamentList("FREE_MATCH") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mode_card_free_match")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OriginOsGlassModeChip(
                        title = "Clash Squad 4v4",
                        subtitle = "Custom Room • Unlimited Ammo",
                        accent = PlaytCoinGold,
                        icon = Icons.Default.Groups,
                        onClick = { viewModel.openTournamentList("CLASH_SQUAD") },
                        modifier = Modifier.weight(1f)
                    )
                    OriginOsGlassModeChip(
                        title = "Lone Wolf 1v1",
                        subtitle = "Headshot Duel • Iron Cage",
                        accent = CrystalAuroraBlue,
                        icon = Icons.Default.GpsFixed,
                        onClick = { viewModel.openTournamentList("LONE_WOLF") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 5. Featured Tournament Crystal Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = viewModel.tr("FEATURED TOURNAMENTS", "LIVE AUR UPCOMING MATCHES"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = PlaytTextPrimary
                )
                TextButton(onClick = { viewModel.openTournamentList("ALL") }) {
                    Text(
                        text = "View All >",
                        color = CrystalAuroraBlue,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        val combinedFeatured = (ongoing + upcoming).take(6)
        items(combinedFeatured, key = { it.id }) { tourney ->
            val joinedReg = registrations.find { it.tournamentId == tourney.id }
            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)) {
                TournamentBattleCard(
                    tournament = tourney,
                    joinedRegistration = joinedReg,
                    onClick = { viewModel.openTournamentDetail(tourney.id) }
                )
            }
        }
    }
}

@Composable
private fun OriginOsAtomicStatusTile(
    title: String,
    subtitle: String,
    count: Int,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CrystalGlassSurface,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.1.dp,
            crystalSpecularBorderBrush(accentColor)
        ),
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(crystalSurfaceBrush(accentColor))
                .padding(vertical = 13.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.20f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = PlaytTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = PlaytTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun CrystalArenaPosterCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    imageRes: Int,
    matchCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CrystalGlassSurface,
        shape = RoundedCornerShape(26.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.3.dp,
            crystalSpecularBorderBrush(badgeColor)
        ),
        shadowElevation = 14.dp,
        modifier = modifier
            .height(210.dp)
            .clip(RoundedCornerShape(26.dp))
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x1A060913),
                                Color(0x99060913),
                                Color(0xF2080E1E)
                            )
                        )
                    )
            )
            Surface(
                color = Color.White.copy(alpha = 0.18f),
                shape = RoundedCornerShape(50),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSpecularHighlight),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = PlaytTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "$matchCount Lobbies",
                        style = MaterialTheme.typography.labelSmall,
                        color = PlaytCoinGold,
                        fontWeight = FontWeight.Black
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OriginOsGlassModeChip(
    title: String,
    subtitle: String,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CrystalGlassSurface,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.1.dp, crystalSpecularBorderBrush(accent)),
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(crystalSurfaceBrush(accent))
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.20f))
                    .border(0.8.dp, CrystalSpecularHighlight, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = PlaytTextPrimary,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = PlaytTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TournamentBattleCard(
    tournament: Tournament,
    joinedRegistration: Registration?,
    onClick: () -> Unit
) {
    val fillFraction = if (tournament.totalSlots > 0) {
        (tournament.filledSlots.toFloat() / tournament.totalSlots.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val spotsLeft = (tournament.totalSlots - tournament.filledSlots).coerceAtLeast(0L)
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val formattedStart = remember(tournament.startTimeEpochMillis) {
        timeFormat.format(Date(tournament.startTimeEpochMillis))
    }
    val isLive = tournament.status == "ONGOING"
    val cardAccent = when {
        joinedRegistration != null -> PlaytVerifiedGreen
        isLive -> PlaytCrimson
        else -> CrystalAuroraBlue
    }

    // OriginOS 7 Translucent Crystal Tournament Widget
    Surface(
        color = CrystalGlassSurface,
        shape = RoundedCornerShape(26.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.2.dp,
            brush = crystalSpecularBorderBrush(cardAccent)
        ),
        shadowElevation = 12.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .clickable { onClick() }
            .testTag("tournament_card_${tournament.id}")
    ) {
        Column(
            modifier = Modifier
                .background(crystalSurfaceBrush(cardAccent))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(50),
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSpecularHighlight)
                    ) {
                        Text(
                            text = tournament.gameMode.replace("_", " "),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        color = CrystalAuroraBlue.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "${tournament.mapName} • ${tournament.teamSize}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CrystalAuroraBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                }

                Surface(
                    color = if (isLive) PlaytCrimson else Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSpecularHighlight)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isLive) Icons.Default.Sensors else Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = if (isLive) Color.White else PlaytCoinGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isLive) "LIVE" else formattedStart,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isLive) Color.White else PlaytCoinGold,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tournament.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = PlaytTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Frosted Inner Crystal Telemetry Bar
            Surface(
                color = Color.White.copy(alpha = 0.06f),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSubtleEdge),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PRIZE POOL",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytTextSecondary
                        )
                        Text(
                            text = "₹${tournament.prizePool}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = PlaytCoinGold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(26.dp)
                            .background(CrystalSubtleEdge)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PER KILL",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytTextSecondary
                        )
                        Text(
                            text = "₹${tournament.perKillPrize}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = CrystalAuroraBlue
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(26.dp)
                            .background(CrystalSubtleEdge)
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "BOOYAH #1",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytTextSecondary
                        )
                        Text(
                            text = "₹${tournament.booyahPrize}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = PlaytVerifiedGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (spotsLeft == 0L) "Lobby Full" else "$spotsLeft Spots Left",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (spotsLeft <= 5L) PlaytCrimson else PlaytTextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${tournament.filledSlots}/${tournament.totalSlots}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytTextPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { fillFraction },
                        color = if (spotsLeft <= 5L) PlaytCrimson else CrystalAuroraBlue,
                        trackColor = Color.White.copy(alpha = 0.10f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // OriginOS Pill Action Button
                Surface(
                    color = when {
                        joinedRegistration != null -> PlaytVerifiedGreen
                        tournament.entryFee == 0L -> CrystalAuroraBlue
                        else -> PlaytCrimson
                    },
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrystalSpecularHighlight),
                    modifier = Modifier.clickable { onClick() }
                ) {
                    Text(
                        text = when {
                            joinedRegistration != null -> "SLOT #${joinedRegistration.slotNumber}"
                            tournament.entryFee == 0L -> "JOIN FREE"
                            else -> "JOIN ₹${tournament.entryFee}"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        color = if (joinedRegistration != null || tournament.entryFee == 0L) Color.Black else Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }

            if (joinedRegistration != null && tournament.roomPublished && tournament.roomId.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = PlaytVerifiedGreen.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PlaytVerifiedGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ROOM ID: ${tournament.roomId}  •  PASS: ${tournament.roomPassword}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = PlaytVerifiedGreen
                        )
                        Text(
                            text = "OPEN",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TournamentListScreen(
    viewModel: Playt24ViewModel
) {
    val upcoming by viewModel.upcomingTournaments.collectAsState()
    val ongoing by viewModel.ongoingTournaments.collectAsState()
    val registrations by viewModel.userRegistrations.collectAsState()
    val modeFilter by viewModel.selectedGameModeFilter.collectAsState()
    val teamFilter by viewModel.selectedTeamSizeFilter.collectAsState()

    BackHandler { viewModel.navigateBack() }

    val allMatches = remember(upcoming, ongoing, modeFilter, teamFilter) {
        (ongoing + upcoming).filter { t ->
            val modeOk = modeFilter == "ALL" ||
                t.gameMode == modeFilter ||
                (modeFilter == "FREE_MATCH" && t.entryFee == 0L)
            val teamOk = teamFilter == "ALL" || t.teamSize == teamFilter
            modeOk && teamOk
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OverlayTopAppBar(
            title = "CRYSTAL TOURNAMENT LOBBY",
            onBack = { viewModel.navigateBack() }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "ALL" to "All Modes",
                "FULL_MAP" to "FF MAX Full Map",
                "CLASH_SQUAD" to "Clash Squad 4v4",
                "LONE_WOLF" to "Lone Wolf 1v1",
                "FREE_MATCH" to "Free Scrims"
            ).forEach { (key, label) ->
                val selected = modeFilter == key
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.setGameModeFilter(key) },
                    label = {
                        Text(
                            text = label,
                            fontWeight = if (selected) FontWeight.Black else FontWeight.Medium
                        )
                    },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CrystalAuroraBlue,
                        selectedLabelColor = Color.Black,
                        containerColor = CrystalGlassSurface,
                        labelColor = PlaytTextSecondary
                    )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ALL", "SOLO", "DUO", "SQUAD").forEach { size ->
                val selected = teamFilter == size
                AssistChip(
                    onClick = { viewModel.setTeamSizeFilter(size) },
                    shape = RoundedCornerShape(50),
                    label = {
                        Text(
                            text = size,
                            fontWeight = FontWeight.Black,
                            color = if (selected) Color.Black else PlaytTextSecondary
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (selected) PlaytCoinGold else CrystalGlassSurface
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(allMatches, key = { it.id }) { tourney ->
                val joinedReg = registrations.find { it.tournamentId == tourney.id }
                TournamentBattleCard(
                    tournament = tourney,
                    joinedRegistration = joinedReg,
                    onClick = { viewModel.openTournamentDetail(tourney.id) }
                )
            }
        }
    }
}

@Composable
fun TournamentDetailScreen(
    viewModel: Playt24ViewModel
) {
    val context = LocalContext.current
    val tourneyId by viewModel.selectedTournamentId.collectAsState()
    val upcoming by viewModel.upcomingTournaments.collectAsState()
    val ongoing by viewModel.ongoingTournaments.collectAsState()
    val completed by viewModel.completedTournaments.collectAsState()
    val userRegs by viewModel.userRegistrations.collectAsState()
    val slotRegs by viewModel.selectedTournamentRegistrations.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val config by viewModel.platformConfig.collectAsState()

    BackHandler { viewModel.navigateBack() }

    val tournament = remember(tourneyId, upcoming, ongoing, completed) {
        (ongoing + upcoming + completed).find { it.id == tourneyId }
    } ?: return

    val myReg = userRegs.find { it.tournamentId == tournament.id }
    val takenSlotsMap = remember(slotRegs) {
        slotRegs.associateBy { it.slotNumber }
    }

    var selectedSlot by remember(takenSlotsMap) {
        val firstFree = (1L..tournament.totalSlots).firstOrNull { !takenSlotsMap.containsKey(it) } ?: 1L
        mutableLongStateOf(firstFree)
    }
    var showJoinModal by remember { mutableStateOf(false) }
    var ffUidInput by remember(profile) { mutableStateOf(profile?.ffUid ?: "") }
    var ffIgnInput by remember(profile) { mutableStateOf(profile?.ffIgn ?: "") }
    var teammateInput by remember { mutableStateOf("") }

    var proofUrlInput by remember(myReg) { mutableStateOf(myReg?.proofText ?: "") }
    var disputeReasonInput by remember(myReg) { mutableStateOf(myReg?.disputeReason ?: "") }

    val bonusUsable = ((tournament.entryFee * config.bonusUsagePercent) / 100L)
        .coerceAtMost(profile?.bonusCoins ?: 0L)
    val remainingMainFee = (tournament.entryFee - bonusUsable).coerceAtLeast(0L)

    Column(modifier = Modifier.fillMaxSize()) {
        OverlayTopAppBar(
            title = tournament.title,
            onBack = { viewModel.navigateBack() }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Crystal Match Summary Widget
            item {
                Surface(
                    color = CrystalGlassSurface,
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.3.dp,
                        crystalSpecularBorderBrush(CrystalAuroraBlue)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(crystalSurfaceBrush(CrystalAuroraBlue))
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.10f),
                                shape = RoundedCornerShape(50),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSpecularHighlight)
                            ) {
                                Text(
                                    text = "${tournament.gameMode} • ${tournament.mapName} • ${tournament.teamSize}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                color = PlaytVerifiedGreen.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = tournament.status,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PlaytVerifiedGreen,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("TOTAL PRIZE POOL", style = MaterialTheme.typography.labelSmall, color = PlaytTextSecondary)
                                Text(
                                    text = "₹${tournament.prizePool} PlayCoins",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Black,
                                    color = PlaytCoinGold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("ENTRY FEE", style = MaterialTheme.typography.labelSmall, color = PlaytTextSecondary)
                                Text(
                                    text = if (tournament.entryFee == 0L) "FREE" else "₹${tournament.entryFee}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Black,
                                    color = CrystalAuroraBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PrizeBreakdownPill("BOOYAH #1", "₹${tournament.booyahPrize}", PlaytCoinGold, Modifier.weight(1f))
                            PrizeBreakdownPill("RANK #2", "₹${tournament.rank2Prize}", CrystalAuroraBlue, Modifier.weight(1f))
                            PrizeBreakdownPill("RANK #3", "₹${tournament.rank3Prize}", PlaytVerifiedGreen, Modifier.weight(1f))
                            PrizeBreakdownPill("PER KILL", "₹${tournament.perKillPrize}", PlaytCrimson, Modifier.weight(1f))
                        }
                    }
                }
            }

            // 2. Crystal Custom Room Vault
            item {
                Surface(
                    color = CrystalGlassSurface,
                    shape = RoundedCornerShape(26.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.1.dp,
                        crystalSpecularBorderBrush(
                            if (myReg != null && tournament.roomPublished) PlaytVerifiedGreen else PlaytCoinGold
                        )
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(crystalSurfaceBrush(PlaytCoinGold))
                            .padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (myReg != null && tournament.roomPublished) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (myReg != null && tournament.roomPublished) PlaytVerifiedGreen else PlaytCoinGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CUSTOM ROOM CREDENTIALS",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = PlaytTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (myReg == null) {
                            Text(
                                text = "Join a slot below to unlock the Free Fire Custom Room ID & Password.",
                                style = MaterialTheme.typography.bodySmall,
                                color = PlaytTextSecondary
                            )
                        } else if (!tournament.roomPublished || tournament.roomId.isBlank()) {
                            Text(
                                text = "Slot #${myReg.slotNumber} Confirmed! Room ID & Password appear here 10 minutes before start.",
                                style = MaterialTheme.typography.bodySmall,
                                color = PlaytCoinGold
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CrystalSpecularHighlight),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            cb.setPrimaryClip(ClipData.newPlainText("Room ID", tournament.roomId))
                                            viewModel.showMessage("Copied Room ID: ${tournament.roomId}")
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("ROOM ID (TAP TO COPY)", style = MaterialTheme.typography.labelSmall, color = PlaytTextSecondary)
                                        Text(
                                            text = tournament.roomId,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black,
                                            color = PlaytVerifiedGreen
                                        )
                                    }
                                }
                                Surface(
                                    color = Color.White.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CrystalSpecularHighlight),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            cb.setPrimaryClip(ClipData.newPlainText("Room Password", tournament.roomPassword))
                                            viewModel.showMessage("Copied Password: ${tournament.roomPassword}")
                                        }
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("PASSWORD (TAP TO COPY)", style = MaterialTheme.typography.labelSmall, color = PlaytTextSecondary)
                                        Text(
                                            text = tournament.roomPassword,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black,
                                            color = PlaytCoinGold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Crystal 48-Slot Grid
            item {
                Surface(
                    color = CrystalGlassSurface,
                    shape = RoundedCornerShape(26.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.1.dp,
                        crystalSpecularBorderBrush(CrystalAuroraViolet)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(crystalSurfaceBrush(CrystalAuroraViolet))
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CRYSTAL SLOT MATRIX (1 - ${tournament.totalSlots})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = PlaytTextPrimary
                            )
                            Text(
                                text = "Slot #$selectedSlot",
                                style = MaterialTheme.typography.labelSmall,
                                color = CrystalAuroraBlue,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        val totalSlotsInt = tournament.totalSlots.toInt().coerceIn(2, 48)
                        val rows = (1..totalSlotsInt).chunked(6)
                        rows.forEach { rowSlots ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowSlots.forEach { slotNum ->
                                    val regAtSlot = takenSlotsMap[slotNum.toLong()]
                                    val isMine = myReg?.slotNumber == slotNum.toLong()
                                    val isTaken = regAtSlot != null
                                    val isPicked = selectedSlot == slotNum.toLong()

                                    Surface(
                                        color = when {
                                            isMine -> PlaytVerifiedGreen
                                            isTaken -> Color.White.copy(alpha = 0.04f)
                                            isPicked -> CrystalAuroraBlue
                                            else -> Color.White.copy(alpha = 0.10f)
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isPicked || isMine) Color.White else CrystalSubtleEdge
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(36.dp)
                                            .clickable(enabled = !isTaken && myReg == null) {
                                                selectedSlot = slotNum.toLong()
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = if (isMine) "YOU" else "#$slotNum",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Black,
                                                color = when {
                                                    isMine || isPicked -> Color.Black
                                                    isTaken -> PlaytTextMuted
                                                    else -> PlaytTextPrimary
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (myReg == null) {
                            Button(
                                onClick = { showJoinModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson),
                                shape = RoundedCornerShape(22.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("open_join_modal_button")
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (tournament.entryFee == 0L) {
                                        "JOIN SLOT #$selectedSlot • FREE ENTRY"
                                    } else {
                                        "JOIN SLOT #$selectedSlot • PAY ₹$remainingMainFee (+₹$bonusUsable BONUS)"
                                    },
                                    fontWeight = FontWeight.Black
                                )
                            }
                        } else {
                            Surface(
                                color = PlaytVerifiedGreen.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(18.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PlaytVerifiedGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "REGISTERED • SLOT #${myReg.slotNumber} (${myReg.ffIgn})",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Black,
                                    color = PlaytVerifiedGreen,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (myReg != null) {
                item {
                    Surface(
                        color = CrystalGlassSurface,
                        shape = RoundedCornerShape(26.dp),
                        border = androidx.compose.foundation.BorderStroke(1.1.dp, crystalSpecularBorderBrush(CrystalAuroraBlue)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .background(crystalSurfaceBrush(CrystalAuroraBlue))
                                .padding(18.dp)
                        ) {
                            Text(
                                text = "BOOYAH PROOF & ANTI-CHEAT POV UPLOAD",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = CrystalAuroraBlue
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = proofUrlInput,
                                onValueChange = { proofUrlInput = it },
                                label = { Text("Booyah Screenshot / Kill Summary Note") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = disputeReasonInput,
                                onValueChange = { disputeReasonInput = it },
                                label = { Text("Report Suspect UID / HUD POV Link (Optional)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    viewModel.submitMatchProofOrDispute(
                                        registration = myReg,
                                        proofText = proofUrlInput,
                                        disputeReason = disputeReasonInput
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CrystalAuroraBlue),
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "SUBMIT PROOF TO ADMIN",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showJoinModal) {
        AlertDialog(
            onDismissRequest = { showJoinModal = false },
            containerColor = PlaytSurfaceDark,
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    text = "CONFIRM SLOT #$selectedSlot",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = PlaytTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        color = Color.White.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrystalSpecularHighlight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "3-BUCKET SMART DEDUCTION",
                                style = MaterialTheme.typography.labelSmall,
                                color = PlaytCoinGold,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Entry: ₹${tournament.entryFee}  |  10% Bonus: -₹$bonusUsable  |  Main Coins: ₹$remainingMainFee",
                                style = MaterialTheme.typography.bodySmall,
                                color = PlaytTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = ffUidInput,
                        onValueChange = { ffUidInput = it },
                        label = { Text("Free Fire / FF MAX Numeric UID") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("join_ff_uid_input")
                    )
                    OutlinedTextField(
                        value = ffIgnInput,
                        onValueChange = { ffIgnInput = it },
                        label = { Text("In-Game Name (Exact IGN)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("join_ff_ign_input")
                    )
                    if (tournament.teamSize != "SOLO") {
                        OutlinedTextField(
                            value = teammateInput,
                            onValueChange = { teammateInput = it },
                            label = { Text("Squad Teammates UIDs (Optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showJoinModal = false
                        viewModel.joinTournament(
                            tournament = tournament,
                            ffUid = ffUidInput,
                            ffIgn = ffIgnInput,
                            teammateInfo = teammateInput,
                            preferredSlot = selectedSlot
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.testTag("confirm_join_tournament_button")
                ) {
                    Text("JOIN NOW", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinModal = false }) {
                    Text("CANCEL", color = PlaytTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun PrizeBreakdownPill(
    label: String,
    amount: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.07f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, accent.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = PlaytTextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = amount,
                style = MaterialTheme.typography.labelLarge,
                color = accent,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun MyContestsScreen(
    viewModel: Playt24ViewModel
) {
    val selectedTab by viewModel.selectedMyContestsTab.collectAsState()
    val registrations by viewModel.userRegistrations.collectAsState()
    val upcoming by viewModel.upcomingTournaments.collectAsState()
    val ongoing by viewModel.ongoingTournaments.collectAsState()
    val completed by viewModel.completedTournaments.collectAsState()

    BackHandler { viewModel.navigateBack() }

    val allTourneys = remember(upcoming, ongoing, completed) {
        (ongoing + upcoming + completed).associateBy { it.id }
    }

    val filteredRegs = remember(registrations, selectedTab, allTourneys) {
        registrations.filter { reg ->
            val t = allTourneys[reg.tournamentId]
            when (selectedTab) {
                "ONGOING" -> t?.status == "ONGOING"
                "UPCOMING" -> t?.status == "UPCOMING" && reg.status == "REGISTERED"
                else -> t?.status == "COMPLETED" || reg.status == "PLAYED" || reg.status == "REFUNDED"
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OverlayTopAppBar(
            title = "MY CONTESTS",
            onBack = { viewModel.navigateBack() }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ONGOING", "UPCOMING", "COMPLETED").forEach { tab ->
                val isSelected = selectedTab == tab
                Surface(
                    color = if (isSelected) CrystalAuroraBlue else CrystalGlassSurface,
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrystalSpecularHighlight),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .clickable { viewModel.openMyContests(tab) }
                ) {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color.Black else PlaytTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredRegs, key = { it.id }) { reg ->
                val t = allTourneys[reg.tournamentId]
                if (t != null) {
                    TournamentBattleCard(
                        tournament = t,
                        joinedRegistration = reg,
                        onClick = { viewModel.openTournamentDetail(t.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun OverlayTopAppBar(
    title: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Surface(
            color = CrystalGlassSurface,
            shape = RoundedCornerShape(26.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.1.dp,
                crystalSpecularBorderBrush(CrystalAuroraBlue)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("overlay_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PlaytTextPrimary
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = PlaytTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
