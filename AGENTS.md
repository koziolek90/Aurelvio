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
- Kotlin 2.4.x, Gradle Kotlin DSL, Version Catalog (`gradle/libs.versions.toml`),
  Convention Plugins in `build-logic`, AGP 9 (using `com.android.kotlin.multiplatform.library`).
- JVM Target: Java 17 across all modules.
- Base package name: `com.kris.aurelvio.<module_path>` (e.g. `com.kris.aurelvio.domain`).
- Type-safe project accessors enabled (`enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")`).
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

## Convention Plugins (build-logic)
The project uses an included build (`build-logic`) with precompiled script plugins prefixed with `aurelvio.`:
- `aurelvio.kmp.library`: Configures KMP library module + AGP 9 Android Multiplatform Library (`com.android.kotlin.multiplatform.library`), targets (`android`, `iosArm64`, `iosSimulatorArm64`), `jvmTarget = 17`, package namespace (`com.kris.aurelvio.<module_path>`), compiler flags (`-Xexpect-actual-classes`), default opt-ins (`ExperimentalCoroutinesApi`, `ExperimentalMaterial3Api`), and `kotlin.test`.
- `aurelvio.compose.library`: Applies `aurelvio.kmp.library` + Compose Multiplatform + Compose Compiler plugin. Includes core Compose dependencies (runtime, foundation, material3, ui, components-resources, ui-tooling-preview) for `commonMain` and `ui-tooling` for Android.
- `aurelvio.feature`: Applies `aurelvio.compose.library` + `kotlin.plugin.serialization` + `kotlinx-serialization-json` + `project(":core:designsystem")`.

## Module Structure & Dependencies
### Existing Modules
- `build-logic/`: Included build containing convention plugins (`:convention`).
- `androidApp` (`:androidApp`): Regular Android application module (`com.android.application`), entry point `MainActivity`.
- `shared` (`:shared`): KMP/CMP entry point, navigation, DI, theme, iOS framework generation (`Shared`).
- `core/common` (`:core:common`): Common infrastructure utilities, base models, extensions.
- `core/designsystem` (`:core:designsystem`): Design system UI theme (`AurelvioTheme`, `AurelvioColors`), shapes, spacing, `AurelvioCard`, `TrendBadge`.
- `domain` (`:domain`): Pure Kotlin business logic, calculations, repository interfaces.
- `core/testing` (`:core:testing`): Test harnesses, test dispatchers, fake implementations.
- `feature/dashboard` (`:feature:dashboard`): Dashboard screen (MVI), net worth chart (Canvas), range selector, top assets, summary cards.

### Planned Modules
- `data/` (`:data`): Repository implementations, Room database, DataStore settings.
- `feature/*` (`:feature:assets`, `:feature:entry`, `:feature:quickupdate`, `:feature:history`, `:feature:charts`, `:feature:settings`, `:feature:backup`, `:feature:lock`, `:feature:onboarding`).

## Architecture Rules
1. **Dependencies point inward**: `feature -> domain <- data`. `:domain` is pure Kotlin without Compose, Room, DataStore, or Android SDK dependencies.
2. **Module dependencies**:
   - `:domain` depends ONLY on `:core:common` (and `:core:testing` in `commonTest`).
   - `:core:testing` depends ONLY on `:core:common` (does NOT depend on `:domain`).
   - `:shared` depends on `:feature:dashboard`, `:core:designsystem`, `:domain`, `:core:common`.
   - `feature` modules depend on `:core:designsystem` and `:core:common` (and `:domain` once added), but NEVER on each other.
3. **Room entities** do not leak outside `:data`. Mapping to domain models occurs at the boundary.
4. **Screen architecture**: `Route` (ViewModel, state collection) + stateless `Screen(state, onAction)`. MVI state: UiState, Action, Effect.
5. **Time handling**: Time logic handled via injected `Clock`.
6. **Testing priority**: Domain and calculation logic first, covered by unit tests, followed by UI.

## Konwencje MVI i UI
1. **Struktura kontraktu**: Każdy ekran posiada plik `Contract.kt` definiujący:
   - `UiState`: pojedynczy, niemutowalny stan zawierający sealed `Content` (`Loading`, `Empty`, `Data`, `Error`).
   - `Action`: interakcje użytkownika i zdarzenia UI (sealed interface).
   - `Effect`: jednorazowe zdarzenia nawigacji lub powiadomień (sealed interface).
2. **Architektura Route + Screen**:
   - `Route`: odpowiedzialna za powiązanie z ViewModel, zbieranie stanu (`collectAsStateWithLifecycle`), przechwytywanie efektów w `LaunchedEffect` i przekazywanie ich dalej.
   - `Screen`: czysty, bezstanowy Composable przyjmujący `state` i callback `onAction`. Bez bezpośrednich odwołań do ViewModelu.
3. **Brak logiki biznesowej w Composable**: Wszystkie przeliczenia, transformacje i obsługa zdarzeń znajdują się w ViewModelu lub wydzielonych klasach domeny/data.
4. **Lokalizacja tekstów**: 100% tekstów UI w zasobach Compose Resources (`values/strings.xml` i `values-pl/strings.xml`). Brak twardo wpisanych ciągów znaków w kodzie UI.
5. **Reguła trzech dla komponentów**: Komponenty trafiają do `:core:designsystem` dopiero, gdy są współdzielone przez co najmniej 3 ekrany. Do tego czasu pozostają w module danej funkcji.
6. **Wersje podglądu (Previews)**: Każdy Composable ekranu posiada podglądy `@Preview` reprezentujące różne stany (`Data`, `Loading`, `Empty`, `Error`).

## Reguły domeny

### Dane
- Pieniądze: `Money(minorUnits: Long)`, wartość `value class`, zero `Double`
  dla kwot. Udziały procentowe (donut) mogą być `Double`.
- Wpis (`Entry`): data (`LocalDate`), wartość aktywa PO wpisie (>= 0),
  dopłata ze znakiem (+ wpłata, - wypłata, domyślnie 0), notatka.
- Kilka wpisów jednego aktywa w tym samym dniu jest dozwolone.
  Porządek: (data, id). Wartość = wartość ostatniego wpisu,
  dopłaty z tego dnia się sumują.
- Wpis z datą z przyszłości jest niedozwolony ("dziś" z `DateProvider`).

### Obliczenia
- Wartość aktywa w dniu X = wartość ostatniego wpisu o dacie <= X.
  Brak takiego wpisu = aktywo jeszcze nie istnieje (w sumach liczone jako 0).
- Majątek netto w dniu X = suma wartości wszystkich aktywów w dniu X.
- Suma dopłat netto w dniu X = suma dopłat ze wpisów o dacie <= X.
- Zysk = wartość - suma dopłat netto (dla aktywa i dla całości).
  To prosta różnica, nie stopa zwrotu.
- Wartość początkowa przy tworzeniu aktywa = pierwszy wpis; domyślnie
  liczona jako dopłata w tej samej kwocie (przełącznik). Gdy wynosi 0,
  pierwszy wpis nie powstaje.
- Zamknięcie aktywa (archiwizacja) = wpis z wartością 0 i wypłatą całej
  ostatniej wartości, potem flaga isArchived. Dzięki temu majątek i zysk
  zrealizowany liczą się poprawnie bez specjalnych wyjątków. Odarchiwizacja
  tylko zdejmuje flagę.

### Styl kodu
- Identyfikatory jako value class (`AssetId`, `EntryId`, `CategoryId`).
- Osobne typy szkiców (`AssetDraft`, `EntryDraft`) bez id vs. modele zapisane.
- Use case'y: klasa z `operator fun invoke`, błędy walidacji zwracane jako
  `Outcome.Failure(sealed error)`, nie wyjątki.
- Domena nie importuje androidx.*, Compose, Room ani niczego z platformy.
- Podejście TDD: najpierw testy (kotlin.test, kotlinx-coroutines-test,
  Turbine dla Flow), potem implementacja. Nazwy testów opisowe.
- Każdy krok kończy się: `./gradlew spotlessCheck detekt :core:common:allTests
  :domain:allTests` na zielono.
