package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.example.ui.MainBottomTab
import com.example.ui.Playt24ViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Playt24WalletTab(
    viewModel: Playt24ViewModel
) {
    val profile by viewModel.userProfile.collectAsState()
    val transactions by viewModel.walletTransactions.collectAsState()
    val config by viewModel.platformConfig.collectAsState()

    var selectedInvoiceTx by remember { mutableStateOf<WalletTransaction?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaytBgObsidian),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. 3-Bucket PlayCoin Balance Card (Matches Wallet Screenshot in Dark Esports Theme)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PlaytCardBorder, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = viewModel.tr("PYBattle WALLET LEDGER", "PYBattle WALLET LEDGER"),
                            style = MaterialTheme.typography.labelMedium,
                            color = PlaytCoinGold
                        )
                        Text(
                            text = "Total: 🪙 ${profile?.totalPlayCoins ?: 5L}",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlaytTextPrimary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WalletBucketColumn(
                            title = "DEPOSITED",
                            amount = profile?.depositedCoins ?: 5L,
                            subtitle = "100% Entry Usable",
                            accent = PlaytElectricBlue
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(50.dp)
                                .background(PlaytCardBorder)
                        )
                        WalletBucketColumn(
                            title = "WINNING",
                            amount = profile?.winningCoins ?: 0L,
                            subtitle = "Instant Withdraw",
                            accent = PlaytVerifiedGreen
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(50.dp)
                                .background(PlaytCardBorder)
                        )
                        WalletBucketColumn(
                            title = "BONUS",
                            amount = profile?.bonusCoins ?: 0L,
                            subtitle = "${config.bonusUsagePercent}% Per Match",
                            accent = PlaytCoinGold
                        )
                    }
                }
            }
        }

        // 2. 3 Primary Action Cards (Add Money / Withdraw / Transaction)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WalletActionCard(
                    title = viewModel.tr("Add Money", "Paisa Jodein"),
                    icon = Icons.Default.QrCodeScanner,
                    accent = PlaytVerifiedGreen,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("wallet_add_money_card"),
                    onClick = { viewModel.openManualQrScreen(DEFAULT_ADD_CASH_TIERS[1]) }
                )
                WalletActionCard(
                    title = viewModel.tr("Withdraw", "Nikalein"),
                    icon = Icons.Default.AccountBalanceWallet,
                    accent = PlaytCrimson,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("wallet_withdraw_card"),
                    onClick = { viewModel.openOverlay(ActiveOverlayScreen.WITHDRAW_COINS) }
                )
                WalletActionCard(
                    title = viewModel.tr("Transaction", "History"),
                    icon = Icons.Default.ReceiptLong,
                    accent = PlaytCoinGold,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("wallet_transactions_card"),
                    onClick = { viewModel.openOverlay(ActiveOverlayScreen.TRANSACTION_HISTORY) }
                )
            }
        }

        // 3. Add Cash Bonus Packs (Matches Add Cash Bonus Screenshot: 75->80, 140->150, 270->300, 500->570)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ADD CASH BONUS TIERS (INSTANT QR)",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlaytTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "1 Coin = ₹1",
                            style = MaterialTheme.typography.labelSmall,
                            color = PlaytCoinGold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    DEFAULT_ADD_CASH_TIERS.forEach { tier ->
                        Surface(
                            color = PlaytSurfaceElevated,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.dp, PlaytCardBorder, RoundedCornerShape(12.dp))
                                .clickable { viewModel.openManualQrScreen(tier) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Deposit ₹${tier.amountInr} → Get 🪙 ${tier.totalCoins} Coins",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = PlaytTextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = if (tier.bonusCoins > 0) "Includes +${tier.bonusCoins} Free Bonus PlayCoins" else "Standard Starter Pack",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (tier.bonusCoins > 0) PlaytVerifiedGreen else PlaytTextSecondary
                                    )
                                }

                                Surface(
                                    color = PlaytCrimson,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "ADD ₹${tier.amountInr}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Recent Wallet Transactions Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT LEDGER ACTIVITY",
                    style = MaterialTheme.typography.titleMedium,
                    color = PlaytTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { viewModel.openOverlay(ActiveOverlayScreen.TRANSACTION_HISTORY) }) {
                    Text("Full History →", color = PlaytCoinGold)
                }
            }
        }

        if (transactions.isEmpty()) {
            item {
                Surface(
                    color = PlaytSurfaceDark,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No transactions yet. Tap 'Add Money' to scan the PYBattle QR code and submit your UTR.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaytTextSecondary,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(transactions.take(5), key = { it.id }) { tx ->
                TransactionRowCard(tx = tx, onClick = { selectedInvoiceTx = tx })
            }
        }
    }

    if (selectedInvoiceTx != null) {
        TaxInvoiceDialog(tx = selectedInvoiceTx!!, onDismiss = { selectedInvoiceTx = null })
    }
}

@Composable
private fun WalletBucketColumn(
    title: String,
    amount: Long,
    subtitle: String,
    accent: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = PlaytTextSecondary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(PlaytCoinGold),
                contentAlignment = Alignment.Center
            ) {
                Text("₹", color = PlaytBgObsidian, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "$amount",
                style = MaterialTheme.typography.headlineMedium,
                color = PlaytTextPrimary,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = accent,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun WalletActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(96.dp)
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
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = PlaytTextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ManualQrAddMoneyScreen(
    viewModel: Playt24ViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val context = LocalContext.current
    val initialTier by viewModel.pendingAddCashTier.collectAsState()
    val config by viewModel.platformConfig.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    var selectedTier by remember(initialTier) { mutableStateOf(initialTier) }
    var customAmountText by remember(initialTier) { mutableStateOf(initialTier.amountInr.toString()) }
    var utrInput by remember { mutableStateOf("") }
    var selectedUpiApp by remember { mutableStateOf("GPay / UPI QR") }
    var remainingSeconds by remember { mutableIntStateOf(480) } // 8:00 countdown matching screenshot

    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
    }

    val mins = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val timerFormatted = String.format(Locale.US, "%d:%02d", mins, secs)

    val amountInr = customAmountText.toLongOrNull() ?: selectedTier.amountInr
    val matchedBonus = remember(amountInr) {
        when {
            amountInr >= 500L -> 70L
            amountInr >= 270L -> 30L
            amountInr >= 140L -> 10L
            amountInr >= 75L -> 5L
            else -> 0L
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaytBgObsidian)
    ) {
        Surface(color = PlaytCrimson) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Column {
                        Text(
                            text = "Add Money • Manual QR & UTR",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Merchant: ${config.merchantName} (Verified Business)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                Surface(
                    color = PlaytBgObsidian,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "⏱ $timerFormatted",
                        style = MaterialTheme.typography.labelMedium,
                        color = PlaytCoinGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Amount Selector Pills
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                    modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "STEP 1: SELECT OR ENTER AMOUNT (₹)",
                            style = MaterialTheme.typography.labelMedium,
                            color = PlaytCoinGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DEFAULT_ADD_CASH_TIERS.forEach { tier ->
                                val isSel = amountInr == tier.amountInr
                                FilterChip(
                                    selected = isSel,
                                    onClick = {
                                        selectedTier = tier
                                        customAmountText = tier.amountInr.toString()
                                    },
                                    label = {
                                        Text(
                                            text = if (tier.bonusCoins > 0) "₹${tier.amountInr} (+${tier.bonusCoins})" else "₹${tier.amountInr}",
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PlaytCrimson,
                                        containerColor = PlaytSurfaceElevated
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customAmountText,
                            onValueChange = { input ->
                                customAmountText = input.filter { it.isDigit() }.take(5)
                            },
                            label = { Text("Deposit Amount in INR (Daily Limit: ₹${profile?.dailyDepositLimit ?: 2000L})") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("qr_amount_input")
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "You will receive: 🪙 $amountInr Deposited + 🪙 $matchedBonus Bonus = 🪙 ${amountInr + matchedBonus} Total PlayCoins",
                            style = MaterialTheme.typography.bodySmall,
                            color = PlaytVerifiedGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2. Branded PYBattle Verified Business QR Card (from Screenshot)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, PlaytElectricBlue.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = PlaytVerifiedGreen)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PYBattle • Verified Business QR",
                                style = MaterialTheme.typography.titleMedium,
                                color = PlaytTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Image(
                            painter = painterResource(id = R.drawable.img_pybattle_qr),
                            contentDescription = "PYBattle Official UPI QR Code",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(230.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(2.dp, Color.White, RoundedCornerShape(14.dp))
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Scan & Pay ₹$amountInr using any UPI App",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlaytCoinGold,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Copy UPI ID & Launch UPI Intent Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", config.upiId))
                                    viewModel.showMessage("Copied UPI ID: ${config.upiId}")
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy UPI ID", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    selectedUpiApp = "UPI Intent App"
                                    val upiUri = Uri.parse(
                                        "upi://pay?pa=${config.upiId}&pn=${config.merchantName}&am=$amountInr&cu=INR"
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW, upiUri)
                                    try {
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                        viewModel.showMessage("Scan the QR code above or copy UPI ID ${config.upiId} to pay.")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PlaytElectricBlue),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Open UPI App", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 3. Step 3: Enter 12-Digit UTR / Reference Number for Admin Approval
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PlaytSurfaceElevated),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PlaytCoinGold, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "STEP 2: SUBMIT 12-DIGIT UTR / UPI REF NO.",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlaytCoinGold,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "After paying ₹$amountInr on the QR code, enter your 12-digit UPI UTR / Reference ID below. Admin will verify and credit your wallet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PlaytTextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = utrInput,
                            onValueChange = { utrInput = it.trim().take(20) },
                            label = { Text("12-Digit UTR / UPI Reference Number *") },
                            placeholder = { Text("e.g. 429812349876") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("utr_number_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.submitManualQrUtr(
                                    amountInr = amountInr,
                                    bonusCoins = matchedBonus,
                                    utrNumber = utrInput,
                                    paymentApp = selectedUpiApp
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PlaytVerifiedGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_utr_button")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SUBMIT UTR FOR ₹$amountInr (+${matchedBonus} BONUS)",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
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
    BackHandler { viewModel.navigateBack() }

    val profile by viewModel.userProfile.collectAsState()
    val config by viewModel.platformConfig.collectAsState()

    var amountText by remember { mutableStateOf("100") }
    var upiAddress by remember { mutableStateOf("") }

    val amountCoins = amountText.toLongOrNull() ?: 0L
    val tdsInr = if (amountCoins >= 100L) (amountCoins * 30L) / 100L else 0L
    val netPayout = (amountCoins - tdsInr).coerceAtLeast(0L)

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
                    text = "WITHDRAW WINNING PLAYCOINS",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AVAILABLE WINNING BALANCE", style = MaterialTheme.typography.labelMedium, color = PlaytTextSecondary)
                    Text(
                        text = "🪙 ${profile?.winningCoins ?: 0L} PlayCoins",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PlaytVerifiedGreen,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = if (profile?.kycStatus == "VERIFIED") PlaytVerifiedGreen else PlaytWarning,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "KYC Status: ${profile?.kycStatus ?: "VERIFIED"} • Min Withdrawal: ${config.minWithdrawalCoins} Coins",
                            style = MaterialTheme.typography.bodySmall,
                            color = PlaytTextPrimary
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { ch -> ch.isDigit() }.take(6) },
                label = { Text("Coins to Withdraw (1 Coin = ₹1)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("withdraw_amount_input")
            )

            OutlinedTextField(
                value = upiAddress,
                onValueChange = { upiAddress = it },
                label = { Text("Your Verified UPI ID or Bank A/C + IFSC *") },
                placeholder = { Text("e.g. player@okaxis") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("withdraw_upi_input")
            )

            Surface(
                color = PlaytSurfaceElevated,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Statutory TDS Breakdown (Section 194BA):", style = MaterialTheme.typography.labelMedium, color = PlaytCoinGold)
                    Text("• Gross Withdrawal: ₹$amountCoins", style = MaterialTheme.typography.bodySmall, color = PlaytTextPrimary)
                    Text("• TDS Deducted (30% on net winnings ≥ ₹100): -₹$tdsInr", style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
                    Text("• Net Payout to UPI: ₹$netPayout", style = MaterialTheme.typography.titleMedium, color = PlaytVerifiedGreen, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = { viewModel.requestWithdrawal(amountCoins, upiAddress) },
                colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_withdrawal_button")
            ) {
                Text("REQUEST PAYOUT (₹$netPayout)", fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun TransactionHistoryScreen(
    viewModel: Playt24ViewModel
) {
    BackHandler { viewModel.navigateBack() }

    val transactions by viewModel.walletTransactions.collectAsState()
    var filterType by remember { mutableStateOf("ALL") }
    var selectedTx by remember { mutableStateOf<WalletTransaction?>(null) }

    val types = listOf("ALL", "DEPOSIT_UTR", "MATCH_ENTRY", "MATCH_WINNING", "WITHDRAWAL", "BONUS_CREDIT")
    val filtered = remember(transactions, filterType) {
        if (filterType == "ALL") transactions else transactions.filter { it.type == filterType }
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
                        text = "WALLET TRANSACTIONS & GST/TDS INVOICES",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    types.forEach { t ->
                        FilterChip(
                            selected = filterType == t,
                            onClick = { filterType = t },
                            label = { Text(t.replace("_", " ")) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PlaytCrimson,
                                containerColor = PlaytSurfaceElevated
                            )
                        )
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No transactions found for $filterType", color = PlaytTextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.id }) { tx ->
                    TransactionRowCard(tx = tx, onClick = { selectedTx = tx })
                }
            }
        }
    }

    if (selectedTx != null) {
        TaxInvoiceDialog(tx = selectedTx!!, onDismiss = { selectedTx = null })
    }
}

@Composable
private fun TransactionRowCard(
    tx: WalletTransaction,
    onClick: () -> Unit
) {
    val isPositive = tx.type in listOf("DEPOSIT_UTR", "MATCH_WINNING", "MATCH_REFUND", "BONUS_CREDIT")
    val statusColor = when (tx.status) {
        "APPROVED", "COMPLETED" -> PlaytVerifiedGreen
        "PENDING" -> PlaytWarning
        else -> PlaytDanger
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PlaytCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tx.type.replace("_", " "),
                        style = MaterialTheme.typography.titleMedium,
                        color = PlaytTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = statusColor.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = tx.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tx.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = PlaytTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Ref/UTR: ${tx.utrNumber} • Tap for GST/TDS Invoice",
                    style = MaterialTheme.typography.labelSmall,
                    color = PlaytCoinGold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "${if (isPositive) "+" else "-"}🪙 ${tx.amountCoins}",
                style = MaterialTheme.typography.titleLarge,
                color = if (isPositive) PlaytVerifiedGreen else PlaytCrimson,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun TaxInvoiceDialog(
    tx: WalletTransaction,
    onDismiss: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val dateStr = tx.createdAt?.toDate()?.let { sdf.format(it) } ?: "Just Now"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PlaytSurfaceDark,
        title = {
            Text("PYBattle GST / TDS TAX INVOICE", style = MaterialTheme.typography.titleLarge, color = PlaytCoinGold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Invoice ID: ${tx.id.uppercase()}", style = MaterialTheme.typography.labelMedium, color = PlaytTextPrimary)
                Text("Date: $dateStr", style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
                Text("Player: ${tx.username}", style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
                Text("Transaction Type: ${tx.type}", style = MaterialTheme.typography.bodySmall, color = PlaytTextPrimary)
                Text("Status: ${tx.status}", style = MaterialTheme.typography.bodySmall, color = PlaytVerifiedGreen)
                HorizontalDivider(color = PlaytCardBorder)
                Text("Total PlayCoins: 🪙 ${tx.amountCoins}", style = MaterialTheme.typography.titleMedium, color = PlaytTextPrimary)
                Text("• Deposited Bucket Delta: ${tx.depositedDelta}", style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
                Text("• Winning Bucket Delta: ${tx.winningDelta}", style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
                Text("• Bonus Bucket Delta: ${tx.bonusDelta}", style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
                HorizontalDivider(color = PlaytCardBorder)
                Text("GST Component (28% Included): ₹${tx.gstAmountInr}", style = MaterialTheme.typography.bodySmall, color = PlaytCoinGold)
                Text("TDS Component (30% Sec 194BA): ₹${tx.tdsAmountInr}", style = MaterialTheme.typography.bodySmall, color = PlaytCoinGold)
                Text("UTR / Reference: ${tx.utrNumber}", style = MaterialTheme.typography.labelSmall, color = PlaytTextSecondary)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson)
            ) {
                Text("CLOSE INVOICE")
            }
        }
    )
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

    val top3 = entries.take(3)
    val rest = entries.drop(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaytBgObsidian)
    ) {
        // Period Selector Tabs (DAILY / WEEKLY / MONTHLY matching Screenshot)
        Surface(color = PlaytCrimsonDark) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("DAILY", "WEEKLY", "MONTHLY").forEach { period ->
                    val isSel = selectedPeriod == period
                    Surface(
                        color = if (isSel) PlaytBgObsidian else Color.Transparent,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPeriod = period }
                            .testTag("ranking_tab_$period")
                    ) {
                        Text(
                            text = period,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSel) PlaytCoinGold else Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top 3 Podium Cards
            if (top3.size >= 3) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, PlaytCoinGold.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🏆 TOP 10 LEADERBOARD PRIZE POOL • 100% ANTI-CHEAT VERIFIED",
                                style = MaterialTheme.typography.labelSmall,
                                color = PlaytCoinGold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                PodiumPlayerColumn(entry = top3[1], medal = "🥈 #2", heightDp = 110, badgeColor = Color(0xFFCBD5E1))
                                PodiumPlayerColumn(entry = top3[0], medal = "👑 #1", heightDp = 132, badgeColor = PlaytCoinGold)
                                PodiumPlayerColumn(entry = top3[2], medal = "🥉 #3", heightDp = 100, badgeColor = Color(0xFFD97706))
                            }
                        }
                    }
                }
            }

            // Table Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PlaytCrimson, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("#", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Players", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Winnings (PlayCoins)", style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            items(rest, key = { it.id }) { row ->
                Surface(
                    color = PlaytSurfaceDark,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PlaytCardBorder, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${row.position}",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlaytCoinGold,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.width(34.dp)
                        )
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = row.playerName,
                                style = MaterialTheme.typography.titleMedium,
                                color = PlaytTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (row.antiCheatClean) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "Anti-Cheat Verified",
                                    tint = PlaytVerifiedGreen,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Text(
                            text = "🪙 ${row.winningCoins}",
                            style = MaterialTheme.typography.titleMedium,
                            color = PlaytCoinGold,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumPlayerColumn(
    entry: LeaderboardEntry,
    medal: String,
    heightDp: Int,
    badgeColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(badgeColor.copy(alpha = 0.2f))
                .border(2.dp, badgeColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = entry.playerName,
                tint = badgeColor,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = medal,
            style = MaterialTheme.typography.labelMedium,
            color = badgeColor,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = entry.playerName,
            style = MaterialTheme.typography.bodySmall,
            color = PlaytTextPrimary,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "🪙 ${entry.winningCoins}",
            style = MaterialTheme.typography.titleMedium,
            color = PlaytCoinGold,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun Playt24OffersTab(
    viewModel: Playt24ViewModel
) {
    val context = LocalContext.current
    val profile by viewModel.userProfile.collectAsState()
    val claimed by viewModel.claimedOffers.collectAsState()
    val config by viewModel.platformConfig.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaytBgObsidian),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = viewModel.tr("EXCLUSIVE OFFERS & SKILL QUESTS", "DHAMAKA OFFERS AUR BONUS"),
                style = MaterialTheme.typography.titleLarge,
                color = PlaytTextPrimary,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // 1. Non-Wagering Daily Check-In & Skill Quests
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
                modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCoinGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "🎁 DAILY CHECK-IN & SKILL REWARDS (NON-WAGERING)",
                        style = MaterialTheme.typography.titleMedium,
                        color = PlaytCoinGold,
                        fontWeight = FontWeight.Bold
                    )

                    OfferQuestRow(
                        title = "Daily Arena Check-In",
                        subtitle = "Instant +5 Bonus PlayCoins (Usable at 10% per match)",
                        isClaimed = "daily_checkin" in claimed,
                        onClaim = { viewModel.claimOfferBonus("daily_checkin", 5L, "Daily Arena Check-In") }
                    )

                    OfferQuestRow(
                        title = "First Match Warmup Bonus",
                        subtitle = "Complete profile & FF UID verification for +10 Bonus Coins",
                        isClaimed = "warmup_quest" in claimed,
                        onClaim = { viewModel.claimOfferBonus("warmup_quest", 10L, "First Match Warmup Bonus") }
                    )
                }
            }
        }

        // 2. Promotional Visual Banners (Matching Offer Screenshot)
        item {
            PromoBannerImageCard(
                drawableRes = R.drawable.img_pybattle_banner_leaderboard,
                caption = "Daily & Weekly Leaderboard Top-10 Prize Pool → Tap to View Ranks",
                onClick = { viewModel.selectBottomTab(MainBottomTab.RANKING) }
            )
        }

        item {
            PromoBannerImageCard(
                drawableRes = R.drawable.img_pybattle_banner_add_cash,
                caption = "Add Cash Bonus: Deposit ₹75 Get 80 • Deposit ₹500 Get 570 Coins → Tap to Add",
                onClick = { viewModel.openManualQrScreen(DEFAULT_ADD_CASH_TIERS[1]) }
            )
        }

        item {
            PromoBannerImageCard(
                drawableRes = R.drawable.img_pybattle_banner_booyah,
                caption = "₹1 Entry = ₹10,000 Mega Booyah Tournament → Tap to Join Now",
                onClick = { viewModel.openTournamentList("FULL_MAP") }
            )
        }

        item {
            PromoBannerImageCard(
                drawableRes = R.drawable.img_pybattle_banner_telegram,
                caption = "Join Official PYBattle Telegram & WhatsApp Channels for Instant Room IDs",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(config.telegramChannelUrl))
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        viewModel.showMessage("Official Channel: ${config.telegramChannelUrl}")
                    }
                }
            )
        }

        // 3. Refer & Earn Card with Anti-Fraud Rules
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PlaytSurfaceElevated),
                modifier = Modifier.fillMaxWidth().border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🤝 REFER SQUADMATES & EARN BONUS COINS",
                        style = MaterialTheme.typography.titleMedium,
                        color = PlaytVerifiedGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Share your invite code ${profile?.referralCode ?: "PYBPRO"}. Get +15 Bonus PlayCoins when your friend completes KYC & plays their first match (Device-bound anti-fraud verified).",
                        style = MaterialTheme.typography.bodySmall,
                        color = PlaytTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val code = profile?.referralCode ?: "PYBPRO"
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Referral Code", code))
                            viewModel.showMessage("Copied Referral Code $code!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PlaytCrimson),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("COPY REFERRAL CODE: ${profile?.referralCode ?: "PYBPRO"}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun OfferQuestRow(
    title: String,
    subtitle: String,
    isClaimed: Boolean,
    onClaim: () -> Unit
) {
    Surface(
        color = PlaytSurfaceElevated,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = PlaytTextPrimary, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = PlaytTextSecondary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onClaim,
                enabled = !isClaimed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isClaimed) PlaytVerifiedGreen else PlaytCrimson
                )
            ) {
                Text(if (isClaimed) "CLAIMED ✓" else "CLAIM", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PromoBannerImageCard(
    drawableRes: Int,
    caption: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PlaytSurfaceDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PlaytCardBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.labelMedium,
                color = PlaytCoinGold,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}
