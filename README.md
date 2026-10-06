This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`
- Domain KMP tests: `./gradlew :domain:allTests`

## Architektura

Aurelvio wykorzystuje architekturę wielomodułową (Modular Clean Architecture) opartą na Kotlin Multiplatform i Compose Multiplatform. Zależności kierują się do wewnątrz: `feature -> domain <- data`.

### Istniejące moduły

| Moduł | Ścieżka Gradle | Opis / Odpowiedzialność | Dopuszczalne zależności |
| :--- | :--- | :--- | :--- |
| **`core:common`** | `:core:common` | Wspólne narzędzia, modele bazowe, uniwersalne rozszerzenia. | Brak wewnętrznych modułów |
| **`domain`** | `:domain` | Czysta logika biznesowa, kalkulacje wartości majątku (time series), interfejsy repozytoriów. | `:core:common` |
| **`core:testing`** | `:core:testing` | Narzędzia testowe, fake'i i dyspozytorzy dla testów jednostkowych. | `:core:common` |
| **`shared`** | `:shared` | Punkt wejścia KMP/CMP, spina nawigację, DI oraz framework dla iOS. | `:domain`, `:core:common` |
| **`androidApp`** | `:androidApp` | Aplikacja Android (punkt wejścia Activity). | `:shared` |

### Reguły zależności
1. Moduł **`:domain`** nie posiada zależności do Compose, Room, Android SDK ani innych narzędzi UI/bazodanowych.
2. Moduł **`:core:testing`** zależy wyłącznie od **`:core:common`** (nie zależy od **`:domain`**).
3. Testy jednostkowe w **`:domain`** wykorzystują **`:core:testing`** w `commonTest`.

### Planowane moduły
- **`data`** — implementacja repozytoriów, baza danych Room, DataStore.
- **`core:designsystem`** — komponenty UI, motyw Material 3, kolory, typografia.
- **`feature:*`** — poszczególne ekrany i funkcje aplikacji (np. `dashboard`, `assets`, `entry`, `quickupdate`, `history`, `charts`, `settings`, `backup`, `lock`, `onboarding`).

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
