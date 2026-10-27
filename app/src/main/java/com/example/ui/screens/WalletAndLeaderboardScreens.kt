package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.example.data.*
import com.example.ui.ActiveOverlayScreen
import com.example.ui.Playt24ViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun Playt24WalletTab(
    viewModel: Playt24ViewModel
) {
    val profile by viewModel.userProfile.collectAsState()
    val transactions by viewModel.walletTransactions.collectAsState()
    val config by viewModel.platformConfig.collectAsState()

    var selectedInvoiceTx by remember { mutableStateOf<WalletTransaction?>(null) }

    val deposited = profile?.depositedCoins ?: 0L
    val winning = profile?.winningCoins ?: 0L
    val bonus = profile?.bonusCoins ?: 0L
    val totalCoins = (deposited + winning + bonus).coerceAtLeast(0L)
    val denom = totalCoins.coerceAtLeast(1L).toFloat()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("wallet_lazy_column"),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        // 1. OriginOS 7 Liquid Crystal 3-Bucket Vault Widget
        item {
            Surface(
                color = CrystalGlassSurface,
                shape = RoundedCornerShape(30.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.4.dp,
                    crystalSpecularBorderBrush(CrystalAuroraBlue)
                ),
                shadowElevation = 20.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.14f),
                                    CrystalAuroraViolet.copy(alpha = 0.12f),
                                    CrystalGlassSurface
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = CrystalAuroraBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ORIGIN CRYSTAL VAULT",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = PlaytTextPrimary,
                                    letterSpacing = 1.sp
                                )
                            }
                            Surface(
                                color = Color.White.copy(alpha = 0.10f),
                                shape = RoundedCornerShape(50),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSpecularHighlight)
                            ) {
                                Text(
                                    text = "1 COIN = ₹1",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PlaytVerifiedGreen,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "TOTAL PLAYCOIN BALANCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytTextSecondary
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "₹$totalCoins",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PLAYCOINS",
                                style = MaterialTheme.typography.labelLarge,
                                color = PlaytCoinGold,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Segmented Crystal Distribution Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                        ) {
                            val depWeight = (deposited.toFloat() / denom).coerceAtLeast(0.08f)
                            val winWeight = (winning.toFloat() / denom).coerceAtLeast(0.08f)
                            val bonWeight = (bonus.toFloat() / denom).coerceAtLeast(0.08f)
                            Box(
                                modifier = Modifier
                                    .weight(depWeight)
                                    .fillMaxHeight()
                                    .background(CrystalAuroraBlue)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(winWeight)
                                    .fillMaxHeight()
                                    .background(PlaytVerifiedGreen)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(bonWeight)
                                    .fillMaxHeight()
                                    .background(PlaytCoinGold)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CrystalBucketCard(
                                title = "Deposited",
                                amount = deposited,
                                rule = "100% Entry",
                                accent = CrystalAuroraBlue,
                                modifier = Modifier.weight(1f)
                            )
                            CrystalBucketCard(
                                title = "Winnings",
                                amount = winning,
                                rule = "Withdrawable",
                                accent = PlaytVerifiedGreen,
                                modifier = Modifier.weight(1f)
                            )
                            CrystalBucketCard(
                                title = "Bonus",
                                amount = bonus,
                                rule = "${config.bonusUsagePercent}% Per Match",
                                accent = PlaytCoinGold,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openManualQrScreen(DEFAULT_ADD_CASH_TIERS[1]) },
                                colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("wallet_add_coins_button")
                            ) {
                                Icon(Icons.Default.QrCode2, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ADD COINS", fontWeight = FontWeight.Black)
                            }
                            Button(
                                onClick = { viewModel.openOverlay(ActiveOverlayScreen.WITHDRAW_COINS) },
                                colors = ButtonDefaults.buttonColors(containerColor = PlaytVerifiedGreen),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("wallet_withdraw_button")
                            ) {
                                Icon(
                                    Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "WITHDRAW",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. OriginOS 7 Atomic Recharge Packs (Admin Verified QR/UTR)
        item {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CRYSTAL RECHARGE PACKS",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = PlaytTextPrimary
                    )
                    Text(
                        text = "QR + UTR ADMIN VERIFIED",
                        style = MaterialTheme.typography.labelSmall,
                        color = CrystalAuroraBlue,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                DEFAULT_ADD_CASH_TIERS.chunked(2).forEach { rowTiers ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowTiers.forEach { tier ->
                            Surface(
                                color = CrystalGlassSurface,
                                shape = RoundedCornerShape(24.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.1.dp,
                                    crystalSpecularBorderBrush(
                                        if (tier.bonusCoins >= 30L) PlaytCoinGold else CrystalAuroraBlue
                                    )
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(24.dp))
                                    .clickable { viewModel.openManualQrScreen(tier) }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(crystalSurfaceBrush(CrystalAuroraBlue))
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "₹${tier.amountInr}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Black,
                                            color = PlaytTextPrimary
                                        )
                                        Surface(
                                            color = PlaytCoinGold.copy(alpha = 0.20f),
                                            shape = RoundedCornerShape(50)
                                        ) {
                                            Text(
                                                text = "+${tier.bonusCoins} BONUS",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = PlaytCoinGold,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Total ${tier.totalCoins} PlayCoins",
                                        style = MaterialTheme.typography.labelSmall,
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

        // 3. Recent Vault Ledger
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT TRANSACTIONS",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Black,
                    color = PlaytTextPrimary
                )
                TextButton(onClick = { viewModel.openOverlay(ActiveOverlayScreen.TRANSACTION_HISTORY) }) {
                    Text("All Passbook >", color = CrystalAuroraBlue, fontWeight = FontWeight.Black)
                }
            }
        }

        if (transactions.isEmpty()) {
            item {
                Surface(
                    color = CrystalGlassSurface,
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrystalSubtleEdge),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                ) {
                    Text(
                        text = "No transactions yet. Select a recharge pack above or join a Free Match!",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaytTextSecondary,
                        modifier = Modifier.padding(18.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(transactions.take(6), key = { it.id }) { tx ->
                Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                    TransactionRowCard(
                        tx = tx,
                        onClick = { selectedInvoiceTx = tx }
                    )
                }
            }
        }
    }

    if (selectedInvoiceTx != null) {
        val tx = selectedInvoiceTx!!
        AlertDialog(
            onDismissRequest = { selectedInvoiceTx = null },
            containerColor = PlaytSurfaceDark,
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    text = "GST / TDS COMPLIANCE INVOICE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = PlaytCoinGold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Transaction ID: ${tx.id}", style = MaterialTheme.typography.bodySmall, color = PlaytTextPrimary)
                    Text("Type: ${tx.type} (${tx.status})", style = MaterialTheme.typography.bodySmall, color = PlaytVerifiedGreen)
                    Text("Total PlayCoins: ${tx.amountCoins}", style = MaterialTheme.typography.bodySmall, color = PlaytTextPrimary)
                    Text(
                        text = "Bucket Delta -> Deposited: ${tx.depositedDelta} | Winning: ${tx.winningDelta} | Bonus: ${tx.bonusDelta}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaytTextSecondary
                    )
                    Text("GST Included (28% Rule): ₹${tx.gstAmountInr}", style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
                    Text("TDS Deducted (30% Net Winnings): ₹${tx.tdsAmountInr}", style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
                    Text("Reference / UTR: ${tx.utrNumber}", style = MaterialTheme.typography.bodySmall, color = CrystalAuroraBlue)
                    Text("Audit Note: ${tx.note}", style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedInvoiceTx = null }) {
                    Text("CLOSE", color = PlaytCoinGold, fontWeight = FontWeight.Black)
                }
            }
        )
    }
}

@Composable
private fun CrystalBucketCard(
    title: String,
    amount: Long,
    rule: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(0.9.dp, CrystalSpecularHighlight),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = PlaytTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "₹$amount",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = accent
            )
            Text(
                text = rule,
                style = MaterialTheme.typography.labelSmall,
                color = PlaytTextSecondary,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun ManualQrAddMoneyScreen(
    viewModel: Playt24ViewModel
) {
    val context = LocalContext.current
    val initialTier by viewModel.pendingAddCashTier.collectAsState()
    val config by viewModel.platformConfig.collectAsState()

    BackHandler { viewModel.navigateBack() }

    var selectedTier by remember(initialTier) { mutableStateOf(initialTier) }
    var utrInput by remember { mutableStateOf("") }
    var selectedUpiApp by remember { mutableStateOf("GPay") }
    var remainingSeconds by remember { mutableIntStateOf(8 * 60) }

    LaunchedEffect(selectedTier) {
        remainingSeconds = 8 * 60
        while (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
    }

    val mins = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val timerFormatted = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)

    Column(modifier = Modifier.fillMaxSize()) {
        OverlayTopAppBar(
            title = "QR SCAN & UTR DEPOSIT",
            onBack = { viewModel.navigateBack() }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Amount Selector Pills
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DEFAULT_ADD_CASH_TIERS.forEach { tier ->
                        val isSelected = selectedTier.amountInr == tier.amountInr
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTier = tier },
                            shape = RoundedCornerShape(50),
                            label = {
                                Text(
                                    text = "₹${tier.amountInr} (+${tier.bonusCoins} Bonus)",
                                    fontWeight = FontWeight.Black
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CrystalAuroraBlue,
                                selectedLabelColor = Color.Black,
                                containerColor = CrystalGlassSurface,
                                labelColor = PlaytTextSecondary
                            )
                        )
                    }
                }
            }

            // 2. OriginOS 7 Crystal QR Scanner Card
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
                            .fillMaxWidth()
                            .background(crystalSurfaceBrush(CrystalAuroraBlue))
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = config.merchantName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Black,
                                    color = PlaytTextPrimary
                                )
                                Text(
                                    text = "UPI ID: ${config.upiId}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CrystalAuroraBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                color = PlaytCrimson.copy(alpha = 0.22f),
                                shape = RoundedCornerShape(50),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, PlaytCrimson)
                            ) {
                                Text(
                                    text = "SESSION $timerFormatted",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Image(
                            painter = painterResource(id = R.drawable.img_pybattle_qr),
                            contentDescription = "PYBattle Official UPI QR",
                            modifier = Modifier
                                .size(220.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .border(1.8.dp, CrystalSpecularHighlight, RoundedCornerShape(22.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Pay ₹${selectedTier.amountInr} -> ${selectedTier.totalCoins} PlayCoins (Credited on Admin Approval)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                            color = PlaytCoinGold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cb.setPrimaryClip(ClipData.newPlainText("PYBattle UPI", config.upiId))
                                viewModel.showMessage("Copied UPI ID: ${config.upiId}")
                            },
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("COPY UPI ID: ${config.upiId}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Submit 12-Digit UTR for Admin Approval (PENDING)
            item {
                Surface(
                    color = CrystalGlassSurface,
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        crystalSpecularBorderBrush(PlaytCoinGold)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(crystalSurfaceBrush(PlaytCoinGold))
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "SUBMIT 12-DIGIT UTR FOR ADMIN VERIFICATION",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = PlaytCoinGold
                        )
                        Text(
                            text = "Your deposit request will be logged as PENDING and credited as soon as Admin approves your UTR.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PlaytTextSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("GPay", "PhonePe", "Paytm", "FamApp").forEach { app ->
                                val selected = selectedUpiApp == app
                                FilterChip(
                                    selected = selected,
                                    onClick = { selectedUpiApp = app },
                                    shape = RoundedCornerShape(50),
                                    label = { Text(app, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CrystalAuroraBlue,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = utrInput,
                            onValueChange = { utrInput = it.filter { ch -> ch.isDigit() }.take(12) },
                            label = { Text("12-Digit UPI Reference / UTR Number") },
                            placeholder = { Text("e.g. 429812349876") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("utr_number_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.submitManualQrUtr(
                                    amountInr = selectedTier.amountInr,
                                    bonusCoins = selectedTier.bonusCoins,
                                    utrNumber = utrInput,
                                    paymentApp = selectedUpiApp
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PlaytVerifiedGreen),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("submit_utr_button")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SUBMIT UTR FOR APPROVAL (₹${selectedTier.amountInr})",
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

@Composable
fun WithdrawCoinsScreen(
    viewModel: Playt24ViewModel
) {
    val profile by viewModel.userProfile.collectAsState()
    val config by viewModel.platformConfig.collectAsState()

    BackHandler { viewModel.navigateBack() }

    var amountInput by remember { mutableStateOf("100") }
    var upiAddressInput by remember { mutableStateOf("") }

    val amountCoins = amountInput.toLongOrNull() ?: 0L
    val tdsInr = if (amountCoins >= 100L) (amountCoins * 30L) / 100L else 0L
    val netPayoutInr = (amountCoins - tdsInr).coerceAtLeast(0L)

    Column(modifier = Modifier.fillMaxSize()) {
        OverlayTopAppBar(
            title = "WITHDRAW WINNING PLAYCOINS",
            onBack = { viewModel.navigateBack() }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(
                    color = CrystalGlassSurface,
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        crystalSpecularBorderBrush(PlaytVerifiedGreen)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(crystalSurfaceBrush(PlaytVerifiedGreen))
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "WITHDRAWABLE WINNINGS BALANCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytTextSecondary
                        )
                        Text(
                            text = "₹${profile?.winningCoins ?: 0L} PlayCoins",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = PlaytVerifiedGreen
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "KYC Status: ${profile?.kycStatus ?: "PENDING"} • Min Withdrawal: ₹${config.minWithdrawalCoins}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (profile?.kycStatus == "VERIFIED") PlaytVerifiedGreen else PlaytCoinGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Surface(
                    color = CrystalGlassSurface,
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(1.1.dp, crystalSpecularBorderBrush(CrystalAuroraBlue)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(crystalSurfaceBrush(CrystalAuroraBlue))
                            .padding(18.dp)
                    ) {
                        OutlinedTextField(
                            value = amountInput,
                            onValueChange = { amountInput = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Withdrawal PlayCoins (Min ₹${config.minWithdrawalCoins})") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_amount_input")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = upiAddressInput,
                            onValueChange = { upiAddressInput = it },
                            label = { Text("Destination UPI ID (e.g. player@okaxis)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdraw_upi_input")
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = Color.White.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSpecularHighlight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "SEC 194BA TDS SUMMARY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PlaytCoinGold,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Requested: ₹$amountCoins  |  TDS (30%): -₹$tdsInr  |  Net Payout: ₹$netPayoutInr",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PlaytTextPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.requestWithdrawal(amountCoins, upiAddressInput) },
                            colors = ButtonDefaults.buttonColors(containerColor = PlaytVerifiedGreen),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("confirm_withdraw_button")
                        ) {
                            Text(
                                text = "REQUEST ₹$netPayoutInr PAYOUT",
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

@Composable
fun TransactionHistoryScreen(
    viewModel: Playt24ViewModel
) {
    val transactions by viewModel.walletTransactions.collectAsState()
    var filterType by remember { mutableStateOf("ALL") }

    BackHandler { viewModel.navigateBack() }

    val filtered = remember(transactions, filterType) {
        if (filterType == "ALL") transactions
        else transactions.filter { it.type == filterType }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OverlayTopAppBar(
            title = "CRYSTAL VAULT PASSBOOK",
            onBack = { viewModel.navigateBack() }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "ALL" to "All Entries",
                "DEPOSIT_UTR" to "QR Deposits",
                "MATCH_ENTRY" to "Match Entries",
                "MATCH_WINNING" to "Booyah Winnings",
                "WITHDRAWAL" to "Withdrawals",
                "BONUS_CREDIT" to "Bonus Rewards"
            ).forEach { (key, label) ->
                FilterChip(
                    selected = filterType == key,
                    onClick = { filterType = key },
                    shape = RoundedCornerShape(50),
                    label = { Text(label, fontWeight = FontWeight.Bold) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered, key = { it.id }) { tx ->
                TransactionRowCard(tx = tx, onClick = {})
            }
        }
    }
}

@Composable
private fun TransactionRowCard(
    tx: WalletTransaction,
    onClick: () -> Unit
) {
    val isCredit = tx.type in listOf("DEPOSIT_UTR", "MATCH_WINNING", "BONUS_CREDIT", "MATCH_REFUND")
    val statusAccent = when (tx.status) {
        "PENDING" -> PlaytCoinGold
        "REJECTED" -> PlaytDanger
        else -> if (isCredit) PlaytVerifiedGreen else PlaytCrimson
    }

    Surface(
        color = CrystalGlassSurface,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, crystalSpecularBorderBrush(statusAccent)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(crystalSurfaceBrush(statusAccent))
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tx.type.replace("_", " "),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        color = PlaytTextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = statusAccent.copy(alpha = 0.20f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = tx.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = statusAccent,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tx.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = PlaytTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "${if (isCredit) "+" else "-"}₹${tx.amountCoins}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = statusAccent
            )
        }
    }
}

@Composable
fun Playt24RankingTab(
    viewModel: Playt24ViewModel
) {
    val daily by viewModel.dailyLeaderboard.collectAsState()
    val weekly by viewModel.weeklyLeaderboard.collectAsState()
    val monthly by viewModel.monthlyLeaderboard.collectAsState()

    var selectedPeriod by remember { mutableStateOf("DAILY") }

    val entries = when (selectedPeriod) {
        "DAILY" -> daily
        "WEEKLY" -> weekly
        else -> monthly
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ranking_lazy_column"),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.3.dp, crystalSpecularBorderBrush(PlaytCoinGold)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .height(150.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_pybattle_banner_leaderboard),
                        contentDescription = "PYBattle Pro Leaderboard",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xEB060913))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "CRYSTAL HALL OF CHAMPIONS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = PlaytCoinGold
                        )
                        Text(
                            text = "Top 15 Verified Free Fire Slayers",
                            style = MaterialTheme.typography.bodySmall,
                            color = PlaytTextPrimary
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("DAILY", "WEEKLY", "MONTHLY").forEach { period ->
                    val selected = selectedPeriod == period
                    Surface(
                        color = if (selected) CrystalAuroraBlue else CrystalGlassSurface,
                        shape = RoundedCornerShape(50),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrystalSpecularHighlight),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .clickable { selectedPeriod = period }
                    ) {
                        Text(
                            text = period,
                            fontWeight = FontWeight.Black,
                            color = if (selected) Color.Black else PlaytTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }
            }
        }

        if (entries.size >= 3) {
            item {
                val first = entries[0]
                val second = entries[1]
                val third = entries[2]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    CrystalPodiumCard(
                        rank = "#2",
                        entry = second,
                        accent = CrystalAuroraBlue,
                        heightDp = 150,
                        modifier = Modifier.weight(1f)
                    )
                    CrystalPodiumCard(
                        rank = "👑 #1",
                        entry = first,
                        accent = PlaytCoinGold,
                        heightDp = 180,
                        modifier = Modifier.weight(1.12f)
                    )
                    CrystalPodiumCard(
                        rank = "#3",
                        entry = third,
                        accent = CrystalAuroraRose,
                        heightDp = 138,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        items(entries.drop(3), key = { it.id }) { row ->
            Surface(
                color = CrystalGlassSurface,
                shape = RoundedCornerShape(22.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, crystalSpecularBorderBrush(CrystalAuroraBlue)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(crystalSurfaceBrush(CrystalAuroraBlue))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color.White.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(50),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSpecularHighlight)
                        ) {
                            Text(
                                text = "#${row.position}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Black,
                                color = CrystalAuroraBlue,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = row.playerName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = PlaytTextPrimary
                            )
                            Text(
                                text = "UID: ${row.ffUid} • ${row.kills} Kills",
                                style = MaterialTheme.typography.labelSmall,
                                color = PlaytTextSecondary
                            )
                        }
                    }
                    Text(
                        text = "₹${row.winningCoins}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = PlaytCoinGold
                    )
                }
            }
        }
    }
}

@Composable
private fun CrystalPodiumCard(
    rank: String,
    entry: LeaderboardEntry,
    accent: Color,
    heightDp: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CrystalGlassSurface,
        shape = RoundedCornerShape(26.dp),
        border = androidx.compose.foundation.BorderStroke(1.4.dp, crystalSpecularBorderBrush(accent)),
        shadowElevation = 14.dp,
        modifier = modifier.height(heightDp.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(crystalSurfaceBrush(accent))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = accent.copy(alpha = 0.22f),
                shape = RoundedCornerShape(50),
                border = androidx.compose.foundation.BorderStroke(0.8.dp, CrystalSpecularHighlight)
            ) {
                Text(
                    text = rank,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                    color = accent,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = entry.playerName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = PlaytTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${entry.kills} Kills",
                    style = MaterialTheme.typography.labelSmall,
                    color = PlaytTextSecondary
                )
            }
            Text(
                text = "₹${entry.winningCoins}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = accent
            )
        }
    }
}

@Composable
fun Playt24OffersTab(
    viewModel: Playt24ViewModel
) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsState()
    val claimed by viewModel.claimedOffers.collectAsState()

    val quests = listOf(
        Triple("daily_checkin", "Daily Crystal Login Crate", 5L),
        Triple("first_booyah_quest", "Play 1 Full Map Match Quest", 10L),
        Triple("squad_synergy", "Complete Squad Profile & UID Link", 15L),
        Triple("fair_play_pledge", "Anti-Cheat Fair Play Honor Bonus", 10L)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("offers_lazy_column"),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                color = CrystalGlassSurface,
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.3.dp,
                    crystalSpecularBorderBrush(PlaytCoinGold)
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
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PlaytCoinGold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SQUAD REFERRAL BOUNTY",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = PlaytCoinGold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Invite your Free Fire squad! Earn +25 Bonus PlayCoins when a squadmate joins their first paid tournament.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaytTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.10f),
                            shape = RoundedCornerShape(50),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrystalSpecularHighlight)
                        ) {
                            Text(
                                text = "CODE: ${profile?.referralCode ?: "PYBPRO24"}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = PlaytCoinGold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        Button(
                            onClick = {
                                val code = profile?.referralCode ?: "PYBPRO24"
                                val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cb.setPrimaryClip(ClipData.newPlainText("PYBattle Referral", code))
                                viewModel.showMessage("Copied Referral Code: $code")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("COPY CODE", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "DAILY CRYSTAL MISSIONS",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                color = PlaytTextPrimary
            )
        }

        items(quests, key = { it.first }) { (key, title, reward) ->
            val isClaimed = key in claimed
            Surface(
                color = CrystalGlassSurface,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.1.dp,
                    crystalSpecularBorderBrush(if (isClaimed) PlaytVerifiedGreen else CrystalAuroraBlue)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(crystalSurfaceBrush(CrystalAuroraBlue))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = PlaytTextPrimary
                        )
                        Text(
                            text = "Reward: +$reward Bonus PlayCoins (10% usable per entry)",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytCoinGold
                        )
                    }
                    Button(
                        onClick = { viewModel.claimOfferBonus(key, reward, title) },
                        enabled = !isClaimed,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PlaytVerifiedGreen,
                            disabledContainerColor = Color.White.copy(alpha = 0.08f)
                        )
                    ) {
                        Text(
                            text = if (isClaimed) "CLAIMED" else "CLAIM +$reward",
                            color = if (isClaimed) PlaytTextSecondary else Color.Black,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}
