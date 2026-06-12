# 3000 Kosakata Inggris

Aplikasi Android local-first untuk membantu pengguna Indonesia mempelajari
kosakata bahasa Inggris tanpa login, backend, atau subscription.

## Identitas Project

- App name: **3000 Kosakata Inggris**
- Package, namespace, applicationId: `com.kosakata.inggris`
- Version: `1.0.0` (`versionCode 1`)
- `compileSdk 36`
- `targetSdk 36`
- `minSdk 23`

## Tech Stack

- Kotlin dan Coroutines/Flow
- Jetpack Compose dan Material 3
- Navigation Compose
- ViewModel
- Room Database
- DataStore Preferences
- Android TextToSpeech
- Google Mobile Ads SDK dengan test ad units

## Data Vocabulary

Dataset tersedia di:

```text
app/src/main/assets/vocabulary_words.json
```

File berisi 3.000 entri tanpa ID duplikat dan 500 kata bertanda `isCore500`.
Field setiap entri:

```text
id, word, partOfSpeech, level, meaningId, exampleEn, exampleId,
categories, isCore500
```

`VocabularySeeder` memeriksa tabel saat first launch. Import hanya berjalan jika
tabel kosong, dilakukan dalam satu transaksi, dan diproses per batch 500 item.

## Fitur MVP

- Onboarding tujuan belajar dan target 5, 10, atau 20 kata
- Dashboard progress, review jatuh tempo, target, dan streak
- 16 kategori vocabulary
- Sesi belajar: review jatuh tempo lebih dahulu, lalu kata baru
- Flashcard lengkap dengan arti, contoh, bookmark, dan TTS
- Listening/autoplay: kata, arti, kata ulang, contoh kalimat
- Kontrol play, pause, stop, next, repeat, dan delay
- Quiz tebak arti dan tebak kata dengan empat pilihan
- Quiz result dengan jumlah benar dan salah
- Review scheduler lokal:
  - benar 1 kali: besok
  - benar 2 kali: 3 hari
  - benar 3 kali: 7 hari
  - benar 4 kali: 14 hari
  - benar 5 kali: `MASTERED`
  - salah: `LEARNING`, hitungan benar direset, review besok
- Review kata jatuh tempo atau kata yang sering salah
- Bookmark, profile, settings, dan reset progress lokal
- Banner test ad hanya di Home, Profile, dan Quiz Result
- Interstitial test ad hanya setelah sesi belajar atau saat meninggalkan hasil quiz

## Cara Build

Prasyarat:

1. Android Studio dengan JDK 17.
2. Android SDK Platform 36 dan Build Tools yang sesuai.
3. Koneksi internet saat Gradle sync pertama untuk mengambil dependency.

Langkah:

1. Buka folder root project ini di Android Studio.
2. Pastikan Gradle JDK menggunakan JDK 17 bawaan Android Studio.
3. Jalankan **Sync Project with Gradle Files**.
4. Pilih emulator atau device API 23 ke atas.
5. Jalankan konfigurasi `app`.

Perintah CLI:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

## GitHub Actions

Workflow CI tersedia di:

```text
.github/workflows/android-build.yml
```

Workflow berjalan pada push, pull request, dan `workflow_dispatch`. Build akan
memakai JDK 17, menjalankan unit test, membuat debug APK, membuat unsigned
release AAB, lalu mengunggah:

```text
3000-kosakata-inggris-debug-apk
3000-kosakata-inggris-release-aab-unsigned
```

Panduan menjalankan workflow dan mengunduh artifact tersedia di
[GITHUB_BUILD_GUIDE.md](GITHUB_BUILD_GUIDE.md).

## Alur Test Manual

1. Hapus data aplikasi, lalu buka app dan selesaikan onboarding.
2. Pastikan Home menampilkan total 3.000 kata setelah seeding selesai.
3. Mulai belajar dan selesaikan seluruh flashcard.
4. Pastikan quiz memakai kata dari sesi yang baru diselesaikan.
5. Jawab quiz, buka Quiz Result, lalu periksa statistik Profile.
6. Simpan kata, buka Bookmark, lalu hapus bookmark.
7. Uji TTS kata dan contoh, termasuk mode Slow.
8. Uji listening play, pause, resume, stop, next, repeat, dan delay.
9. Ubah tanggal emulator untuk memeriksa review jatuh tempo dan streak.
10. Pastikan iklan tidak muncul saat flashcard, quiz, atau listening berjalan.

## AdMob

Project memakai App ID, banner unit, dan interstitial unit resmi untuk pengujian
Google. Ganti semuanya sebelum release. Jangan menguji klik pada iklan produksi.

Permission aplikasi hanya:

```text
INTERNET
ACCESS_NETWORK_STATE
```

## Known Limitations

- Listening hanya berjalan saat aplikasi terbuka; belum ada background service.
- Progress hanya tersimpan lokal dan tidak disinkronkan antarperangkat.
- Kualitas suara Indonesia bergantung pada engine TTS yang terpasang.
- Dataset perlu review editorial lanjutan untuk kualitas contoh kalimat.
- AdMob tetap memerlukan jaringan, tetapi fitur belajar utama bekerja offline.
- Release AAB belum ditandatangani; upload key disiapkan menjelang release.

## Next Steps

- Tambahkan Room migration saat schema berubah setelah rilis.
- Tambahkan instrumented test untuk seeding Room dan DataStore.
- Tambahkan state loading/error yang lebih rinci.
- Lakukan accessibility review dan pengujian di beberapa ukuran layar.
- Ganti AdMob test IDs dan siapkan consent flow sebelum rilis produksi.

Nama aplikasi dan UI publik tidak menggunakan branding Oxford.
