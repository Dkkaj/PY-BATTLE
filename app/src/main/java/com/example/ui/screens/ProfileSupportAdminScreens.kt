package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.*
import com.example.ui.ActiveOverlayScreen
import com.example.ui.Playt24ViewModel
import com.example.ui.theme.*

@Composable
fun Playt24ProfileTab(
    viewModel: Playt24ViewModel
) {
    val profile by viewModel.userProfile.collectAsState()

    var showEditModal by remember { mutableStateOf(false) }
    var showResponsibleGamingModal by remember { mutableStateOf(false) }

    val matches = (profile?.matchesPlayed ?: 0L).coerceAtLeast(1L)
    val kills = profile?.totalKills ?: 0L
    val kdRatio = String.format(java.util.Locale.US, "%.2f", kills.toDouble() / matches.toDouble())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_lazy_column"),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        // 1. OriginOS 7 Crystal Space Player ID Card
        item {
            Surface(
                color = CrystalGlassSurface,
                shape = RoundedCornerShape(30.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.4.dp,
                    crystalSpecularBorderBrush(CrystalAuroraBlue)
                ),
                shadowElevation = 18.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(crystalSurfaceBrush(CrystalAuroraViolet))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_pybattle_logo),
                            contentDescription = "Operative Avatar",
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .border(1.5.dp, CrystalSpecularHighlight, RoundedCornerShape(22.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile?.username ?: "PYBattle Pro",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = PlaytTextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = if (profile?.kycStatus == "VERIFIED") {
                                        PlaytVerifiedGreen.copy(alpha = 0.20f)
                                    } else {
                                        PlaytCoinGold.copy(alpha = 0.20f)
                                    },
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = if (profile?.kycStatus == "VERIFIED") "KYC VERIFIED" else "KYC PENDING",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (profile?.kycStatus == "VERIFIED") PlaytVerifiedGreen else PlaytCoinGold,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "FF UID: ${profile?.ffUid?.ifBlank { "Not Linked" } ?: "Not Linked"} • IGN: ${profile?.ffIgn?.ifBlank { "Set IGN" } ?: "Set IGN"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = CrystalAuroraBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "State: ${profile?.stateName ?: "Maharashtra"} • Level ${((profile?.matchesPlayed ?: 0L) / 2L) + 1L}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PlaytTextSecondary
                            )
                        }
                        Surface(
                            color = Color.White.copy(alpha = 0.10f),
                            shape = CircleShape,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrystalSpecularHighlight),
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { showEditModal = true }
                                .testTag("edit_profile_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = PlaytCoinGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CrystalCareerStat("Matches", "${profile?.matchesPlayed ?: 0L}", CrystalAuroraBlue, Modifier.weight(1f))
                        CrystalCareerStat("Kills", "${profile?.totalKills ?: 0L}", PlaytCrimson, Modifier.weight(1f))
                        CrystalCareerStat("K/D", kdRatio, PlaytCoinGold, Modifier.weight(1f))
                        CrystalCareerStat("Won", "₹${profile?.totalCoinsWon ?: 0L}", PlaytVerifiedGreen, Modifier.weight(1f))
                    }
                }
            }
        }

        // 2. OriginOS 7 Atomic Language Switcher
        item {
            Surface(
                color = CrystalGlassSurface,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, crystalSpecularBorderBrush(CrystalAuroraBlue)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(crystalSurfaceBrush(CrystalAuroraBlue))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ORIGIN HUD LANGUAGE",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = PlaytTextPrimary
                        )
                        Text(
                            text = "English & Hinglish spatial labels",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytTextSecondary
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("EN" to "English", "HI" to "Hinglish").forEach { (code, label) ->
                            val selected = (profile?.language ?: "EN") == code
                            FilterChip(
                                selected = selected,
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
                                shape = RoundedCornerShape(50),
                                label = { Text(label, fontWeight = FontWeight.Black) }
                            )
                        }
                    }
                }
            }
        }

        // 3. OriginOS 7 Spatial Settings Stack
        item {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                OriginOsCommandTile(
                    title = "Link Free Fire UID, IGN & PAN KYC",
                    subtitle = "Verify KYC for instant Winning Coin withdrawals",
                    icon = Icons.Default.Badge,
                    accent = PlaytCoinGold,
                    onClick = { showEditModal = true }
                )
                OriginOsCommandTile(
                    title = "My Contests & Match Log",
                    subtitle = "Ongoing, Upcoming & Completed Free Fire rooms",
                    icon = Icons.Default.SportsEsports,
                    accent = PlaytCrimson,
                    onClick = { viewModel.openMyContests("UPCOMING") }
                )
                OriginOsCommandTile(
                    title = "Crystal Vault Passbook & Invoices",
                    subtitle = "Track PENDING / APPROVED UTR deposits & payouts",
                    icon = Icons.Default.ReceiptLong,
                    accent = CrystalAuroraBlue,
                    onClick = { viewModel.openOverlay(ActiveOverlayScreen.TRANSACTION_HISTORY) }
                )
                OriginOsCommandTile(
                    title = "Responsible Gaming & Deposit Limits",
                    subtitle = "Daily deposit cap (₹${profile?.dailyDepositLimit ?: 2000L}) & self-exclusion",
                    icon = Icons.Default.HealthAndSafety,
                    accent = PlaytVerifiedGreen,
                    onClick = { showResponsibleGamingModal = true }
                )
                OriginOsCommandTile(
                    title = "24x7 Esports Support & Anti-Cheat Desk",
                    subtitle = "Raise match, UTR payment, or hacker POV tickets",
                    icon = Icons.Default.SupportAgent,
                    accent = CrystalAuroraBlue,
                    onClick = { viewModel.openOverlay(ActiveOverlayScreen.SUPPORT_CENTER) }
                )
                OriginOsCommandTile(
                    title = "Legal, Fair Play & Grievance Officer",
                    subtitle = "Skill gaming compliance, TDS/GST rules & contact",
                    icon = Icons.Default.Gavel,
                    accent = PlaytCoinGold,
                    onClick = { viewModel.openOverlay(ActiveOverlayScreen.COMPLIANCE_LEGAL) }
                )
                OriginOsCommandTile(
                    title = "Sign Out of PYBattle",
                    subtitle = "Disconnect current Google account",
                    icon = Icons.AutoMirrored.Filled.Logout,
                    accent = PlaytDanger,
                    onClick = { viewModel.signOut() }
                )
            }
        }
    }

    if (showEditModal) {
        val current = profile ?: UserProfile()
        var username by remember { mutableStateOf(current.username) }
        var phone by remember { mutableStateOf(current.phone) }
        var ffUid by remember { mutableStateOf(current.ffUid) }
        var ffIgn by remember { mutableStateOf(current.ffIgn) }
        var stateName by remember { mutableStateOf(current.stateName) }
        var panNumber by remember { mutableStateOf(current.kycPanNumber) }

        AlertDialog(
            onDismissRequest = { showEditModal = false },
            containerColor = PlaytSurfaceDark,
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    text = "EDIT PROFILE & KYC",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = PlaytCoinGold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Display Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = ffUid,
                        onValueChange = { ffUid = it },
                        label = { Text("Free Fire Numeric UID") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = ffIgn,
                        onValueChange = { ffIgn = it },
                        label = { Text("Free Fire In-Game Name (IGN)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = stateName,
                        onValueChange = { stateName = it },
                        label = { Text("State (Geo-Compliance)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = panNumber,
                        onValueChange = { panNumber = it.uppercase().take(10) },
                        label = { Text("10-Char PAN Number (KYC)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEditModal = false
                        viewModel.updateProfileSettings(
                            username = username,
                            phone = phone,
                            ffUid = ffUid,
                            ffIgn = ffIgn,
                            stateName = stateName,
                            kycPanNumber = panNumber,
                            dailyDepositLimit = current.dailyDepositLimit,
                            language = current.language,
                            importantNoticeEnabled = current.importantNoticeEnabled
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlaytVerifiedGreen),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("SAVE", color = Color.Black, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditModal = false }) {
                    Text("CANCEL", color = PlaytTextSecondary)
                }
            }
        )
    }

    if (showResponsibleGamingModal) {
        val current = profile ?: UserProfile()
        var limitInput by remember { mutableStateOf(current.dailyDepositLimit.toString()) }

        AlertDialog(
            onDismissRequest = { showResponsibleGamingModal = false },
            containerColor = PlaytSurfaceDark,
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    text = "RESPONSIBLE ESPORTS CONTROLS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = PlaytVerifiedGreen
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Configure your daily PlayCoin deposit cap.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaytTextSecondary
                    )
                    OutlinedTextField(
                        value = limitInput,
                        onValueChange = { limitInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Daily Deposit Limit (₹100 - ₹50,000)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResponsibleGamingModal = false
                        viewModel.updateProfileSettings(
                            username = current.username,
                            phone = current.phone,
                            ffUid = current.ffUid,
                            ffIgn = current.ffIgn,
                            stateName = current.stateName,
                            kycPanNumber = current.kycPanNumber,
                            dailyDepositLimit = limitInput.toLongOrNull() ?: 2000L,
                            language = current.language,
                            importantNoticeEnabled = current.importantNoticeEnabled
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlaytVerifiedGreen),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("UPDATE LIMIT", color = Color.Black, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResponsibleGamingModal = false }) {
                    Text("CLOSE", color = PlaytTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun CrystalCareerStat(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSpecularHighlight),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                color = accent,
                maxLines = 1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = PlaytTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun OriginOsCommandTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit
) {
    Surface(
        color = CrystalGlassSurface,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, crystalSpecularBorderBrush(accent)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(crystalSurfaceBrush(accent))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.18f))
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
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = PlaytTextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = PlaytTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
    val context = LocalContext.current
    val tickets by viewModel.supportTickets.collectAsState()
    val config by viewModel.platformConfig.collectAsState()

    BackHandler { viewModel.navigateBack() }

    var selectedCategory by remember { mutableStateOf("MATCH_ISSUE") }
    var subjectInput by remember { mutableStateOf("") }
    var messageInput by remember { mutableStateOf("") }
    var proofInput by remember { mutableStateOf("") }

    val guideBadges = listOf(
        Triple(R.drawable.img_support_br, "BR Full Map Rules", "Minimum Level 40 • No Emulator"),
        Triple(R.drawable.img_support_cs, "CS 4v4 Custom Rules", "Headshot & Grenade Policy"),
        Triple(R.drawable.img_support_pov, "HUD & Screen POV", "Mandatory for Hacker Reports"),
        Triple(R.drawable.img_support_cash, "UTR & Payout Guide", "Admin Verified UTR Credit")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        OverlayTopAppBar(
            title = "24x7 ESPORTS SUPPORT",
            onBack = { viewModel.navigateBack() }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "OFFICIAL TOURNAMENT GUIDES",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = PlaytCoinGold
                )
                Spacer(modifier = Modifier.height(8.dp))
                guideBadges.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { (imgRes, title, sub) ->
                            Surface(
                                color = CrystalGlassSurface,
                                shape = RoundedCornerShape(22.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, crystalSpecularBorderBrush(CrystalAuroraBlue)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .background(crystalSurfaceBrush(CrystalAuroraBlue))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = imgRes),
                                        contentDescription = title,
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Black,
                                            color = PlaytTextPrimary
                                        )
                                        Text(
                                            text = sub,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = PlaytTextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(config.telegramChannelUrl)))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrystalAuroraBlue),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("TELEGRAM DESK", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                    Button(
                        onClick = {
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(config.whatsappChannelUrl)))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("WHATSAPP CHANNEL", fontWeight = FontWeight.Black)
                    }
                }
            }

            item {
                Surface(
                    color = CrystalGlassSurface,
                    shape = RoundedCornerShape(26.dp),
                    border = androidx.compose.foundation.BorderStroke(1.1.dp, crystalSpecularBorderBrush(CrystalAuroraViolet)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(crystalSurfaceBrush(CrystalAuroraViolet))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "CREATE SUPPORT TICKET (1 ACTIVE PER CATEGORY)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = PlaytTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("MATCH_ISSUE", "PAYMENT_UTR", "WITHDRAWAL", "HACKER_POV").forEach { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { selectedCategory = cat },
                                    shape = RoundedCornerShape(50),
                                    label = {
                                        Text(
                                            text = cat.replace("_", " "),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = subjectInput,
                            onValueChange = { subjectInput = it },
                            label = { Text("Issue Subject / Match ID") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            label = { Text("Detailed Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = proofInput,
                            onValueChange = { proofInput = it },
                            label = { Text("UTR / Screenshot / Hacker POV Reference") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                viewModel.createSupportTicket(
                                    category = selectedCategory,
                                    subject = subjectInput,
                                    message = messageInput,
                                    proofRef = proofInput
                                )
                                subjectInput = ""
                                messageInput = ""
                                proofInput = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SUBMIT TICKET", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            items(tickets, key = { it.id }) { tkt ->
                Surface(
                    color = CrystalGlassSurface,
                    shape = RoundedCornerShape(22.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, crystalSpecularBorderBrush(CrystalAuroraBlue)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(crystalSurfaceBrush(CrystalAuroraBlue))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${tkt.category} • ${tkt.subject}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = PlaytTextPrimary
                            )
                            Text(
                                text = tkt.status,
                                style = MaterialTheme.typography.labelSmall,
                                color = PlaytCoinGold,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(tkt.message, style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
                        if (tkt.adminReply.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Admin Reply: ${tkt.adminReply}",
                                style = MaterialTheme.typography.bodySmall,
                                color = PlaytVerifiedGreen,
                                fontWeight = FontWeight.Bold
                            )
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
    val config by viewModel.platformConfig.collectAsState()
    BackHandler { viewModel.navigateBack() }

    Column(modifier = Modifier.fillMaxSize()) {
        OverlayTopAppBar(
            title = "LEGAL & GRIEVANCE CELL",
            onBack = { viewModel.navigateBack() }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    color = CrystalGlassSurface,
                    shape = RoundedCornerShape(26.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, crystalSpecularBorderBrush(PlaytVerifiedGreen)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(crystalSurfaceBrush(PlaytVerifiedGreen))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "100% SKILL-BASED ESPORTS TOURNAMENT PLATFORM",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = PlaytVerifiedGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "PYBattle hosts competitive Free Fire & Free Fire MAX skill tournaments with fixed, pre-declared prize pools based strictly on in-game rank and eliminations. Zero betting, zero gambling, and zero odds-based wagering.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PlaytTextPrimary
                        )
                    }
                }
            }

            item {
                Surface(
                    color = CrystalGlassSurface,
                    shape = RoundedCornerShape(26.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, crystalSpecularBorderBrush(PlaytCoinGold)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(crystalSurfaceBrush(PlaytCoinGold))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "STATE GEO-RESTRICTION & 18+ GATE",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = PlaytCoinGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Paid entry skill contests are restricted for residents of: ${config.blockedStatesCsv}. Players from these states may participate in ₹0 Free Practice Scrims.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PlaytTextPrimary
                        )
                    }
                }
            }

            item {
                Surface(
                    color = CrystalGlassSurface,
                    shape = RoundedCornerShape(26.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, crystalSpecularBorderBrush(CrystalAuroraBlue)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(crystalSurfaceBrush(CrystalAuroraBlue))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "GRIEVANCE REDRESSAL OFFICER (INDIA IT RULES)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = CrystalAuroraBlue
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Officer Name: PYBattle Grievance Redressal Officer\n" +
                                "Official Email: grievance@pybattle.in\n" +
                                "Support Desk: support@pybattle.in\n" +
                                "Resolution SLA: Acknowledged within 24 hours; resolved within 15 business days.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PlaytTextPrimary
                        )
                    }
                }
            }
        }
    }
}
