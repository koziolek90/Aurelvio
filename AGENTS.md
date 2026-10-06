# Aurelvio: Project Context

## Role
You act as a senior mobile lead (Kotlin Multiplatform + Compose Multiplatform).
You help build the Aurelvio application according to the guidelines below. Work
in small steps, write clean, well-tested code, and do not add features that
were not requested.

## What is the app
Aurelvio is an Android and iOS app for net worth tracking / asset journaling.
The user records the value of their assets (cash, savings accounts,
term deposits, stocks, bonds, ETFs, others) and based on this sees how much
their total net worth is and how it grows over time. The app operates fully locally,
offline, without accounts or a backend.

## Key Product Assumptions
- We track asset VALUE, not individual instruments. No stock prices, quantities,
  exchange rates, market quotes, or automatic interest calculation.
- The user periodically (e.g., monthly) manually enters the current value
  of each asset. Charts are created from these entries.
- Savings accounts and term deposits: value at entry time only, no opening/closing
  dates or interest rates.
- Single currency (currency symbol selected in settings only, no conversions).
- The user can add contributions and withdrawals to an asset. Example: deposit
  10,000 into an ETF, after 2 months the value grows to 12,000, meaning a gain of 2,000.
- Out of scope for MVP: notifications, goals, liabilities, tags, widgets,
  database encryption, sync, internet connection.

## Data Model & Calculations
- Asset: name, category, color/icon, status (active/archived).
- Entry: date, asset value after entry, optional contribution/withdrawal, note.
- Asset value on date X = latest known value from an entry with date <= X.
- Net worth on date X = sum of values of all assets on that date.
- Asset profit/loss = latest value - sum of net contributions (contributions minus
  withdrawals). Initial value when adding an asset is by default the first
  contribution (with an option to disable).
- Total profit = sum of individual asset profits. This is a simple difference, not a
  time-weighted rate of return.
- Money: `value class Money(Long)` in cents / minor units (grosze). Never `Double`.
- Time series logic consists of pure functions in the domain layer backed by thorough
  unit tests (missing entry on a given day, withdrawals, archived assets).

## Tech Stack
- Kotlin 2.4.x, Gradle Kotlin DSL, version catalog, convention plugins
  in `build-logic`, AGP 9 (separate `androidApp` module).
- UI: Compose Multiplatform 1.12 + Material 3, Compose Resources (PL/EN).
- Architecture: Clean Architecture + MVI, feature-based modules.
- State: JetBrains lifecycle-viewmodel (KMP), StateFlow, coroutines/Flow.
- Navigation: Navigation 3, `@Serializable` routes (explicit serializer
  registration for iOS).
- DI: Koin 4.x + graph `verify` test.
- Database: Room 3 (`androidx.room3`) + bundled SQLite driver.
- Settings: DataStore (KMP). Dates: kotlinx-datetime. Serialization:
  kotlinx-serialization.
- Charts: Vico (line/bar), custom donut chart on Canvas.
- Files, Biometrics: interfaces in domain + platform implementations.
- Testing: kotlin.test, coroutines-test, Turbine, Compose UI test.
- Quality: detekt, ktlint (Spotless), Kover, GitHub Actions (macOS runner).
- Before using a library, verify its latest version and KMP support status.

## Module Structure
build-logic/, androidApp/, iosApp/, shared/ (DI, navigation, theme, iOS framework),
core/{common,designsystem,testing}, domain/, data/,
feature/{onboarding,lock,dashboard,assets,entry,quickupdate,history,
charts,settings,backup}.

## Architecture Rules
1. Dependencies point inward: feature -> domain <- data. Domain is pure
   Kotlin, without Compose, Room, or Android.
2. Feature modules do not depend on each other.
3. Room entities do not leak outside `data`. Mapping occurs at the boundary.
4. Screen = `Route` (ViewModel, state collection) + stateless `Screen(state,
   onAction)`. MVI state: UiState, Action, Effect.
5. Time handled via injected `Clock`.
6. Domain and calculation logic first, covered by tests, followed by UI.

## Screens
Onboarding, Lock (PIN/biometrics), Dashboard (net worth, period change,
profit, breakdown, chart), Asset list, Asset details, Add/edit
asset, Add entry, Quick update (multiple assets at once), Entry
history, Charts, Categories, Settings, Backup.

## Visual Design
Material 3, dark theme by default: deep navy/black with gold accent,
large readable numbers, minimalism. Profit in green, loss in red (not only
by color, but also with +/- sign).

## Privacy & Data
- No INTERNET permission in release build, no analytics.
- Backup: JSON export/import with `schemaVersion` (import in a single
  transaction) and CSV export. Backup reminder in app.

## Working Rules
- Break tasks down into small steps with clear "done when..." criteria.
- Ask before making major architectural changes or adding new dependencies.
- Code must pass ktlint, detekt, and tests. Do not leave dead code.
- When uncertain or documentation is unclear, state it instead of guessing.
- Respond in Polish, write code comments and names in English.
