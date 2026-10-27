package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.data.*
import com.example.ui.ActiveOverlayScreen
import com.example.ui.MainBottomTab
import com.example.ui.Playt24ViewModel
import com.example.ui.theme.*

@Composable
fun Playt24ProfileTab(
    viewModel: Playt24ViewModel
) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsState()
    val config by viewModel.platformConfig.collectAsState()

    var showEditModal by remember { mutableStateOf(false) }
    var showResponsibleGamingModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaytBgObsidian),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        // 1. Crimson Header + Avatar + Verified Pill + 3-Column Stats Card (Matching Profile Screenshot)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PlaytBgObsidian)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp)
                        .background(PlaytCrimson)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_pybattle_logo),
                        contentDescription = "PYBattle Player Avatar",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile?.username ?: "Prince",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = PlaytVerifiedGreen,
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${profile?.kycStatus ?: "Verified"} ✓",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "${profile?.phone ?: "6201393486"} • FF UID: ${profile?.ffUid?.ifBlank { "Not Linked" } ?: "284910384"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats Card (Matches Played / Total Kills / Coins Won / K/D)
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProfileStatItem(
                                value = "${profile?.matchesPlayed ?: 4L}",
                                label = "Matches Played",
                                color = PlaytTextPrimary
                            )
                            Box(modifier = Modifier.width(1.dp).height(38.dp).background(PlaytCardBorder))
                            ProfileStatItem(
                                value = "${profile?.totalKills ?: 0L}",
                                label = "Total Kills",
                                color = PlaytCrimson
                            )
                            Box(modifier = Modifier.width(1.dp).height(38.dp).background(PlaytCardBorder))
                            ProfileStatItem(
                                value = "🪙 ${profile?.totalCoinsWon ?: 0L}",
                                label = "Coins Won",
                                color = PlaytCoinGold
                            )
                            Box(modifier = Modifier.width(1.dp).height(38.dp).background(PlaytCardBorder))
                            ProfileStatItem(
                                value = profile?.kdRatio ?: "0.00",
                                label = "K/D Ratio",
                                color = PlaytVerifiedGreen
                            )
                        }
                    }
                }
            }
        }

        // 2. Bilingual Language Switcher (English / Hinglish) & Important Notice Toggle
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Important Notice Toggle Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                    modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = PlaytCrimson)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = viewModel.tr("Important Notice & Room Alerts", "Important Notice Aur Room Alerts"),
                                style = MaterialTheme.typography.titleMedium,
                                color = PlaytTextPrimary
                            )
                        }
                        Switch(
                            checked = profile?.importantNoticeEnabled ?: true,
                            onCheckedChange = { enabled ->
                                val p = profile ?: return@Switch
                                viewModel.updateProfileSettings(
                                    username = p.username,
                                    phone = p.phone,
                                    ffUid = p.ffUid,
                                    ffIgn = p.ffIgn,
                                    stateName = p.stateName,
                                    kycPanNumber = p.kycPanNumber,
                                    dailyDepositLimit = p.dailyDepositLimit,
                                    language = p.language,
                                    importantNoticeEnabled = enabled
                                )
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PlaytCrimson)
                        )
                    }
                }

                // Language Switcher Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                    modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Translate, contentDescription = null, tint = PlaytCoinGold)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "App Language (EN / Hinglish)",
                                style = MaterialTheme.typography.titleMedium,
                                color = PlaytTextPrimary
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("EN" to "English", "HI" to "Hinglish").forEach { (code, label) ->
                                val isSel = (profile?.language ?: "EN") == code
                                FilterChip(
                                    selected = isSel,
                                    onClick = {
                                        val p = profile ?: return@FilterChip
                                        viewModel.updateProfileSettings(
                                            username = p.username,
                                            phone = p.phone,
                                            ffUid = p.ffUid,
                                            ffIgn = p.ffIgn,
                                            stateName = p.stateName,
                                            kycPanNumber = p.kycPanNumber,
                                            dailyDepositLimit = p.dailyDepositLimit,
                                            language = code,
                                            importantNoticeEnabled = p.importantNoticeEnabled
                                        )
                                    },
                                    label = { Text(label, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PlaytCrimson,
                                        containerColor = PlaytSurfaceElevated
                                    )
                                )
                            }
                        }
                    }
                }

                // Profile Menu Items (Matching Screenshot Menu List + Admin & Compliance)
                ProfileMenuTile(
                    icon = Icons.Default.Person,
                    title = "My Profile, FF UID & KYC Verification",
                    subtitle = "IGN: ${profile?.ffIgn ?: "PRINCE_FF"} • State: ${profile?.stateName ?: "Delhi"}",
                    onClick = { showEditModal = true }
                )

                ProfileMenuTile(
                    icon = Icons.Default.AccountBalanceWallet,
                    title = "My Wallet",
                    subtitle = "Deposited, Winning & Bonus PlayCoins",
                    onClick = { viewModel.selectBottomTab(MainBottomTab.WALLET) }
                )

                ProfileMenuTile(
                    icon = Icons.Default.Leaderboard,
                    title = "Top Players",
                    subtitle = "Daily, Weekly & Monthly Leaderboards",
                    onClick = { viewModel.selectBottomTab(MainBottomTab.RANKING) }
                )

                ProfileMenuTile(
                    icon = Icons.Default.AdminPanelSettings,
                    title = "Operator Admin Console (Role-Based)",
                    subtitle = "Manage Tournaments, Room IDs, UTR Queue & Results",
                    accentColor = PlaytCoinGold,
                    onClick = { viewModel.openAndEnableAdminConsole() }
                )

                ProfileMenuTile(
                    icon = Icons.Default.SupportAgent,
                    title = "Customer Support & Anti-Cheat POV",
                    subtitle = "BR Support, CS/LW Support, Hacker Report & Payment Tickets",
                    onClick = { viewModel.openOverlay(ActiveOverlayScreen.SUPPORT_CENTER) }
                )

                ProfileMenuTile(
                    icon = Icons.Default.Shield,
                    title = "Responsible Gaming & Deposit Limits",
                    subtitle = "Daily Limit: ₹${profile?.dailyDepositLimit ?: 2000L} • Self-Exclusion Controls",
                    onClick = { showResponsibleGamingModal = true }
                )

                ProfileMenuTile(
                    icon = Icons.Default.Gavel,
                    title = "Grievance Officer, Privacy & T&C",
                    subtitle = "18+ Skill Gaming Charter, State Geo-Block & GST/TDS Policy",
                    onClick = { viewModel.openOverlay(ActiveOverlayScreen.COMPLIANCE_LEGAL) }
                )

                ProfileMenuTile(
                    icon = Icons.Default.PlayCircleOutline,
                    title = "App Tutorial & Official Channels",
                    subtitle = "How to join Custom Rooms & upload Booyah POV",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(config.whatsappChannelUrl))
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            viewModel.showMessage("Official Channel: ${config.whatsappChannelUrl}")
                        }
                    }
                )

                ProfileMenuTile(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Log Out",
                    subtitle = "Sign out of your PYBattle Google account",
                    accentColor = PlaytDanger,
                    onClick = { viewModel.signOut() }
                )
            }
        }
    }

    if (showEditModal) {
        val p = profile ?: UserProfile()
        var nameInput by remember(p) { mutableStateOf(p.username) }
        var phoneInput by remember(p) { mutableStateOf(p.phone) }
        var uidInput by remember(p) { mutableStateOf(p.ffUid) }
        var ignInput by remember(p) { mutableStateOf(p.ffIgn) }
        var stateInput by remember(p) { mutableStateOf(p.stateName) }
        var panInput by remember(p) { mutableStateOf(p.kycPanNumber) }

        AlertDialog(
            onDismissRequest = { showEditModal = false },
            containerColor = PlaytSurfaceDark,
            title = { Text("Edit Profile, FF UID & KYC", color = PlaytTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Display Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Phone / WhatsApp Number") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uidInput,
                        onValueChange = { uidInput = it },
                        label = { Text("Free Fire MAX Numeric UID") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = ignInput,
                        onValueChange = { ignInput = it },
                        label = { Text("Free Fire In-Game Name (IGN)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = stateInput,
                        onValueChange = { stateInput = it },
                        label = { Text("Indian State (Geo-Compliance)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = panInput,
                        onValueChange = { panInput = it.uppercase().take(10) },
                        label = { Text("10-Char PAN for KYC Verification") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEditModal = false
                        viewModel.updateProfileSettings(
                            username = nameInput,
                            phone = phoneInput,
                            ffUid = uidInput,
                            ffIgn = ignInput,
                            stateName = stateInput,
                            kycPanNumber = panInput,
                            dailyDepositLimit = p.dailyDepositLimit,
                            language = p.language,
                            importantNoticeEnabled = p.importantNoticeEnabled
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson)
                ) {
                    Text("SAVE PROFILE")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditModal = false }) {
                    Text("Cancel", color = PlaytTextSecondary)
                }
            }
        )
    }

    if (showResponsibleGamingModal) {
        val p = profile ?: UserProfile()
        var limitText by remember(p) { mutableStateOf(p.dailyDepositLimit.toString()) }
        var coolOffHours by remember { mutableIntStateOf(0) }

        AlertDialog(
            onDismissRequest = { showResponsibleGamingModal = false },
            containerColor = PlaytSurfaceDark,
            title = { Text("Responsible Gaming Controls", color = PlaytCoinGold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Set your daily deposit limit and optional self-exclusion cool-off timer to keep esports play healthy and responsible.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaytTextSecondary
                    )
                    OutlinedTextField(
                        value = limitText,
                        onValueChange = { limitText = it.filter { ch -> ch.isDigit() }.take(5) },
                        label = { Text("Daily Deposit Limit (₹)") },
                        singleLine = true
                    )
                    Text("Optional Self-Exclusion Cool-Off:", style = MaterialTheme.typography.labelMedium, color = PlaytTextPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0 to "Active", 24 to "24h Pause", 72 to "3d Pause").forEach { (hrs, label) ->
                            FilterChip(
                                selected = coolOffHours == hrs,
                                onClick = { coolOffHours = hrs },
                                label = { Text(label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PlaytCrimson,
                                    containerColor = PlaytSurfaceElevated
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResponsibleGamingModal = false
                        viewModel.updateProfileSettings(
                            username = p.username,
                            phone = p.phone,
                            ffUid = p.ffUid,
                            ffIgn = p.ffIgn,
                            stateName = p.stateName,
                            kycPanNumber = p.kycPanNumber,
                            dailyDepositLimit = limitText.toLongOrNull() ?: 2000L,
                            language = p.language,
                            importantNoticeEnabled = p.importantNoticeEnabled,
                            selfExclusionHours = coolOffHours
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlaytVerifiedGreen)
                ) {
                    Text("APPLY LIMITS")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResponsibleGamingModal = false }) {
                    Text("Cancel", color = PlaytTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ProfileStatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = PlaytTextSecondary
        )
    }
}

@Composable
private fun ProfileMenuTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color = PlaytCrimson,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PlaytCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = PlaytTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = PlaytTextSecondary
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = PlaytTextSecondary
            )
        }
    }
}

@Composable
fun SupportCenterScreen(
    viewModel: Playt24ViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val tickets by viewModel.supportTickets.collectAsState()
    var selectedCategory by remember { mutableStateOf("BR_SUPPORT") }
    var subjectInput by remember { mutableStateOf("") }
    var messageInput by remember { mutableStateOf("") }
    var proofLinkInput by remember { mutableStateOf("") }

    val categories = listOf(
        Triple("BR_SUPPORT", "BR Support", R.drawable.img_support_br),
        Triple("CS_LW_SUPPORT", "CS & LW Support", R.drawable.img_support_cs),
        Triple("HACKER_POV", "POV / Hacker Report", R.drawable.img_support_pov),
        Triple("PAYMENT_SUPPORT", "Withdraw / Add Cash", R.drawable.img_support_cash)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaytBgObsidian)
    ) {
        Surface(color = PlaytCrimson) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column {
                    Text(
                        text = "Customer Support & Anti-Cheat Desk",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Anti-Spam Rule: Max 1 Open Ticket per Category",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 4 Categorized Support Cards with Extracted Artwork (Matching Customer Support Screenshot)
            item {
                Text(
                    text = "SELECT ISSUE CATEGORY",
                    style = MaterialTheme.typography.labelMedium,
                    color = PlaytCoinGold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { (catId, title, imgRes) ->
                        val isSel = selectedCategory == catId
                        val hasOpenTicket = tickets.any { it.category == catId && (it.status == "OPEN" || it.status == "IN_PROGRESS") }
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSel) PlaytSurfaceElevated else PlaytSurfaceDark
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.5.dp,
                                    if (isSel) PlaytCrimson else PlaytCardBorder,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedCategory = catId }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = imgRes),
                                        contentDescription = title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .border(1.dp, PlaytCoinGold, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = PlaytTextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (hasOpenTicket) "Active Ticket Open" else "Tap to select & raise ticket",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (hasOpenTicket) PlaytWarning else PlaytTextSecondary
                                        )
                                    }
                                }

                                RadioButton(
                                    selected = isSel,
                                    onClick = { selectedCategory = catId },
                                    colors = RadioButtonDefaults.colors(selectedColor = PlaytCrimson)
                                )
                            }
                        }
                    }
                }
            }

            // Ticket Form
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                    modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "RAISE NEW TICKET ($selectedCategory)",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlaytTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = subjectInput,
                            onValueChange = { subjectInput = it },
                            label = { Text("Match ID / UTR / Issue Subject *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("ticket_subject_input")
                        )
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            label = { Text("Describe the issue in detail *") },
                            modifier = Modifier.fillMaxWidth().testTag("ticket_message_input")
                        )
                        OutlinedTextField(
                            value = proofLinkInput,
                            onValueChange = { proofLinkInput = it },
                            label = { Text("HUD POV / Screenshot Reference (Optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                if (subjectInput.isBlank() || messageInput.isBlank()) {
                                    viewModel.showMessage("Please enter both subject and issue description.", isError = true)
                                    return@Button
                                }
                                viewModel.createSupportTicket(
                                    category = selectedCategory,
                                    subject = subjectInput,
                                    message = messageInput,
                                    proofRef = proofLinkInput
                                )
                                subjectInput = ""
                                messageInput = ""
                                proofLinkInput = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson),
                            modifier = Modifier.fillMaxWidth().testTag("submit_ticket_button")
                        ) {
                            Text("SUBMIT SUPPORT TICKET", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // My Ticket History & Admin Replies
            item {
                Text(
                    text = "MY SUPPORT TICKETS (${tickets.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = PlaytTextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            items(tickets, key = { it.id }) { tkt ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                    modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "[${tkt.category}] ${tkt.subject}",
                                style = MaterialTheme.typography.titleMedium,
                                color = PlaytCoinGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = tkt.status,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (tkt.status == "RESOLVED") PlaytVerifiedGreen else PlaytWarning,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(tkt.message, style = MaterialTheme.typography.bodySmall, color = PlaytTextPrimary)
                        if (tkt.adminReply.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = PlaytSurfaceElevated,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "👨‍💻 Admin Reply: ${tkt.adminReply}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PlaytVerifiedGreen,
                                    modifier = Modifier.padding(8.dp)
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
fun ComplianceAndGrievanceScreen(
    viewModel: Playt24ViewModel
) {
    BackHandler { viewModel.navigateBack() }
    val config by viewModel.platformConfig.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaytBgObsidian)
    ) {
        Surface(color = PlaytSurfaceDark) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "COMPLIANCE, LEGAL & GRIEVANCE OFFICER",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ComplianceInfoCard(
                    title = "1. Esports Skill-Based Format Only",
                    body = "PYBattle operates strictly as a Free Fire / Free Fire MAX esports tournament organizer. Entry fees are registration fees for organized custom rooms with fixed, predetermined performance prize pools (Rank & Per-Kill). Betting, wagering, odds, or user-vs-user side stakes are strictly prohibited."
                )
            }
            item {
                ComplianceInfoCard(
                    title = "2. State Geo-Restriction List",
                    body = "Paid entry contests are restricted for residents of: ${config.blockedStatesCsv}. Players from restricted states may only participate in ₹0 Free Practice Matches."
                )
            }
            item {
                ComplianceInfoCard(
                    title = "3. Statutory GST (28%) & TDS (30% Sec 194BA)",
                    body = "All deposits include statutory 28% GST accounting, and net winnings withdrawals are subject to 30% TDS under Section 194BA of the Income Tax Act. Downloadable tax breakdowns are available in Wallet → Transaction History."
                )
            }
            item {
                ComplianceInfoCard(
                    title = "4. Grievance Redressal Officer (India)",
                    body = "Grievance Officer: Vikram Aditya Sharma (PYBattle Compliance Desk)\nEmail: grievance@pybattleesports.in\nResolution SLA: Acknowledgement within 24 hours; resolution within 15 days as per IT Rules 2021."
                )
            }
        }
    }
}

@Composable
private fun ComplianceInfoCard(title: String, body: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
        modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = PlaytCoinGold, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(body, style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
        }
    }
}

@Composable
fun AdminConsoleScreen(
    viewModel: Playt24ViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val config by viewModel.platformConfig.collectAsState()
    val upcoming by viewModel.upcomingTournaments.collectAsState()
    val ongoing by viewModel.ongoingTournaments.collectAsState()
    val adminTxs by viewModel.adminTransactions.collectAsState()
    val adminTickets by viewModel.adminTickets.collectAsState()
    val adminUsers by viewModel.adminUsers.collectAsState()
    val selectedTourneyRegs by viewModel.selectedTournamentRegistrations.collectAsState()

    var adminTab by remember { mutableStateOf("DASHBOARD") }
    val adminTabs = listOf("DASHBOARD", "TOURNEYS", "RESULTS", "FINANCE", "USERS & SUPPORT")

    val allTourneys = upcoming + ongoing
    var selectedResultTourney by remember(allTourneys) { mutableStateOf(allTourneys.firstOrNull()) }

    LaunchedEffect(selectedResultTourney?.id) {
        selectedResultTourney?.id?.let { viewModel.observeTournamentRegistrations(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaytBgObsidian)
    ) {
        Surface(color = PlaytSurfaceDark) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Column {
                            Text(
                                text = "PYBattle ADMIN CONSOLE",
                                style = MaterialTheme.typography.titleLarge,
                                color = PlaytCoinGold,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Role: SUPER_ADMIN • Audit Trail Active",
                                style = MaterialTheme.typography.labelSmall,
                                color = PlaytVerifiedGreen
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    adminTabs.forEach { tab ->
                        FilterChip(
                            selected = adminTab == tab,
                            onClick = { adminTab = tab },
                            label = { Text(tab, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PlaytCrimson,
                                containerColor = PlaytSurfaceElevated
                            )
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (adminTab) {
                "DASHBOARD" -> {
                    item {
                        val pendingUtrs = adminTxs.count { it.type == "DEPOSIT_UTR" && it.status == "PENDING" }
                        val pendingWithdrawals = adminTxs.count { it.type == "WITHDRAWAL" && it.status == "PENDING" }
                        val totalDeposits = adminTxs.filter { it.type == "DEPOSIT_UTR" && it.status == "APPROVED" }.sumOf { it.depositedDelta }
                        val totalPayouts = adminTxs.filter { it.type == "MATCH_WINNING" }.sumOf { it.amountCoins }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                            modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("LIVE PLATFORM KPI METRICS", style = MaterialTheme.typography.titleMedium, color = PlaytCoinGold, fontWeight = FontWeight.Bold)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    MetricPill("Registered Players", "${adminUsers.size}", PlaytElectricBlue)
                                    MetricPill("Active Rooms", "${allTourneys.size}", PlaytVerifiedGreen)
                                    MetricPill("Pending UTRs", "$pendingUtrs", PlaytWarning)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    MetricPill("Approved Deposits", "₹$totalDeposits", PlaytVerifiedGreen)
                                    MetricPill("Prizes Credited", "🪙 $totalPayouts", PlaytCoinGold)
                                    MetricPill("Withdraw Queue", "$pendingWithdrawals", PlaytCrimson)
                                }
                            }
                        }
                    }

                    // Instant Paid Contest Compliance Kill-Switch
                    item {
                        var tickerInput by remember(config.announcementTicker) { mutableStateOf(config.announcementTicker) }
                        var blockedInput by remember(config.blockedStatesCsv) { mutableStateOf(config.blockedStatesCsv) }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PlaytSurfaceElevated),
                            modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCrimson, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "EMERGENCY COMPLIANCE KILL-SWITCH",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = PlaytCrimson,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            text = "Instantly enable/disable all Paid Contests across India while keeping Free Matches open.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = PlaytTextSecondary
                                        )
                                    }
                                    Switch(
                                        checked = config.paidContestsEnabled,
                                        onCheckedChange = { enabled ->
                                            viewModel.adminUpdatePlatformConfig(config.copy(paidContestsEnabled = enabled))
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PlaytVerifiedGreen),
                                        modifier = Modifier.testTag("admin_paid_contests_switch")
                                    )
                                }

                                OutlinedTextField(
                                    value = tickerInput,
                                    onValueChange = { tickerInput = it },
                                    label = { Text("Home Announcement Marquee Ticker") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = blockedInput,
                                    onValueChange = { blockedInput = it },
                                    label = { Text("Geo-Blocked States CSV") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Button(
                                    onClick = {
                                        viewModel.adminUpdatePlatformConfig(
                                            config.copy(
                                                announcementTicker = tickerInput,
                                                blockedStatesCsv = blockedInput
                                            )
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("SAVE GLOBAL COMPLIANCE CONFIG", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                "TOURNEYS" -> {
                    // Create Tournament Card
                    item {
                        var title by remember { mutableStateOf("FF MAX Pro Survival Scrims") }
                        var mode by remember { mutableStateOf("FULL_MAP") }
                        var mapName by remember { mutableStateOf("Bermuda") }
                        var fee by remember { mutableStateOf("20") }
                        var pool by remember { mutableStateOf("800") }
                        var perKill by remember { mutableStateOf("10") }
                        var booyah by remember { mutableStateOf("300") }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                            modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("CREATE NEW TOURNAMENT", style = MaterialTheme.typography.titleMedium, color = PlaytCoinGold, fontWeight = FontWeight.Bold)
                                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tournament Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(value = mode, onValueChange = { mode = it }, label = { Text("Mode (FULL_MAP/CLASH_SQUAD)") }, singleLine = true, modifier = Modifier.weight(1f))
                                    OutlinedTextField(value = mapName, onValueChange = { mapName = it }, label = { Text("Map") }, singleLine = true, modifier = Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(value = fee, onValueChange = { fee = it }, label = { Text("Entry Fee") }, singleLine = true, modifier = Modifier.weight(1f))
                                    OutlinedTextField(value = pool, onValueChange = { pool = it }, label = { Text("Prize Pool") }, singleLine = true, modifier = Modifier.weight(1f))
                                    OutlinedTextField(value = perKill, onValueChange = { perKill = it }, label = { Text("Per Kill") }, singleLine = true, modifier = Modifier.weight(1f))
                                }
                                Button(
                                    onClick = {
                                        val newTourney = Tournament(
                                            title = title,
                                            gameMode = mode,
                                            mapName = mapName,
                                            teamSize = "SOLO",
                                            entryFee = fee.toLongOrNull() ?: 20L,
                                            prizePool = pool.toLongOrNull() ?: 800L,
                                            perKillPrize = perKill.toLongOrNull() ?: 10L,
                                            booyahPrize = booyah.toLongOrNull() ?: 300L,
                                            rank2Prize = 150L,
                                            rank3Prize = 90L,
                                            totalSlots = if (mode == "CLASH_SQUAD") 8L else 48L,
                                            filledSlots = 0L,
                                            startTimeEpochMillis = System.currentTimeMillis() + 30 * 60 * 1000L,
                                            status = "UPCOMING"
                                        )
                                        viewModel.adminCreateNewTournament(newTourney)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PlaytVerifiedGreen),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("PUBLISH TOURNAMENT", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Manage Existing Tournaments (Room ID/Password + Cancel & Auto-Refund)
                    items(allTourneys, key = { it.id }) { tourney ->
                        var roomIdText by remember(tourney.roomId) { mutableStateOf(tourney.roomId) }
                        var roomPassText by remember(tourney.roomPassword) { mutableStateOf(tourney.roomPassword) }

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                            modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("${tourney.title} (${tourney.status})", style = MaterialTheme.typography.titleMedium, color = PlaytTextPrimary, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = roomIdText,
                                        onValueChange = { roomIdText = it },
                                        label = { Text("Room ID") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = roomPassText,
                                        onValueChange = { roomPassText = it },
                                        label = { Text("Password") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            viewModel.adminPublishRoomCredentials(tourney, roomIdText, roomPassText, "UPCOMING")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PlaytElectricBlue),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("PUBLISH ROOM", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.adminPublishRoomCredentials(tourney, roomIdText, roomPassText, "ONGOING")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PlaytVerifiedGreen),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("START LIVE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { viewModel.adminCancelTournamentWithAutoRefund(tourney) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PlaytDanger),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("CANCEL & REFUND", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                "RESULTS" -> {
                    item {
                        Text("SELECT TOURNAMENT TO SETTLE RESULTS & AUTO-PAYOUT", style = MaterialTheme.typography.labelMedium, color = PlaytCoinGold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            allTourneys.forEach { t ->
                                FilterChip(
                                    selected = selectedResultTourney?.id == t.id,
                                    onClick = { selectedResultTourney = t },
                                    label = { Text(t.title) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PlaytCrimson,
                                        containerColor = PlaytSurfaceElevated
                                    )
                                )
                            }
                        }
                    }

                    if (selectedTourneyRegs.isEmpty()) {
                        item {
                            Text("No registrations yet for this tournament. Join from the Home tab first to test instant payout settlement.", color = PlaytTextSecondary)
                        }
                    } else {
                        items(selectedTourneyRegs, key = { it.id }) { reg ->
                            val t = selectedResultTourney ?: return@items
                            var killsInput by remember(reg.kills) { mutableStateOf(reg.kills.toString()) }
                            var rankInput by remember(reg.rankAchieved) { mutableStateOf(if (reg.rankAchieved == 0L) "1" else reg.rankAchieved.toString()) }

                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                                modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(14.dp))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Slot #${reg.slotNumber} • ${reg.username} (IGN: ${reg.ffIgn})", style = MaterialTheme.typography.titleMedium, color = PlaytTextPrimary, fontWeight = FontWeight.Bold)
                                    if (reg.proofText.isNotBlank() || reg.disputeReason.isNotBlank()) {
                                        Text("Proof: ${reg.proofText} • Dispute: ${reg.disputeReason}", style = MaterialTheme.typography.bodySmall, color = PlaytWarning)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        OutlinedTextField(
                                            value = killsInput,
                                            onValueChange = { killsInput = it.filter { ch -> ch.isDigit() } },
                                            label = { Text("Kills (🪙${t.perKillPrize}/kill)") },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = rankInput,
                                            onValueChange = { rankInput = it.filter { ch -> ch.isDigit() } },
                                            label = { Text("Rank (#1=🪙${t.booyahPrize})") },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Button(
                                            onClick = {
                                                viewModel.adminSettlePlayerResult(
                                                    tournament = t,
                                                    registration = reg,
                                                    kills = killsInput.toLongOrNull() ?: 0L,
                                                    rank = rankInput.toLongOrNull() ?: 1L
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = PlaytVerifiedGreen)
                                        ) {
                                            Text("PAYOUT", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "FINANCE" -> {
                    item {
                        Text(
                            text = "MANUAL QR UTR DEPOSIT & WITHDRAWAL APPROVAL QUEUE",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlaytCoinGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(adminTxs, key = { it.id }) { tx ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                            modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${tx.type} • ${tx.username}", style = MaterialTheme.typography.titleMedium, color = PlaytTextPrimary, fontWeight = FontWeight.Bold)
                                    Text("🪙 ${tx.amountCoins} (${tx.status})", style = MaterialTheme.typography.titleMedium, color = PlaytCoinGold, fontWeight = FontWeight.Bold)
                                }
                                Text(tx.note, style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
                                Text("UTR / Payout Ref: ${tx.utrNumber} • GST: ₹${tx.gstAmountInr} • TDS: ₹${tx.tdsAmountInr}", style = MaterialTheme.typography.labelSmall, color = PlaytElectricBlue)

                                if (tx.status == "PENDING") {
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Button(
                                            onClick = { viewModel.adminVerifyTransaction(tx, approve = true) },
                                            colors = ButtonDefaults.buttonColors(containerColor = PlaytVerifiedGreen),
                                            modifier = Modifier.weight(1f).testTag("admin_approve_tx_${tx.id}")
                                        ) {
                                            Text("APPROVE & CREDIT", fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = { viewModel.adminVerifyTransaction(tx, approve = false) },
                                            colors = ButtonDefaults.buttonColors(containerColor = PlaytDanger),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("REJECT", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "USERS & SUPPORT" -> {
                    item {
                        Text("PLAYER KYC, ANTI-CHEAT BAN & WALLET ADJUSTMENTS", style = MaterialTheme.typography.titleMedium, color = PlaytCoinGold, fontWeight = FontWeight.Bold)
                    }
                    items(adminUsers, key = { it.userId }) { u ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                            modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("${u.username} (FF UID: ${u.ffUid}) • State: ${u.stateName}", style = MaterialTheme.typography.titleMedium, color = PlaytTextPrimary, fontWeight = FontWeight.Bold)
                                Text("Wallet: Dep 🪙${u.depositedCoins} | Win 🪙${u.winningCoins} | Bonus 🪙${u.bonusCoins} | KYC: ${u.kycStatus}", style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            viewModel.adminManageUser(
                                                targetUser = u,
                                                newKycStatus = "VERIFIED",
                                                newBannedState = u.isBanned,
                                                depDelta = 50L,
                                                winDelta = 0L,
                                                bonusDelta = 10L,
                                                reason = "Admin Promotional Grant +50 Dep / +10 Bonus"
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = PlaytVerifiedGreen),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("+60 COINS & VERIFY", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.adminManageUser(
                                                targetUser = u,
                                                newKycStatus = u.kycStatus,
                                                newBannedState = !u.isBanned,
                                                depDelta = 0L,
                                                winDelta = 0L,
                                                bonusDelta = 0L,
                                                reason = if (!u.isBanned) "Anti-Cheat Ban" else "Unbanned by Admin"
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (u.isBanned) PlaytElectricBlue else PlaytDanger),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (u.isBanned) "UNBAN PLAYER" else "BAN CHEATER", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("PLAYER SUPPORT TICKETS (${adminTickets.size})", style = MaterialTheme.typography.titleMedium, color = PlaytCoinGold, fontWeight = FontWeight.Bold)
                    }

                    items(adminTickets, key = { it.id }) { tkt ->
                        var replyText by remember(tkt.adminReply) { mutableStateOf(tkt.adminReply) }
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                            modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(14.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("[${tkt.category}] ${tkt.subject} (${tkt.status})", style = MaterialTheme.typography.titleMedium, color = PlaytTextPrimary, fontWeight = FontWeight.Bold)
                                Text("From ${tkt.username}: ${tkt.message}", style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
                                OutlinedTextField(
                                    value = replyText,
                                    onValueChange = { replyText = it },
                                    label = { Text("Admin Resolution Reply") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Button(
                                    onClick = {
                                        viewModel.adminReplyToTicket(tkt, replyText.ifBlank { "Issue verified & resolved by PYBattle Support." }, "RESOLVED")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("SEND REPLY & MARK RESOLVED", fontWeight = FontWeight.Bold)
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
private fun MetricPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.ExtraBold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = PlaytTextSecondary)
    }
}
