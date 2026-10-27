# PLAYT24 — Free Fire & Free Fire MAX Esports Tournament Platform

PLAYT24 is a production-grade, compliance-first Free Fire & Free Fire MAX esports tournament and skill-gaming platform built for India. It unifies the **Player App**, **Role-Based Admin Console**, and **Real-Time Cloud Backend** (Firebase Firestore + Google Sign-In Authentication) inside a cohesive, dark-themed native Android experience.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural and product choices have been locked in based on your answers and reference screenshots:

- **Confirmed Decision 1 — Unified Native Android App (Player + Admin Views) on Firebase**: Both the complete 13-module Player App and the 6-module Admin Panel are delivered inside the native Android app using Jetpack Compose and backed by Firebase Firestore and Google Sign-In (`CredentialManager`), utilizing the `digitalselling-b3d1c` project configuration when provisioned.
- **Confirmed Decision 2 — Manual QR Scan + UTR/Proof Upload with Admin Verification**: Adding PlayCoins uses the branded **PLAYT24 Verified Business QR Code** screen with an 8-minute expiry timer, quick UPI app launch intents (GPay, Paytm, BHIM), and a UTR / 12-digit reference number + screenshot submission flow that routes directly into the Admin Finance Queue for one-tap verification and instant wallet credit.
- **Confirmed Decision 3 — PLAYT24 Branding & 10% Bonus Coin Entry Rule**: The platform is branded as **PLAYT24** with a unified neon-cyberpunk dark esports theme across every screen (upgrading the legacy white screens in Wallet, Add Money, Profile Edit, and Support Center to match the dark gaming aesthetic). Tournament entries automatically apply up to **10% from the Bonus bucket** first, then deduct the remaining 90% from **Deposited** and **Winning** buckets.

---

## 1. Overview & Core Concept

- **What It Does**: Enables Indian Free Fire and Free Fire MAX players to browse and register for daily custom-room esports tournaments (Full Map Battle Royale, Clash Squad, Lone Wolf, Free Match), receive scheduled Room IDs & Passwords before match start, upload match result proofs, track Daily/Weekly/Monthly leaderboards, manage a 3-bucket PlayCoin wallet (Deposited, Winning, Bonus), and withdraw winnings after KYC verification—while giving operators a built-in Admin Console to manage tournaments, verify UTR deposits, publish room credentials, calculate payouts, and enforce compliance.
- **Target Audience / Persona**: Competitive mobile esports players in India (18+, Hinglish & English speakers) looking for structured, fair-play custom rooms with transparent fixed prize pools, and tournament organizers managing matches, anti-cheat reviews, and financial reconciliation on the go.
- **Key Value**: Eliminates fragmented WhatsApp/Telegram room management and inconsistent light/dark UI screens with an atomic 3-bucket wallet ledger, automated 10% bonus fee splitting, built-in Indian esports regulatory compliance (18+ age gate, state geo-blocking, KYC gate, GST/TDS calculation, emergency paid-contest kill-switch), and one-tap admin operations.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Authentication, Device Binding & Compliance Gate**:
   - Players sign in with Google via Jetpack `CredentialManager` (`GetSignInWithGoogleOption`).
   - First-time setup captures their **18+ Age Confirmation**, **State of Residence** (checking against the configurable restricted states list), **Free Fire UID & IGN**, optional **Referral Code**, and logs T&C / Privacy consent along with device-binding and emulator-check telemetry.
2. **Home Hub & Match Discovery (`Home` Tab)**:
   - Top bar displays the **PLAYT24** emblem, Personalized Greeting, Announcement Marquee Ticker (`PLAYT24 MUST FOLLOW OFFICIAL RULES...`), Notification & Support shortcuts, and the red **PlayCoin Wallet Chip (`5 | +`)**.
   - Quick-action icon bar provides 1-tap access to **My Profile**, **My Wallet**, **Top Player**, and **Contact Us**.
   - **Updates & Alerts Carousel** showcases the Monthly Leaderboard Top-10 Prize Pool banner and promotional cards.
   - **Exclusive Game Mode Cards** (`FREE FIRE MAX FULL MAP`, `CLASH SQUAD 4v4`, `LONE WOLF 1v1`, `FREE MATCH`) and **My Contests** (`Ongoing`, `Upcoming`, `Completed`) let players filter and jump straight into tournaments.
3. **Tournament Detail, Slot Map & Room ID Reveal**:
   - Displays map (Bermuda, Purgatory, Alpine, Kalahari, Nexterra), mode (Solo/Duo/Squad), entry fee, per-kill reward, full rank prize breakdown table, and an interactive **48-Slot / Team Grid**.
   - **Room ID & Password Vault**: Locked with a live countdown timer until 10–15 minutes before match start; once published by the Admin, registered players see the Room ID and Password with one-tap copy buttons.
   - **Join Flow Modal**: Pre-fills the player's Free Fire UID & IGN (plus teammate UIDs for Duo/Squad), previews the exact wallet deduction (`10% Bonus + 90% Deposited/Winning`), and atomically reserves the slot.
4. **Live Match, Proof Upload & Dispute Flow**:
   - In `Ongoing` and `Completed` matches, players view live standings, submit their end-of-match Booyah/Rank screenshot or HUD POV link, and can raise a time-windowed **Match Dispute** or **Hacker Report** if needed.
5. **3-Bucket Wallet, Manual QR/UTR Add Coins & KYC Withdrawal (`Wallet` Tab)**:
   - Displays total **PlayCoins** and individual cards for **Deposited**, **Winning**, and **Bonus** buckets.
   - **Add Coins**: Players select an amount (or choose an Add-Cash Bonus Tier like `₹75 -> 80 Coins (+5 Bonus)`), open the **PLAYT24 Verified Business QR Code** screen with an `08:00` countdown timer, scan or tap GPay/Paytm/BHIM, and submit their **12-digit UTR / Reference No.** to create a pending deposit request.
   - **Withdraw Coins**: Enforces **KYC Verified** status and minimum withdrawal threshold, previews any applicable 30% TDS on net winnings, and submits a UPI ID or Bank IMPS withdrawal request.
   - **Transaction History**: Filterable ledger (`All`, `Deposits`, `Withdrawals`, `Match Entry`, `Winnings`, `Bonus`) with downloadable GST/TDS invoice details.
6. **Leaderboards (`Ranking` Tab) & Promotions (`Offer` Tab)**:
   - **Ranking**: Toggle between **Daily**, **Weekly**, and **Monthly** Full Map leaderboards with gradient header bar (`Position | Player Name | Winning`), Top-10 prize breakdown, and sticky `My Rank` footer.
   - **Offers**: Visual promotional banners (Monthly Leaderboard, App Tutorial, Telegram/WhatsApp Official Channels, Add Cash Bonus Tiers, ₹1 Entry ₹10,000 Booyah), Promo Coupon redemption, Daily Check-In streak, Non-Wagering Skill Quests, and **Refer & Earn**.
7. **Support Center & Profile Hub (`Profile` Tab)**:
   - **Support Center**: 2x2 grid of specialized hubs (`Full Map Survival Support`, `CS / LW / Other Modes Support`, `Sending HUD POV / Hacker Report`, `Add Cash / Withdrawal Support`) enforcing the **1 Open Ticket Per Category** anti-spam rule, plus interactive ticket chat and FAQ.
   - **Profile & Compliance Settings**: Displays Avatar, Username, `KYC Verified` badge, stats (`Matches Played`, `Total Kills`, `PlayCoin Won`, `K/D Ratio`), language switch (`English / Hinglish`), Responsible Gaming limits & Self-Exclusion, Grievance Officer details, Legal pages, and the **Admin Console Switch** for authorized staff.

### Visual Identity & Theme

- **Aesthetic Direction**: High-contrast **Dark Esports Arena** aesthetic inspired by the PLAYT24 dark screens—eliminating jarring white sub-screens so the entire app feels like a unified AAA gaming companion.
- **Color Palette & Mood**:
  - `--background`: `#0B0A14` (Deep Obsidian Arena Background)
  - `--surface`: `#16122B` (Midnight Indigo Card Surface)
  - `--surface-elevated`: `#211B3D` (Elevated Purple-Slate Container)
  - `--primary`: `#FF2A3C` (PLAYT24 Crimson Flame Accent)
  - `--secondary`: `#FF7A00` (Booyah Amber / Action Orange)
  - `--accent`: `#7C3AED` to `#EC4899` (Leaderboard Header Neon Violet-to-Pink Gradient)
  - `--coin-gold`: `#FACC15` (PlayCoin Gold Emblem)
  - `--success`: `#22C55E` (KYC Verified & Active Pill Green)
  - `--text-primary`: `#F8FAFC` / `--text-secondary`: `#94A3B8`
- **Typography & Hierarchy**:
  - **Display / Headers**: `Rajdhani` / `Orbitron` (bundled locally via `font-util`) for bold, angular esports tournament headers, prize pools, and countdown timers.
  - **Body & UI Labels**: `Plus Jakarta Sans` / `Inter` for crisp readability in rules, Hinglish tips, and transaction tables.
  - **Monospace**: `JetBrains Mono` / `Roboto Mono` for Room IDs, Passwords, UTR numbers, Free Fire UIDs, and countdown timers.
- **Interactive Feedback & Motion**:
  - Custom dark-themed empty states (replacing generic light illustrations) with actionable CTAs (`Browse Upcoming Matches`), animated countdown timers, shimmer loading skeletons, and instant Snackbar/Toast feedback on Room ID copy or UTR submission.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Unified Dark Theme Over Mixed Light/Dark Screens**
  - *Chosen Approach*: Re-skin the legacy white screens from the reference screenshots (`My Wallet`, `Add Money`, `Edit Profile`, `Support Center`, and `QR Payment`) into the core PLAYT24 deep purple-black (`#0B0A14` / `#16122B`) palette while preserving their exact functional layout and visual badges.
  - *Why*: Eliminates eye strain and visual jarring when navigating from the dark Home screen to Wallet or Support, fulfilling the strict requirement for "one consistent design system on every screen (no white/dark mixing)".
- **Decision 2: Atomic 3-Bucket Wallet Ledger with 10% Bonus Rule**
  - *Chosen Approach*: Store `depositedCoins`, `winningCoins`, and `bonusCoins` as separate integer balances on the user profile with an immutable `wallet_transactions` ledger collection. When joining a contest with entry fee $E$:
    - $\text{bonusUsed} = \min(\text{bonusCoins}, \lfloor 0.10 \times E \rfloor)$
    - $\text{depositedUsed} = \min(\text{depositedCoins}, E - \text{bonusUsed})$
    - $\text{winningUsed} = E - \text{bonusUsed} - \text{depositedUsed}$
  - *Why*: Prevents bonus abuse while rewarding player retention, ensures only `winningCoins` can be withdrawn, and enables exact bucket restoration on match cancellation auto-refunds.
- **Decision 3: Built-In Role-Based Admin Console**
  - *Chosen Approach*: Provide a dedicated **Admin Console** mode accessible from the Top Bar / Profile menu (enabled for admin UIDs and toggleable for testing/demoing all operator workflows on-device) with real-time Firestore synchronization.
  - *Why*: Allows you to test the full loop immediately in the Android emulator—creating a tournament, joining as a player, publishing Room ID/Password as Admin, submitting a UTR deposit, approving the UTR in Admin Finance, and publishing match kills/rank payouts.

---

## 4. Technical Architecture & Data Strategy *(Technical Reference)*

### Architecture & Component Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    PLAYT24 ANDROID APP (Jetpack Compose)                    │
├──────────────────────────────────────┬──────────────────────────────────────┤
│           PLAYER EXPERIENCE          │        ADMIN CONSOLE (RBAC)          │
│  ┌────────┐ ┌─────────┐ ┌─────────┐  │  ┌───────────────┐ ┌──────────────┐  │
│  │  Home  │ │ Ranking │ │  Offer  │  │  │ KPI Dashboard │ │ Match/RoomID │  │
│  └───┬────┘ └────┬────┘ └────┬────┘  │  │ & Kill-Switch │ │   Manager    │  │
│  ┌───┴────┐ ┌────┴────┐ ┌────┴────┐  │  └───────┬───────┘ └──────┬───────┘  │
│  │ Wallet │ │ Profile │ │ Support │  │  ┌───────┴───────┐ ┌──────┴───────┐  │
│  │ QR/UTR │ │ & KYC   │ │ Tickets │  │  │ Result/Payout │ │ UTR Deposit  │  │
│  └───┬────┘ └────┬────┘ └────┬────┘  │  │  Auto-Calc    │ │ & KYC Queue  │  │
└──────┼───────────┼───────────┼───────┴──────────┼─────────┴──────┼──────────┘
       │           │           │                  │                │
       ▼           ▼           ▼                  ▼                ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                 MVVM VIEWMODELS & COMPLIANCE / WALLET ENGINE                │
│  • Auth & Device/Geo Gate      • 10% Bonus + 90% Deposit/Win Fee Splitter   │
│  • GST (28%) / TDS (30%) Calc  • Anti-Cheat Flagging & Self-Exclusion Check │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│             FIREBASE FIRESTORE & AUTHENTICATION (Named Database)            │
│  • /users/{uid} (Profile, 3-Bucket Wallet, KYC, Limits, FF UID, Role)       │
│  • /tournaments/{id} (Mode, Map, Fee, Prize Table, Room ID/Pass, Status)    │
│  • /registrations/{id} (TournamentId, UserId, FF UID/IGN, Slot, BucketSplit)│
│  • /wallet_transactions/{id} (Double-Entry Audit, UTR, Proof, Tax Breakdown)│
│  • /withdrawals/{id} (UPI/Bank, KYC Check, Gross/TDS/Net, Approval Status)  │
│  • /support_tickets/{id} (Category, 1-Open-Per-Category Lock, Chat Log)     │
│  • /platform_config/global (Paid Contests Kill-Switch, Blocked States, UPI) │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Data Model & Core State Machines

1. **Tournament State Machine**:
   - `UPCOMING` (Open for registration, Room ID/Pass hidden) $\rightarrow$ `ROOM_PUBLISHED` (10 mins prior; Room ID/Pass visible only to registered players) $\rightarrow$ `ONGOING` (Match live, proof upload open) $\rightarrow$ `COMPLETED` (Admin enters kills/ranks, prizes credited to `winningCoins`) **OR** `CANCELLED` (100% atomic refund to original `depositedCoins`, `winningCoins`, and `bonusCoins` buckets).
2. **Manual QR / UTR Deposit State Machine**:
   - `INITIATED` (8-min QR countdown started) $\rightarrow$ `UTR_SUBMITTED` (Player submits 12-digit UTR & optional screenshot) $\rightarrow$ `APPROVED` (Admin verifies UTR; `depositedCoins` + tier `bonusCoins` credited atomically with GST invoice record) **OR** `REJECTED` (Reason logged, player notified).
3. **Withdrawal State Machine**:
   - `REQUESTED` (Checks `kycStatus == VERIFIED`, `winningCoins >= minWithdrawal`, deducts `winningCoins`, calculates 30% TDS on net winnings if applicable) $\rightarrow$ `PROCESSING` $\rightarrow$ `PAID` (Admin records payout UTR) **OR** `REJECTED` (Refunds `winningCoins`).
4. **Support Ticket State Machine**:
   - `OPEN` (Enforces max 1 active ticket per user per category: `BR_SUPPORT`, `CS_LW_SUPPORT`, `HACKER_POV`, `PAYMENT_SUPPORT`) $\rightarrow$ `IN_PROGRESS` (Admin replies) $\rightarrow$ `RESOLVED` $\rightarrow$ `CLOSED`.

### Interactive Component & State Mapping

- **Bottom Navigation Bar (5 Tabs)**: `Offer` (`%`), `Ranking` (Trophy/Podium), `Home` (House), `Wallet` (Wallet), `Profile` (User Gear)—matching the exact PLAYT24 navigation bar layout with safe-area window insets.
- **Persistent Top Header**: Displays PLAYT24 logo, `Welcome Back, PLAYT24` / Username, Broadcast/Channel icon, Support Headset icon, and the red `PlayCoins | +` pill that navigates straight to the Add Coins flow.
- **Emergency Compliance Kill-Switch**: Toggling "Disable Paid Contests" in the Admin Console immediately updates `/platform_config/global` in Firestore, hiding paid entry buttons across all connected player clients and allowing only `Free Match` entries until re-enabled.
- **Bilingual Hinglish Support**: Global toggle in Profile/Settings switches helper copy, empty-state guidance, and rule explanations between English and gamer-friendly Hinglish (e.g., *"Room ID & Password match start hone se 10 min pehle milega"*).
