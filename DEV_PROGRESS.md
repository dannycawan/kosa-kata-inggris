<!--
Tujuan: Menyimpan snapshot task aktif dan handoff terbaru untuk agent berikutnya.
Caller: Semua agent sebelum melanjutkan pekerjaan repository.
Dependensi: AGENTS.md, SYSTEM_MAP.md, source code, dan hasil validasi lokal.
Main Functions: Status task, fakta terverifikasi, pekerjaan selesai, langkah berikutnya, dan validation status.
Side Effects: Diperbarui setiap milestone atau perubahan penting.
-->

# Active Task

- Upgrade aplikasi menjadi Version 2 UX: dashboard profesional, flashcard/TTS/listening V2, target dan delay custom, quiz/review/bookmark/profile/settings yang lebih kaya, bottom navigation baru, serta aturan iklan yang diperketat.

# Current Status

- **Partially Completed** (task selesai, validasi Gradle belum dijalankan)
- Implementasi Version 2 UX sudah ditulis: UI V2, TTS/listening V2, bottom nav baru, settings custom, statistik tambahan, aturan ads baru.
- **Perubahan kondisi**: C: 2.05 GB free, D: 9.28 GB free (sebelumnya 0). Cache Gradle masih perlu diverifikasi.
- **SYSTEM_MAP.md belum diupdate** — masih mencerminkan struktur pra-V2 (tidak mencantumkan `CategoryProgress.kt`, `VocabComponents.kt`, bottom nav V2, aturan banner V2).
- **CODEX_PROMPT_LANJUTAN.md** konsisten sebagai prompt awal task ini (UI/UX Polish = Version 2 UX).

# What Has Been Confirmed

- Project adalah single-module Android app `:app` dengan Kotlin, Jetpack Compose, Room, DataStore, TextToSpeech, dan Google Mobile Ads.
- Entrypoint aplikasi adalah `VocabApplication` dan `MainActivity`.
- Flow utama adalah Compose screen -> `VocabRepository` -> repository khusus -> DAO Room/DataStore.
- Dataset vocabulary berlokasi di `app/src/main/assets/vocabulary_words.json` dan di-seed oleh `VocabularySeeder`.
- Semua file Kotlin utama saat pemetaan berukuran kurang dari 500 baris.
- User meminta mempertahankan package `com.kosakata.inggris`, SDK 36/36/23, dan arsitektur database Room.
- Banner hanya boleh berada di Home dan Profile (V2); interstitial hanya setelah Quiz Result.
- File baru V2: `CategoryProgress.kt`, `VocabComponents.kt`.
- Sekarang ada ruang disk untuk menjalankan Gradle (C: 2GB, D: 9GB free).

# What Has Been Confirmed (Original - Masih Berlaku)

- Project adalah single-module Android app `:app` dengan Kotlin, Jetpack Compose, Room, DataStore, TextToSpeech, dan Google Mobile Ads.
- Entrypoint aplikasi adalah `VocabApplication` dan `MainActivity`.
- Flow utama adalah Compose screen -> `VocabRepository` -> repository khusus -> DAO Room/DataStore.
- Dataset vocabulary berlokasi di `app/src/main/assets/vocabulary_words.json` dan di-seed oleh `VocabularySeeder`.
- Semua file Kotlin utama saat pemetaan berukuran kurang dari 500 baris.
- User meminta mempertahankan package `com.kosakata.inggris`, SDK 36/36/23, dan arsitektur database Room.
- Banner hanya boleh berada di Home dan Profile; interstitial hanya setelah Quiz Result.

# Work Completed

- Membaca request Version 2 UX dari CODEX_PROMPT_LANJUTAN.md.
- Membaca `SYSTEM_MAP.md` dan full source code.
- Menambahkan design system Material 3 V2 dan komponen UI bersama (`VocabTheme.kt`, `VocabComponents.kt`).
- Mendesain ulang Home, Category, Learning, Listening, Quiz, Quiz Result, Review, Bookmark, Profile, Settings, dan Onboarding.
- Mengubah bottom navigation menjadi Home, Belajar, Dengarkan, Quiz, Profil.
- Menghapus banner dari Quiz Result dan menghapus interstitial sebelum Quiz.
- Menambah TTS bilingual, kecepatan Fast, listening stage 5 langkah, previous/next, repeat custom, target/delay custom, bookmark count, review tomorrow count, progress kategori, words heard, dan longest streak.
- Menambah `CategoryProgress.kt` di `data/local/model/` untuk agregasi progres per kategori.

# In Progress

- **Validasi build/test belum dijalankan** karena sebelumnya disk/blokir. Sekarang disk sudah longgar, perlu dicoba.
- **SYSTEM_MAP.md perlu diupdate** untuk mencerminkan struktur V2.

# Next Exact Steps

1. Hapus/timpa cache Gradle korup: `Remove-Item -Recurse -Force "$env:USERPROFILE\.gradle\caches\journal-1"`
2. Jalankan `.\gradlew.bat testDebugUnitTest`
3. Jika test lolos: `.\gradlew.bat clean`, `.\gradlew.bat assembleDebug`, `.\gradlew.bat bundleRelease`
4. Update `SYSTEM_MAP.md` agar mencerminkan arsitektur V2:
   - Tambah entry `data/local/model/CategoryProgress.kt` di Module Map
   - Tambah entry `ui/components/VocabComponents.kt` di Module Map
   - Update bottom navigation V2 (Home, Belajar, Dengarkan, Quiz, Profil)
   - Update aturan banner (Hanya Home & Profile)
   - Update header desain system V2

# Files Already Read

- CODEX_PROMPT_LANJUTAN.md (prompt task)
- SYSTEM_MAP.md
- DEV_PROGRESS.md (saat ini)
- Semua file .kt di project (verifikasi langsung isi)

# Files Modified (V2)

- `DEV_PROGRESS.md` (update ini)
- `app/src/main/java/com/kosakata/inggris/ui/components/VocabComponents.kt` — komponen Material 3 reusable V2 (BARU)
- `app/src/main/java/com/kosakata/inggris/ui/theme/VocabTheme.kt` — design system V2
- `app/src/main/java/com/kosakata/inggris/navigation/VocabNavigation.kt` — bottom nav V2 dan aturan interstitial
- `app/src/main/java/com/kosakata/inggris/ui/screens/*.kt` — redesign seluruh screen
- `app/src/main/java/com/kosakata/inggris/audio/*.kt` — TTS dan listening manager V2
- `app/src/main/java/com/kosakata/inggris/data/preferences/UserPreferences.kt` — target/repeat/delay tervalidasi, longest streak, words heard
- `app/src/main/java/com/kosakata/inggris/data/repository/*.kt`, `data/local/dao/*.kt`
- `app/src/main/java/com/kosakata/inggris/data/local/model/CategoryProgress.kt` — model agregasi progres (BARU)
- `app/src/main/res/values/strings.xml`, `colors.xml`, `styles.xml`

# Important Functions / Flows Touched

- Flow target: `VocabNavigation -> screen -> VocabRepository/UserPreferences -> Room/DataStore`
- Flow audio: screen -> `ListeningSessionManager`/`TtsManager` -> Android TextToSpeech
- Flow ads: banner hanya Home/Profile; interstitial hanya pada aksi keluar dari QuizResult

# Decisions Made

- Schema Room tidak akan diubah.
- Statistik tambahan yang tidak cocok untuk Room akan memakai DataStore atau agregasi query yang sudah ada.
- Query kategori memakai aggregate read-only dengan join `vocabulary_words` ke `user_word_progress`, tanpa schema migration.

# Errors / Blockers

- Sebelumnya: disk C: dan D: free space 0 dan cache Gradle korup.
- **Sekarang: C: 2.05 GB free, D: 9.28 GB free** — ada cukup ruang.
- Cache Gradle masih belum diverifikasi — perlu dihapus dan dicoba.
- Build/test belum pernah berhasil dijalankan untuk kode V2 — masih ada potensi compile error.

# Validation Status

- Build: Not run (sebelumnya Blocked karena disk/cache)
- Test: Not run
- Lint: Not run
- Manual Check: Not run

# Do Not Repeat

- Jangan ubah schema Room untuk statistik UX.
- Jangan membaca penuh dataset 3.000 kata; gunakan DAO dan metadata yang ada.
- SYSTEM_MAP.md perlu diupdate setelah validasi Gradle berhasil.

# Resume Note for Next Agent

Task Version 2 UX sudah diimplementasikan penuh di source code. Namun:
1. **SYSTEM_MAP.md masih mencerminkan struktur pra-V2** — perlu diupdate.
2. **Validasi Gradle belum dijalankan** karena sebelumnya blokir disk. Sekarang C: 2GB/D: 9GB free, jadi bisa dicoba.
3. Cache Gradle (`journal-1`) mungkin perlu dihapus dulu sebelum build.
4. Ada potensi compile error yang perlu diperbaiki setelah build pertama.
5. Langkah pertama yang disarankan: hapus cache Gradle, lalu `.\gradlew.bat testDebugUnitTest`.
