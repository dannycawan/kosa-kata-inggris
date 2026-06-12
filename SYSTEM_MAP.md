<!--
Tujuan: Menjadi peta navigasi utama arsitektur, flow fungsi, data, config, dan integrasi proyek.
Caller: Agent dan developer sebelum melakukan analisis atau perubahan source code.
Dependensi: Source Kotlin, Gradle config, AndroidManifest.xml, Room schema, DataStore, assets, dan workflow CI.
Main Functions: Project Summary, Core Logic Flow, Clean Tree, Module Map, Data & Config, External Integrations.
Side Effects: Tidak ada; dokumen ini harus diperbarui saat struktur atau flow utama berubah.
-->

# Project Summary

- **Tujuan aplikasi:** Aplikasi Android local-first untuk membantu pengguna Indonesia mempelajari 3.000 kosakata bahasa Inggris tanpa login, backend, atau subscription.
- **Identitas:** App `3000 Kosakata Inggris`, package/namespace/applicationId `com.kosakata.inggris`, version `1.0.0` (`versionCode 1`), `minSdk 23`, `targetSdk 36`, `compileSdk 36`.
- **Tech stack:** Kotlin/JVM 17, Android Gradle Plugin 8.10.1, Jetpack Compose Material 3, Navigation Compose, Android ViewModel, Coroutines/Flow, Room 2.6.1, DataStore Preferences 1.1.1, Android TextToSpeech, Google Mobile Ads 23.6.0, JUnit 4.
- **Penyimpanan:** Room database lokal `vocab_inggris.db`, DataStore `user_preferences`, seed JSON `app/src/main/assets/vocabulary_words.json`, dan model agregasi `CategoryProgress`.
- **Pola arsitektur:** Single-activity Compose. Navigation memanggil screen composable; screen memakai facade `VocabRepository`; facade mendelegasikan ke repository domain/data, DAO Room, DataStore, atau manager integrasi.
- **UI framework:** Jetpack Compose Material 3 Version 2 dengan design system kustom (light/dark theme, shape, komponen reusable).
- **Bottom navigation:** Home, Belajar, Dengarkan, Quiz, Profil (V2).
- **Backend/queue/cloud sync:** Not found.

# Core Logic Flow (Function-Level Flowchart)

## Startup dan First Open

`Android launcher -> MainActivity.onCreate -> AppDatabase.getInstance + VocabRepository + VocabViewModelFactory.create -> VocabViewModel.init -> VocabRepository.seedIfNeeded -> VocabularyRepository.seedIfNeeded -> VocabularySeeder.seedIfNeeded -> VocabularyDao.countWords/insertAll -> Room vocab_inggris.db`

`Application startup -> VocabApplication.onCreate -> MobileAds.initialize -> Google Mobile Ads SDK`

`VocabNavigation[gate] -> UserPreferences.firstOpenDone -> onboarding | home`

## Onboarding dan Preferensi

`OnboardingScreen[Mulai] -> UserPreferences.setGoal/setCategory/setDailyTarget/setFirstOpenDone -> DataStore user_preferences -> VocabNavigation[home]`

`SettingsScreen -> UserPreferences.setDailyTarget/setAudioAccent/setAudioSpeed/setRepeatCount/setListeningDelay -> DataStore user_preferences`

## Sesi Belajar

`VocabNavigation[learn] -> LearningSessionScreen -> VocabRepository.getTodayWords -> UserProgressRepository.getDueReviewWordIds -> UserProgressDao.getDueReviewWordIds -> Room`

`VocabRepository.getTodayWords -> VocabularyRepository.getWordsByIds/getNewWordsByCategory -> VocabularyDao -> Room`

`LearningSessionScreen[Masih Belajar/Sudah Tahu] -> VocabRepository.markSeen -> UserProgressRepository.recordAnswer -> ReviewScheduler.tomorrow/nextReviewAt -> UserProgressDao.upsert -> Room`

`LearningSessionScreen[selesai] -> VocabRepository.saveSession -> LearningSessionRepository.save -> LearningSessionDao.insert -> Room`

`VocabRepository.saveSession -> UserPreferences.recordStudyDay -> DataStore user_preferences`

## Quiz

`LearningSessionScreen[selesai] -> VocabViewModel.setCurrentSession -> InterstitialAdManager.showIfReady -> VocabNavigation[quiz]`

`QuizScreen -> QuizGenerator.generateMixed/generateOne + VocabRepository.getRandomWords -> VocabularyDao.getRandomWords -> Room`

`QuizScreen[jawaban] -> VocabRepository.updateQuizProgress -> UserProgressRepository.recordAnswer -> UserProgressDao.upsert -> Room`

`QuizScreen[selesai] -> VocabRepository.saveSession -> LearningSessionDao.insert + UserPreferences.recordStudyDay -> Room/DataStore -> VocabViewModel.setQuizResult -> QuizResultScreen`

## Review dan Bookmark

`ReviewScreen -> VocabRepository.getReviewWords -> UserProgressDao.getDueReviewWordIds -> VocabularyDao.getWordsByIds -> Room`

`ReviewScreen[fallback] -> VocabRepository.getDifficultWords -> UserProgressDao.getDifficultWordIds -> VocabularyDao.getWordsByIds -> Room`

`BookmarkScreen -> VocabRepository.getBookmarkedWords/toggleBookmark -> UserProgressRepository -> UserProgressDao -> Room`

## Listening dan TTS

`ListeningScreen -> VocabRepository.getTodayWords -> Room -> ListeningSessionManager.start/resume/next -> TtsManager.playWord/playMeaning/playExample -> Android TextToSpeech`

`LearningSessionScreen/ReviewScreen/BookmarkScreen -> TtsManager.playWord/playExample -> Android TextToSpeech`

## Dashboard, Profile, dan Reset

`HomeScreen/ProfileScreen -> VocabRepository.observe* -> UserProgressRepository/VocabularyRepository -> Room Flow`

`SettingsScreen[reset] -> VocabRepository.resetProgress -> UserProgressDao.resetProgress + LearningSessionDao.resetSessions + UserPreferences.setStreak -> Room/DataStore`

## Ads (V2)

`VocabApplication.onCreate -> MobileAds.initialize`

`HomeScreen/ProfileScreen -> BannerAdView -> AdView.loadAd -> Google Mobile Ads`

`MainActivity.onCreate -> InterstitialAdManager.load -> QuizResult exit action -> InterstitialAdManager.showIfReady -> Google Mobile Ads`

# Clean Tree

```text
.
|-- .github/
|   `-- workflows/android-build.yml
|-- app/
|   |-- build.gradle.kts
|   `-- src/
|       |-- main/
|       |   |-- AndroidManifest.xml
|       |   |-- assets/vocabulary_words.json
|       |   |-- java/com/kosakata/inggris/
|       |   |   |-- MainActivity.kt
|       |   |   |-- VocabApplication.kt
|       |   |   |-- ads/
|       |   |   |-- audio/
|       |   |   |-- data/
|       |   |   |   |-- local/
|       |   |   |   |   |-- dao/
|       |   |   |   |   |-- entity/
|       |   |   |   |   `-- model/
|       |   |   |   |-- preferences/
|       |   |   |   |-- repository/
|       |   |   |   `-- seed/
|       |   |   |-- domain/
|       |   |   |   |-- model/
|       |   |   |   `-- review/
|       |   |   |-- navigation/
|       |   |   `-- ui/
|       |   |       |-- components/
|       |   |       |-- screens/
|       |   |       `-- theme/
|       |   `-- res/
|       |       |-- drawable/
|       |       |-- mipmap-anydpi/
|       |       |-- mipmap-anydpi-v26/
|       |       |-- values/
|       |       `-- xml/
|       `-- test/java/com/kosakata/inggris/domain/review/
|-- build.gradle.kts
|-- gradle/wrapper/
|-- gradle.properties
|-- settings.gradle.kts
|-- AGENTS.md
|-- DEV_PROGRESS.md
`-- SYSTEM_MAP.md
```

# Module Map (The Chapters)

## App Entry dan Navigation

| Path | Fungsi/class utama | Peran |
|---|---|---|
| `app/src/main/java/com/kosakata/inggris/VocabApplication.kt` | `VocabApplication.onCreate` | Menginisialisasi Google Mobile Ads saat proses aplikasi mulai. |
| `app/src/main/java/com/kosakata/inggris/MainActivity.kt` | `MainActivity.onCreate/onDestroy` | Membuat dependency root, ViewModel, TTS, interstitial ads, theme, dan navigation host. |
| `app/src/main/java/com/kosakata/inggris/navigation/VocabNavigation.kt` | `VocabNavigation` | Mendefinisikan gate first-open, route Compose, bottom navigation V2 (Home, Belajar, Dengarkan, Quiz, Profil), dan interstitial hanya saat keluar dari QuizResult. |
| `app/src/main/java/com/kosakata/inggris/ui/VocabViewModel.kt` | `VocabViewModel`, `VocabViewModelFactory` | Memulai seeding dan menyimpan kata sesi aktif serta hasil quiz selama lifecycle Activity. |
| `app/src/main/java/com/kosakata/inggris/ui/theme/VocabTheme.kt` | `VocabTheme` | Design system Material 3 Version 2 untuk light dan dark mode. |
| `app/src/main/java/com/kosakata/inggris/ui/components/VocabComponents.kt` | `ScreenTitle`, `DashboardActionCard`, `StatCard`, `EmptyState`, `NumericInputDialog` | Komponen UI reusable V2 untuk seluruh screen. |

## UI Screens

| Path | Fungsi utama | Peran |
|---|---|---|
| `ui/screens/OnboardingScreen.kt` | `OnboardingScreen` | Menyimpan tujuan, kategori awal, target harian, dan status first-open. |
| `ui/screens/HomeScreen.kt` | `HomeScreen` | Dashboard V2: statistik progress, target harian, streak, words heard, CTA belajar/listening/review, dan banner ad. |
| `ui/screens/CategoryScreen.kt` | `CategoryScreen` | Memilih kategori vocabulary dengan progres per kategori dan membuka sesi belajar. |
| `ui/screens/LearningSessionScreen.kt` | `LearningSessionScreen` | Flashcard V2: due-review-first, TTS bilingual, bookmark, tombol previous/next, target/delay custom, sesi belajar. |
| `ui/screens/ListeningScreen.kt` | `ListeningScreen` | Listening V2: 5 stage autoplay (kata-arti-kata-contoh-kata), previous/next, repeat custom, delay custom, progress feedback. |
| `ui/screens/QuizScreen.kt` | `QuizScreen` | Membuat soal campuran, merekam hasil per kata, menyimpan sesi quiz, dan mengirim skor. |
| `ui/screens/QuizResultScreen.kt` | `QuizResultScreen` | Menampilkan skor, navigasi lanjut/review/home, **tanpa** banner ad. |
| `ui/screens/ReviewScreen.kt` | `ReviewScreen` | Mereview kata jatuh tempo, atau kata sering salah sebagai fallback. |
| `ui/screens/BookmarkScreen.kt` | `BookmarkScreen` | Menampilkan, membunyikan, dan menghapus bookmark. |
| `ui/screens/ProfileScreen.kt` | `ProfileScreen` | Dashboard profil V2: statistik komprehensif (kata dipelajari, dikuasai, streak terpanjang, bookmark count, review tomorrow), setting aktif, shortcut, banner ad. |
| `ui/screens/SettingsScreen.kt` | `SettingsScreen` | V2: target harian custom, kecepatan audio (Normal/Fast), aksen (US/UK), repeat count, listening delay, dan reset progress. |

Semua path UI di atas berada di `app/src/main/java/com/kosakata/inggris/`.

## Data, Domain, dan Storage

| Path | Fungsi/class utama | Peran |
|---|---|---|
| `data/repository/VocabRepository.kt` | `VocabRepository` | Facade utama screen untuk vocabulary, progress, session, preferences, dan seeding. |
| `data/repository/VocabularyRepository.kt` | `VocabularyRepository` | Mengatur seeding terserialisasi serta operasi baca vocabulary melalui `VocabularyDao`. |
| `data/repository/UserProgressRepository.kt` | `UserProgressRepository.recordAnswer/toggleBookmark` | Menerapkan status review/mastery dan menyimpan progress per kata. |
| `data/repository/LearningSessionRepository.kt` | `LearningSessionRepository.save/observeRecent/reset` | Membaca dan menulis histori sesi belajar. |
| `data/local/AppDatabase.kt` | `AppDatabase.getInstance` | Singleton Room database version 1 dan provider tiga DAO. |
| `data/local/dao/VocabularyDao.kt` | Query vocabulary dan `insertAll` | Akses tabel `vocabulary_words`. |
| `data/local/dao/UserProgressDao.kt` | Query due/bookmark/difficult/statistics dan `upsert` | Akses tabel `user_word_progress`. |
| `data/local/dao/LearningSessionDao.kt` | `insert/observeRecentSessions/resetSessions` | Akses tabel `learning_sessions`. |
| `data/local/entity/VocabularyWordEntity.kt` | `VocabularyWordEntity` | Entity vocabulary dengan index `word`, `level`, dan `isCore500`. |
| `data/local/entity/UserWordProgressEntity.kt` | `UserWordProgressEntity` | Entity progress per `wordId` dengan index status, jadwal review, dan bookmark. |
| `data/local/entity/LearningSessionEntity.kt` | `LearningSessionEntity` | Entity histori sesi dan skor. |
| `data/local/model/CategoryProgress.kt` | `CategoryProgress` | Model agregasi read-only: totalWords dan completedWords per kategori. |
| `data/preferences/UserPreferences.kt` | `UserPreferences`, `recordStudyDay` | Flow dan write API untuk DataStore preferences, kalkulasi streak, longest streak, dan words heard V2. |
| `data/seed/VocabularySeeder.kt` | `VocabularySeeder.seedIfNeeded` | Membaca 3.000 entri JSON dan insert per batch 500 dalam satu transaksi. |
| `domain/review/ReviewScheduler.kt` | `nextReviewAt/tomorrow` | Menentukan interval review 1, 3, 7, dan 14 hari hingga mastered. |
| `domain/review/QuizGenerator.kt` | `generateMixed/generateOne` | Membuat pertanyaan tebak arti/kata dengan empat opsi unik bila distractor cukup. |
| `domain/model/QuizQuestion.kt` | `QuizType`, `QuizQuestion` | Model quiz non-persisten. |

Semua path data/domain di atas berada di `app/src/main/java/com/kosakata/inggris/`.

## Audio dan Ads

| Path | Fungsi/class utama | Peran |
|---|---|---|
| `audio/TtsManager.kt` | `TtsManager.playWord/playMeaning/playExample/speak` | TTS V2: bilingual EN/ID, kecepatan Normal/Fast, aksen US/UK, dan mode slow. |
| `audio/ListeningSessionManager.kt` | `start/pause/resume/stop/next/release` | State machine autoplay V2: 5 stage (kata -> arti -> kata -> contoh -> kata), repeat custom, delay custom, previous/next. |
| `ads/BannerAdView.kt` | `BannerAdView` | Compose wrapper untuk banner test ad, hanya ditampilkan di Home dan Profile. |
| `ads/InterstitialAdManager.kt` | `load/showIfReady` | Memuat dan menampilkan interstitial test ad hanya saat navigasi keluar dari QuizResult. |

Semua path audio/ads di atas berada di `app/src/main/java/com/kosakata/inggris/`.

## Test dan Build

| Path | Fungsi utama | Peran |
|---|---|---|
| `app/src/test/.../ReviewSchedulerTest.kt` | Dua unit test scheduler | Memverifikasi interval benar dan jadwal jawaban salah. |
| `app/src/test/.../QuizGeneratorTest.kt` | Dua unit test generator | Memverifikasi empat opsi unik dan jawaban benar selalu tersedia. |
| `.github/workflows/android-build.yml` | Job `build` | Menjalankan clean, unit test, debug APK, release AAB, dan upload artifact. |
| `app/build.gradle.kts` | Android config/dependencies | Konfigurasi SDK, JVM 17, Compose, Room/KSP, DataStore, Ads, dan JUnit. |

# Data & Config

## Environment dan Config

- `.env*`: Not found.
- `local.properties`: Di-ignore dan tidak ada dalam source tree saat pemetaan; biasanya berisi lokasi Android SDK lokal.
- Root build config: `build.gradle.kts`, `settings.gradle.kts`, `gradle.properties`.
- App config: `app/build.gradle.kts`.
- Runtime manifest: `app/src/main/AndroidManifest.xml`.
- UI resources: `app/src/main/res/values/{strings.xml,colors.xml,styles.xml}`.
- Backup config: `app/src/main/res/xml/{backup_rules.xml,data_extraction_rules.xml}`.
- CI: `.github/workflows/android-build.yml`.

## Skema Data

### `vocabulary_words`

- Primary key: `id`.
- Field inti: kata, jenis kata, level, arti Indonesia, contoh EN/ID, kategori pipe-separated, flag core 500.
- Index: `word`, `level`, `isCore500`.
- Sumber awal: `app/src/main/assets/vocabulary_words.json` berisi 3.000 item.

### `user_word_progress`

- Primary key: `wordId`; relasi logis satu-ke-satu ke `vocabulary_words.id`.
- Tidak ada Room foreign key eksplisit.
- Menyimpan status `NEW/LEARNING/REVIEW/MASTERED`, correct/wrong count, waktu review, dan bookmark.
- Index: `status`, `nextReviewAt`, `isBookmarked`.

### `learning_sessions`

- Primary key auto-generated: `id`.
- Menyimpan waktu mulai/selesai, kategori, total kata, jawaban benar, dan jawaban salah.
- Tidak memiliki relasi langsung ke tabel vocabulary.

### DataStore `user_preferences`

- Menyimpan daily target, selected goal/category, streak, last study epoch-day, accent/speed audio, repeat, listening delay, dan first-open flag.

## Migration dan Seed

- Room migrations: Not found; database masih version 1.
- Seed: `VocabularySeeder.seedIfNeeded` dari `assets/vocabulary_words.json`.
- Seed berjalan hanya jika tabel vocabulary kosong, dalam satu transaksi, batch 500.

## Output dan Runtime Artifacts

- Local build: `app/build/` dan root `build/` (di-ignore).
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.
- Unsigned release AAB: `app/build/outputs/bundle/release/app-release.aab`.
- CI artifact names: `3000-kosakata-inggris-debug-apk` dan `3000-kosakata-inggris-release-aab-unsigned`.
- Runtime DB/DataStore berada di private app storage Android.

# External Integrations

| Integrasi | Modul pemanggil | Catatan |
|---|---|---|
| Android TextToSpeech | `TtsManager`, `ListeningSessionManager`, screen belajar/listening/review/bookmark | Memakai suara perangkat; kualitas dan ketersediaan locale bergantung engine terpasang. |
| Google Mobile Ads SDK | `VocabApplication`, `BannerAdView`, `InterstitialAdManager` | Saat ini memakai test IDs. Banner hanya di Home & Profile. Interstitial hanya saat keluar dari QuizResult. |
| GitHub Actions | `.github/workflows/android-build.yml` | Build cloud untuk unit test, APK, dan AAB. |

API HTTP aplikasi, backend, Firebase, authentication, payment, queue, dan cloud database: Not found.

# Risks / Blind Spots

- Semua file Kotlin saat pemetaan berukuran di bawah 500 baris dan flow utama telah ditelusuri; resource visual tidak diperiksa pixel-by-pixel.
- `categories LIKE '%...%'` pada string kategori pipe-separated tidak dapat memakai index secara efektif dan berpotensi full scan ketika dataset tumbuh.
- Query `ORDER BY RANDOM()` membaca/mengacak kandidat dan akan makin mahal pada dataset besar.
- `getTodayWords` membaca due IDs lalu vocabulary dalam query kedua; hasil `WHERE id IN (...)` tidak menjamin urutan due yang sama dengan `nextReviewAt`.
- `VocabularyRepository.seedIfNeeded` mencatat error dengan Log tetapi tidak mempropagasi kegagalan ke UI; screen dapat tampak kosong tanpa error state rinci.
- Tidak ada Room migration dan `exportSchema = false`; perubahan schema berikutnya perlu migration eksplisit sebelum release.
- Relasi `user_word_progress.wordId -> vocabulary_words.id` hanya relasi logis, tanpa foreign key database.
- `lastStudyDate` bernama timestamp tetapi menyimpan local epoch-day; perubahan timezone dapat memengaruhi streak.
- Quiz mengasumsikan distractor cukup untuk empat opsi; unit test hanya mencakup input dengan lima kata unik.
- Ads masih test IDs dan consent flow produksi belum ditemukan.
- Instrumented/UI tests untuk Room seeding, DataStore, navigation, audio, dan ads: Not found.
- Git metadata tidak tersedia sebagai repository pada workspace ini saat pemetaan, sehingga status branch/commit tidak dapat dikonfirmasi.
