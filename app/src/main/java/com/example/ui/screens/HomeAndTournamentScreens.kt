package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
    var termsConfirmed by remember { mutableStateOf(true) }
    var selectedState by remember { mutableStateOf("Delhi") }
    var referralInput by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf<String?>(null) }
    var isSigningIn by remember { mutableStateOf(false) }

    val statesList = remember {
        listOf(
            "Delhi", "Maharashtra", "Uttar Pradesh", "Karnataka", "Bihar",
            "West Bengal", "Rajasthan", "Gujarat", "Madhya Pradesh",
            "Assam (Restricted)", "Odisha (Restricted)", "Telangana (Restricted)"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(PlaytBgObsidian, PlaytSurfaceDark, PlaytBgObsidian)
                )
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
                .border(1.dp, PlaytCardBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_pybattle_logo),
                    contentDescription = "PYBattle Official Logo",
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .border(2.dp, PlaytCrimson, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "PYBattle ESPORTS",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PlaytTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "INDIA'S SKILL-BASED FREE FIRE MAX ARENA",
                    style = MaterialTheme.typography.labelMedium,
                    color = PlaytCoinGold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Compliance Notice Pill
                Surface(
                    color = PlaytSurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Skill Esports",
                            tint = PlaytVerifiedGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "100% Skill Esports • Fixed Prize Pools • No Betting or Odds • Anti-Cheat Protected",
                            style = MaterialTheme.typography.bodySmall,
                            color = PlaytTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // State Selector Chips
                Text(
                    text = "Select Your State (Geo-Compliance)",
                    style = MaterialTheme.typography.labelMedium,
                    color = PlaytTextSecondary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statesList.forEach { stateItem ->
                        val cleanState = stateItem.substringBefore(" (")
                        val isRestricted = stateItem.contains("Restricted")
                        val isSelected = selectedState == cleanState
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedState = cleanState },
                            label = {
                                Text(
                                    text = stateItem,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else PlaytTextPrimary
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (isRestricted) PlaytWarning else PlaytCrimson,
                                containerColor = PlaytSurfaceElevated
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = referralInput,
                    onValueChange = { referralInput = it.uppercase().take(12) },
                    label = { Text("Referral Code (Optional)") },
                    placeholder = { Text("e.g. PYBPRO") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("referral_code_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PlaytCrimson,
                        unfocusedBorderColor = PlaytCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { ageConfirmed = !ageConfirmed }
                ) {
                    Checkbox(
                        checked = ageConfirmed,
                        onCheckedChange = { ageConfirmed = it },
                        colors = CheckboxDefaults.colors(checkedColor = PlaytCrimson)
                    )
                    Text(
                        text = "I confirm I am 18+ years old and playing skill-based esports tournaments.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaytTextPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { termsConfirmed = !termsConfirmed }
                ) {
                    Checkbox(
                        checked = termsConfirmed,
                        onCheckedChange = { termsConfirmed = it },
                        colors = CheckboxDefaults.colors(checkedColor = PlaytCrimson)
                    )
                    Text(
                        text = "I accept PYBattle Terms, Privacy Policy, Fair Play & TDS/GST rules.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaytTextPrimary
                    )
                }

                if (authError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = PlaytDanger.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = authError ?: "",
                            color = PlaytDanger,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (!ageConfirmed || !termsConfirmed) {
                            authError = "Please confirm 18+ age verification and Terms & Conditions to continue."
                            return@Button
                        }
                        val activity = context as? Activity
                        if (activity == null) {
                            authError = "Activity context unavailable for Google Sign-In."
                            return@Button
                        }
                        authError = null
                        isSigningIn = true
                        scope.launch {
                            val result = authHelper.signIn(activity)
                            isSigningIn = false
                            result.onSuccess { user ->
                                onSignedIn(user)
                            }.onFailure { err ->
                                authError = err.localizedMessage ?: "Google Sign-In failed. Please try again."
                            }
                        }
                    },
                    enabled = !isSigningIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("google_sign_in_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson)
                ) {
                    if (isSigningIn) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("CONNECTING TO PYBattle...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.SportsEsports, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "SIGN IN WITH GOOGLE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
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
    onAdminClick: () -> Unit,
    onSupportClick: () -> Unit
) {
    Surface(
        color = PlaytCrimson,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.img_pybattle_logo),
                    contentDescription = "PYBattle Emblem",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, PlaytCoinGold, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (profile?.language == "HI") "Swagat Hai," else "Welcome Back,",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PYBattle",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (profile?.kycStatus == "VERIFIED") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "KYC Verified",
                                tint = PlaytCoinGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Admin Console Quick Pill
                Surface(
                    color = PlaytBgObsidian.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .clickable { onAdminClick() }
                        .testTag("open_admin_console_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Console",
                            tint = PlaytCoinGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ADMIN",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Support Icon
                IconButton(
                    onClick = onSupportClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PlaytBgObsidian.copy(alpha = 0.5f))
                        .testTag("top_support_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Customer Support",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // PlayCoin Balance Chip (Matches screenshot `● 5 +`)
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .clickable { onWalletClick() }
                        .testTag("wallet_balance_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(PlaytCoinGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "₹",
                                color = PlaytBgObsidian,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${profile?.totalPlayCoins ?: 5L}",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlaytBgObsidian,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(PlaytVerifiedGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Coins",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
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
    val profile by viewModel.userProfile.collectAsState()
    val config by viewModel.platformConfig.collectAsState()
    val upcoming by viewModel.upcomingTournaments.collectAsState()
    val ongoing by viewModel.ongoingTournaments.collectAsState()
    val completed by viewModel.completedTournaments.collectAsState()
    val registrations by viewModel.userRegistrations.collectAsState()

    var selectedBannerIdx by remember { mutableIntStateOf(0) }
    val bannerImages = listOf(
        R.drawable.img_pybattle_banner_leaderboard,
        R.drawable.img_pybattle_banner_add_cash,
        R.drawable.img_pybattle_banner_booyah
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaytBgObsidian),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        // 1. Announcement Marquee Ticker
        item {
            Surface(
                color = Color(0xFF231C35),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Announcement",
                        tint = PlaytCoinGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = config.announcementTicker,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // 2. Hero Promotional Banner Carousel
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(155.dp)
                        .border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
                        .clickable {
                            when (selectedBannerIdx) {
                                0 -> viewModel.selectBottomTab(MainBottomTab.RANKING)
                                1 -> viewModel.openManualQrScreen()
                                else -> viewModel.openTournamentList("FULL_MAP")
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(id = bannerImages[selectedBannerIdx]),
                        contentDescription = "PYBattle Featured Event Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    bannerImages.indices.forEach { idx ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (idx == selectedBannerIdx) 20.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (idx == selectedBannerIdx) PlaytCrimson else PlaytCardBorder)
                                .clickable { selectedBannerIdx = idx }
                        )
                    }
                }
            }
        }

        // 3. Emergency Paid Contest Compliance Alert (if toggled off by Admin)
        if (!config.paidContestsEnabled) {
            item {
                Surface(
                    color = PlaytWarning.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .border(1.dp, PlaytWarning, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = PlaytWarning)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Compliance Mode Active: Paid entry contests are currently paused by Admin. Free Practice Matches are open!",
                            style = MaterialTheme.typography.bodySmall,
                            color = PlaytTextPrimary
                        )
                    }
                }
            }
        }

        // 4. My Contests Section (Ongoing / Upcoming / Completed — exact match to screenshot)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.tr("My Contests", "Mere Contests"),
                        style = MaterialTheme.typography.titleLarge,
                        color = PlaytTextPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    TextButton(onClick = { viewModel.openMyContests("UPCOMING") }) {
                        Text(
                            text = "${registrations.size} Joined →",
                            color = PlaytCoinGold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MyContestStatusCard(
                        title = viewModel.tr("Ongoing", "Chalu Match"),
                        count = ongoing.size,
                        icon = Icons.Default.PlayCircleFilled,
                        accentColor = PlaytOrange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("my_contests_ongoing_card"),
                        onClick = { viewModel.openMyContests("ONGOING") }
                    )
                    MyContestStatusCard(
                        title = viewModel.tr("Upcoming", "Aane Wale"),
                        count = upcoming.size,
                        icon = Icons.Default.Schedule,
                        accentColor = PlaytElectricBlue,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("my_contests_upcoming_card"),
                        onClick = { viewModel.openMyContests("UPCOMING") }
                    )
                    MyContestStatusCard(
                        title = viewModel.tr("Completed", "Poore Hue"),
                        count = completed.size,
                        icon = Icons.Default.EmojiEvents,
                        accentColor = PlaytVerifiedGreen,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("my_contests_completed_card"),
                        onClick = { viewModel.openMyContests("COMPLETED") }
                    )
                }
            }
        }

        // 5. Esports Games Mode Cards (FF MAX Full Map, Free Match, Clash Squad, Lone Wolf)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = viewModel.tr("Esports Games", "Esports Games"),
                    style = MaterialTheme.typography.titleLarge,
                    color = PlaytTextPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EsportsGameModeImageCard(
                        title = "FF MAX (OLD)",
                        subtitle = "${upcoming.count { it.gameMode == "FULL_MAP" }} Matches Open",
                        drawableRes = R.drawable.img_card_ff_max,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mode_card_full_map"),
                        onClick = { viewModel.openTournamentList("FULL_MAP") }
                    )
                    EsportsGameModeImageCard(
                        title = "Free Match",
                        subtitle = "₹0 Entry • Daily Prizes",
                        drawableRes = R.drawable.img_card_free_match,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mode_card_free_match"),
                        onClick = { viewModel.openTournamentList("FREE_MATCH") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CompactModePillCard(
                        title = "Clash Squad 4v4",
                        tag = "UNLIMITED AMMO",
                        accent = PlaytCrimson,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.openTournamentList("CLASH_SQUAD") }
                    )
                    CompactModePillCard(
                        title = "Lone Wolf 1v1",
                        tag = "HEADSHOT ONLY",
                        accent = PlaytCoinGold,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.openTournamentList("LONE_WOLF") }
                    )
                }
            }
        }

        // 6. Featured Live & Upcoming Matches Feed
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = viewModel.tr("Featured Battle Rooms", "Live Aur Upcoming Rooms"),
                    style = MaterialTheme.typography.titleLarge,
                    color = PlaytTextPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
                TextButton(onClick = { viewModel.openTournamentList("ALL") }) {
                    Text("View All →", color = PlaytCrimson, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(upcoming.take(5), key = { it.id }) { tourney ->
            val isJoined = registrations.any { it.tournamentId == tourney.id }
            Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                TournamentBattleCard(
                    tournament = tourney,
                    isJoined = isJoined,
                    bonusPercent = config.bonusUsagePercent,
                    onClick = { viewModel.openTournamentDetail(tourney.id) }
                )
            }
        }
    }
}

@Composable
private fun MyContestStatusCard(
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(104.dp)
            .border(1.dp, PlaytCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = PlaytTextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = "$count Rooms",
                style = MaterialTheme.typography.labelSmall,
                color = PlaytTextSecondary
            )
        }
    }
}

@Composable
private fun EsportsGameModeImageCard(
    title: String,
    subtitle: String,
    drawableRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = PlaytTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = PlaytCoinGold
                )
            }
        }
    }
}

@Composable
private fun CompactModePillCard(
    title: String,
    tag: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PlaytSurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                color = accent,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = PlaytTextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TournamentBattleCard(
    tournament: Tournament,
    isJoined: Boolean,
    bonusPercent: Long,
    onClick: () -> Unit
) {
    val progress = if (tournament.totalSlots > 0) {
        (tournament.filledSlots.toFloat() / tournament.totalSlots.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val spotsLeft = (tournament.totalSlots - tournament.filledSlots).coerceAtLeast(0L)
    val sdf = remember { SimpleDateFormat("hh:mm a • dd MMM", Locale.getDefault()) }
    val formattedTime = remember(tournament.startTimeEpochMillis) {
        sdf.format(Date(tournament.startTimeEpochMillis))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("tournament_card_${tournament.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = PlaytCrimson.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = tournament.gameMode.replace("_", " "),
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytCrimson,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = PlaytSurfaceElevated,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${tournament.mapName} • ${tournament.teamSize}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytTextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = PlaytCoinGold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tournament.title,
                style = MaterialTheme.typography.titleMedium,
                color = PlaytTextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Prize Pool / Per Kill / Booyah / Entry Metrics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PlaytSurfaceElevated, RoundedCornerShape(12.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn(label = "PRIZE POOL", value = "🪙 ${tournament.prizePool}", valueColor = PlaytCoinGold)
                MetricColumn(label = "PER KILL", value = "🪙 ${tournament.perKillPrize}", valueColor = PlaytTextPrimary)
                MetricColumn(label = "BOOYAH #1", value = "🪙 ${tournament.booyahPrize}", valueColor = PlaytVerifiedGreen)
                MetricColumn(
                    label = "ENTRY FEE",
                    value = if (tournament.entryFee == 0L) "FREE" else "🪙 ${tournament.entryFee}",
                    valueColor = if (tournament.entryFee == 0L) PlaytVerifiedGreen else PlaytCrimson
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Slot Progress Bar + Join CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    LinearProgressIndicator(
                        progress = { progress },
                        color = PlaytCrimson,
                        trackColor = PlaytSurfaceElevated,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Only $spotsLeft spots left",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytOrange
                        )
                        Text(
                            text = "${tournament.filledSlots}/${tournament.totalSlots}",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isJoined) PlaytVerifiedGreen else PlaytCrimson
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isJoined) "JOINED ✓" else if (spotsLeft == 0L) "FULL" else "JOIN",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            if (tournament.entryFee > 0L) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "✨ Up to $bonusPercent% Bonus PlayCoins usable on entry • Room ID shared 10m before start",
                    style = MaterialTheme.typography.labelSmall,
                    color = PlaytTextSecondary
                )
            }
        }
    }
}

@Composable
private fun MetricColumn(
    label: String,
    value: String,
    valueColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = PlaytTextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = valueColor,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun TournamentListScreen(
    viewModel: Playt24ViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val upcoming by viewModel.upcomingTournaments.collectAsState()
    val ongoing by viewModel.ongoingTournaments.collectAsState()
    val registrations by viewModel.userRegistrations.collectAsState()
    val config by viewModel.platformConfig.collectAsState()
    val modeFilter by viewModel.selectedGameModeFilter.collectAsState()
    val teamFilter by viewModel.selectedTeamSizeFilter.collectAsState()

    val allMatches = remember(upcoming, ongoing, modeFilter, teamFilter) {
        (upcoming + ongoing).filter { t ->
            (modeFilter == "ALL" || t.gameMode == modeFilter) &&
                (teamFilter == "ALL" || t.teamSize == teamFilter)
        }
    }

    val modes = listOf("ALL", "FULL_MAP", "CLASH_SQUAD", "LONE_WOLF", "FREE_MATCH")
    val teams = listOf("ALL", "SOLO", "DUO", "SQUAD")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaytBgObsidian)
    ) {
        // Sub-screen Header
        Surface(color = PlaytSurfaceDark, shadowElevation = 4.dp) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "ESPORTS TOURNAMENTS",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    modes.forEach { mode ->
                        FilterChip(
                            selected = modeFilter == mode,
                            onClick = { viewModel.setGameModeFilter(mode) },
                            label = { Text(mode.replace("_", " ")) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PlaytCrimson,
                                containerColor = PlaytSurfaceElevated
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    teams.forEach { team ->
                        FilterChip(
                            selected = teamFilter == team,
                            onClick = { viewModel.setTeamSizeFilter(team) },
                            label = { Text(team) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PlaytElectricBlue,
                                containerColor = PlaytSurfaceElevated
                            )
                        )
                    }
                }
            }
        }

        if (allMatches.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = PlaytCrimson,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No Tournaments Match Your Filter",
                        style = MaterialTheme.typography.titleMedium,
                        color = PlaytTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            viewModel.setGameModeFilter("ALL")
                            viewModel.setTeamSizeFilter("ALL")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson)
                    ) {
                        Text("Reset Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allMatches, key = { it.id }) { tourney ->
                    val isJoined = registrations.any { it.tournamentId == tourney.id }
                    TournamentBattleCard(
                        tournament = tourney,
                        isJoined = isJoined,
                        bonusPercent = config.bonusUsagePercent,
                        onClick = { viewModel.openTournamentDetail(tourney.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TournamentDetailScreen(
    viewModel: Playt24ViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val context = LocalContext.current
    val tourneyId by viewModel.selectedTournamentId.collectAsState()
    val upcoming by viewModel.upcomingTournaments.collectAsState()
    val ongoing by viewModel.ongoingTournaments.collectAsState()
    val completed by viewModel.completedTournaments.collectAsState()
    val userRegs by viewModel.userRegistrations.collectAsState()
    val slotRegs by viewModel.selectedTournamentRegistrations.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val config by viewModel.platformConfig.collectAsState()

    val tournament = remember(tourneyId, upcoming, ongoing, completed) {
        (upcoming + ongoing + completed).find { it.id == tourneyId }
    }

    if (tournament == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tournament not found", color = PlaytTextPrimary)
        }
        return
    }

    val myRegistration = remember(userRegs, tournament.id) {
        userRegs.find { it.tournamentId == tournament.id }
    }

    var ffUidInput by remember(profile) { mutableStateOf(profile?.ffUid ?: "") }
    var ffIgnInput by remember(profile) { mutableStateOf(profile?.ffIgn ?: "") }
    var teammateInput by remember { mutableStateOf("") }
    var selectedSlot by remember { mutableLongStateOf(1L) }
    var showJoinModal by remember { mutableStateOf(false) }

    var proofTextInput by remember(myRegistration) { mutableStateOf(myRegistration?.proofText ?: "") }
    var disputeReasonInput by remember(myRegistration) { mutableStateOf(myRegistration?.disputeReason ?: "") }

    val breakdown = remember(tournament.entryFee, profile, config.bonusUsagePercent) {
        calculateEntryFeeBreakdown(
            entryFee = tournament.entryFee,
            depositedCoins = profile?.depositedCoins ?: 0L,
            winningCoins = profile?.winningCoins ?: 0L,
            bonusCoins = profile?.bonusCoins ?: 0L,
            bonusUsagePercent = config.bonusUsagePercent
        )
    }

    val takenSlots = remember(slotRegs) { slotRegs.map { it.slotNumber }.toSet() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaytBgObsidian)
    ) {
        // Header
        Surface(color = PlaytSurfaceDark) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tournament.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${tournament.gameMode} • ${tournament.mapName} • ${tournament.teamSize}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PlaytCoinGold
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Room ID & Password Vault Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaytSurfaceElevated),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PlaytCoinGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = PlaytCoinGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CUSTOM ROOM CREDENTIALS VAULT",
                                style = MaterialTheme.typography.titleMedium,
                                color = PlaytCoinGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (tournament.roomPublished && tournament.roomId.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("ROOM ID: ${tournament.roomId}", style = MaterialTheme.typography.labelLarge, color = Color.White)
                                    Text("PASSWORD: ${tournament.roomPassword.ifBlank { "No Password" }}", style = MaterialTheme.typography.labelLarge, color = PlaytVerifiedGreen)
                                }
                                OutlinedButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Room ID", tournament.roomId))
                                        viewModel.showMessage("Copied Room ID ${tournament.roomId} to clipboard!")
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("COPY ID")
                                }
                            }
                        } else {
                            Text(
                                text = "🔒 Room ID & Password will be published here 10 minutes before match start.",
                                style = MaterialTheme.typography.bodySmall,
                                color = PlaytTextSecondary
                            )
                        }
                    }
                }
            }

            // 2. Prize Pool Breakdown Table
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                    modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "PERFORMANCE PRIZE POOL BREAKDOWN",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlaytTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        PrizeRow("🥇 Booyah (Rank #1)", "${tournament.booyahPrize} PlayCoins", PlaytCoinGold)
                        PrizeRow("🥈 Runner-Up (Rank #2)", "${tournament.rank2Prize} PlayCoins", PlaytTextPrimary)
                        PrizeRow("🥉 Rank #3", "${tournament.rank3Prize} PlayCoins", PlaytTextPrimary)
                        PrizeRow("🎯 Every Confirmed Kill", "${tournament.perKillPrize} PlayCoins / Kill", PlaytVerifiedGreen)
                    }
                }
            }

            // 3. Interactive Slot Grid (Select Preferred Slot)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                    modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "SELECT LOBBY SLOT (${tournament.filledSlots}/${tournament.totalSlots} FILLED)",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlaytTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val maxSlotsToShow = tournament.totalSlots.coerceAtMost(48L).toInt()
                        val rows = (1..maxSlotsToShow).chunked(8)
                        rows.forEach { rowSlots ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowSlots.forEach { slotNum ->
                                    val isTaken = slotNum.toLong() in takenSlots
                                    val isSelected = selectedSlot == slotNum.toLong()
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(32.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                when {
                                                    isTaken -> PlaytDanger.copy(alpha = 0.25f)
                                                    isSelected -> PlaytCrimson
                                                    else -> PlaytSurfaceElevated
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) PlaytCoinGold else PlaytCardBorder,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable(enabled = !isTaken) { selectedSlot = slotNum.toLong() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "#$slotNum",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isTaken) PlaytTextMuted else Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Official Fair-Play Rules
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                    modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "OFFICIAL FAIR-PLAY & ANTI-CHEAT RULES",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlaytCrimson,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = tournament.rulesText,
                            style = MaterialTheme.typography.bodySmall,
                            color = PlaytTextSecondary
                        )
                    }
                }
            }

            // 5. If Registered: Live Match Result Proof / Anti-Cheat Dispute Upload
            if (myRegistration != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PlaytSurfaceElevated),
                        modifier = Modifier.fillMaxWidth().border(1.dp, PlaytVerifiedGreen, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "YOUR REGISTRATION • SLOT #${myRegistration.slotNumber}",
                                style = MaterialTheme.typography.titleMedium,
                                color = PlaytVerifiedGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "FF IGN: ${myRegistration.ffIgn} (UID: ${myRegistration.ffUid}) • Status: ${myRegistration.status}",
                                style = MaterialTheme.typography.bodySmall,
                                color = PlaytTextPrimary
                            )
                            if (myRegistration.prizeWon > 0L) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "🏆 Prize Won: ${myRegistration.prizeWon} Winning PlayCoins (Rank #${myRegistration.rankAchieved}, ${myRegistration.kills} Kills)",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = PlaytCoinGold
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = proofTextInput,
                                onValueChange = { proofTextInput = it },
                                label = { Text("Booyah Screenshot / HUD POV Link or Match Note") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = disputeReasonInput,
                                onValueChange = { disputeReasonInput = it },
                                label = { Text("Report Hacker / Dispute Reason (Optional)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    viewModel.submitMatchProofOrDispute(
                                        registration = myRegistration,
                                        proofText = proofTextInput,
                                        disputeReason = disputeReasonInput
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("SUBMIT MATCH PROOF / DISPUTE", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Bottom Sticky Join Bar
        Surface(color = PlaytSurfaceDark, shadowElevation = 10.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (tournament.entryFee == 0L) "FREE ENTRY" else "ENTRY: 🪙 ${tournament.entryFee} Coins",
                        style = MaterialTheme.typography.titleMedium,
                        color = PlaytCoinGold,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Bonus Usable: 🪙 ${breakdown.bonusUsed} • Deposit/Win: 🪙 ${breakdown.depositedUsed + breakdown.winningUsed}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PlaytTextSecondary
                    )
                }

                Button(
                    onClick = { showJoinModal = true },
                    enabled = myRegistration == null && tournament.status == "UPCOMING",
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (myRegistration != null) PlaytVerifiedGreen else PlaytCrimson
                    ),
                    modifier = Modifier.height(48.dp).testTag("open_join_modal_button")
                ) {
                    Text(
                        text = if (myRegistration != null) "JOINED (SLOT #${myRegistration.slotNumber})" else "JOIN SLOT #$selectedSlot",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }

    if (showJoinModal) {
        AlertDialog(
            onDismissRequest = { showJoinModal = false },
            containerColor = PlaytSurfaceDark,
            title = {
                Text(
                    text = "Confirm Tournament Entry (Slot #$selectedSlot)",
                    style = MaterialTheme.typography.titleLarge,
                    color = PlaytTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = PlaytSurfaceElevated,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Wallet Deduction Split (10% Bonus Rule):", style = MaterialTheme.typography.labelMedium, color = PlaytCoinGold)
                            Text("• Bonus Coins Used: 🪙 ${breakdown.bonusUsed}", style = MaterialTheme.typography.bodySmall, color = PlaytTextPrimary)
                            Text("• Deposited Coins Used: 🪙 ${breakdown.depositedUsed}", style = MaterialTheme.typography.bodySmall, color = PlaytTextPrimary)
                            Text("• Winning Coins Used: 🪙 ${breakdown.winningUsed}", style = MaterialTheme.typography.bodySmall, color = PlaytTextPrimary)
                            if (!breakdown.canAfford) {
                                Text(
                                    "⚠️ Shortfall: Need ${breakdown.shortfall} more PlayCoins. Tap 'Add Money' first.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PlaytDanger
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = ffUidInput,
                        onValueChange = { ffUidInput = it },
                        label = { Text("Free Fire MAX Numeric UID *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("join_ff_uid_input")
                    )

                    OutlinedTextField(
                        value = ffIgnInput,
                        onValueChange = { ffIgnInput = it },
                        label = { Text("Free Fire In-Game Name (IGN) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("join_ff_ign_input")
                    )

                    if (tournament.teamSize != "SOLO") {
                        OutlinedTextField(
                            value = teammateInput,
                            onValueChange = { teammateInput = it },
                            label = { Text("Teammate FF UIDs (${tournament.teamSize})") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (breakdown.canAfford) {
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
                        modifier = Modifier.testTag("confirm_join_tournament_button")
                    ) {
                        Text("CONFIRM & PAY 🪙 ${tournament.entryFee}", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            showJoinModal = false
                            viewModel.openManualQrScreen()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PlaytVerifiedGreen)
                    ) {
                        Text("ADD PLAYCOINS NOW", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinModal = false }) {
                    Text("Cancel", color = PlaytTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun PrizeRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = PlaytTextSecondary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = valueColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MyContestsScreen(
    viewModel: Playt24ViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val selectedTab by viewModel.selectedMyContestsTab.collectAsState()
    val upcoming by viewModel.upcomingTournaments.collectAsState()
    val ongoing by viewModel.ongoingTournaments.collectAsState()
    val completed by viewModel.completedTournaments.collectAsState()
    val userRegs by viewModel.userRegistrations.collectAsState()
    val config by viewModel.platformConfig.collectAsState()

    val tabs = listOf("ONGOING", "UPCOMING", "COMPLETED")
    val displayedMatches = when (selectedTab) {
        "ONGOING" -> ongoing
        "UPCOMING" -> upcoming
        else -> completed
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaytBgObsidian)
    ) {
        Surface(color = PlaytSurfaceDark) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "$selectedTab MATCHES",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEach { tab ->
                        Button(
                            onClick = { viewModel.openMyContests(tab) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == tab) PlaytCrimson else PlaytSurfaceElevated
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(tab, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (displayedMatches.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = PlaytCoinGold,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Currently No $selectedTab Matches Are Available!",
                        style = MaterialTheme.typography.titleMedium,
                        color = PlaytTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Join upcoming Battle Royale, Clash Squad, or Free Matches to see live room credentials and results here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaytTextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewModel.openTournamentList("ALL") },
                        colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson)
                    ) {
                        Text("EXPLORE UPCOMING ROOMS")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedMatches, key = { it.id }) { tourney ->
                    val isJoined = userRegs.any { it.tournamentId == tourney.id }
                    TournamentBattleCard(
                        tournament = tourney,
                        isJoined = isJoined,
                        bonusPercent = config.bonusUsagePercent,
                        onClick = { viewModel.openTournamentDetail(tourney.id) }
                    )
                }
            }
        }
    }
}
