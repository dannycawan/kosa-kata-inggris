# GitHub Build Guide

## Membuat Repository

1. Masuk ke GitHub dan pilih **New repository**.
2. Gunakan nama repository, misalnya `kosa-kata-inggris`.
3. Jangan menambahkan project Android lain ke repository yang sama.
4. Push seluruh isi folder project, termasuk `.github`, `gradle`, dan `gradlew`.

Repository project saat ini:

```text
https://github.com/dannycawan/kosa-kata-inggris
```

## Push Project

Contoh perintah dari folder root project:

```powershell
git init
git branch -M main
git remote add origin https://github.com/dannycawan/kosa-kata-inggris.git
git add .
git commit -m "Build stable Android MVP"
git push -u origin main
```

Jangan commit `local.properties`, folder `.gradle`, `.idea`, atau output `build`.

## Menjalankan GitHub Actions

Workflow berada di:

```text
.github/workflows/android-build.yml
```

Workflow otomatis berjalan pada push dan pull request. Untuk menjalankan manual:

1. Buka tab **Actions**.
2. Pilih **Android Build**.
3. Klik **Run workflow**.
4. Pilih branch yang akan dibuild.
5. Klik tombol **Run workflow**.

Workflow menggunakan JDK 17 dan menjalankan:

```text
./gradlew clean
./gradlew testDebugUnitTest
./gradlew assembleDebug
./gradlew bundleRelease
```

## Mengunduh APK

1. Buka run **Android Build** yang berhasil.
2. Gulir ke bagian **Artifacts**.
3. Unduh `3000-kosakata-inggris-debug-apk`.
4. Ekstrak ZIP artifact untuk mendapatkan `app-debug.apk`.

Path APK di runner:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Mengunduh AAB

1. Buka run **Android Build** yang berhasil.
2. Gulir ke bagian **Artifacts**.
3. Unduh `3000-kosakata-inggris-release-aab-unsigned`.
4. Ekstrak ZIP artifact untuk mendapatkan `app-release.aab`.

Path AAB di runner:

```text
app/build/outputs/bundle/release/app-release.aab
```

AAB ini belum ditandatangani untuk upload Play Store. Upload key dan konfigurasi
signing harus ditambahkan menjelang release.

## Jika Build Gagal

1. Buka run yang gagal di tab **Actions**.
2. Buka job **Test, APK, and AAB**.
3. Cari step merah pertama, bukan hanya pesan error terakhir.
4. Periksa apakah error berasal dari Gradle sync, Kotlin compile, resource,
   Room/KSP, unit test, APK, atau AAB.
5. Perbaiki project pada branch baru dan push kembali.
6. Jangan menonaktifkan test atau menghapus fitur hanya untuk membuat CI hijau.

Masalah umum:

- Pastikan `gradlew` dan `gradle/wrapper/gradle-wrapper.jar` ikut ter-commit.
- Pastikan workflow memakai JDK 17.
- Pastikan Android SDK dan dependency menggunakan versi stabil yang kompatibel.
- Pastikan JSON vocabulary tetap valid.
- Pastikan artifact path sama dengan output Gradle.
