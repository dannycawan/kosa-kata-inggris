# Release Checklist

## Build

- [ ] GitHub Actions `Android Build` berhasil.
- [ ] Unit test berhasil.
- [ ] Debug APK berhasil dibuat.
- [ ] Release AAB berhasil dibuat.
- [ ] `compileSdk` dan `targetSdk` tetap 36.
- [ ] `minSdk` tetap 23.
- [ ] `versionCode` dinaikkan untuk release berikutnya.
- [ ] `versionName` diperbarui bila diperlukan.

## Signing

- [ ] Buat upload key dan simpan di lokasi aman.
- [ ] Konfigurasikan release signing tanpa commit password atau keystore.
- [ ] Aktifkan Play App Signing.
- [ ] Build signed AAB untuk Play Console.

## Ads dan Privasi

- [ ] Ganti AdMob test App ID dengan production App ID.
- [ ] Ganti banner dan interstitial test unit IDs.
- [ ] Pastikan iklan tidak tampil saat flashcard, quiz aktif, atau listening.
- [ ] Publikasikan privacy policy.
- [ ] Siapkan consent flow yang sesuai wilayah distribusi.

## Permission

- [ ] Manifest hanya memakai `INTERNET` dan `ACCESS_NETWORK_STATE`.
- [ ] Tidak ada permission kamera, mikrofon, lokasi, kontak, atau storage.

## Play Store

- [ ] Lengkapi judul, deskripsi, icon, feature graphic, dan screenshot.
- [ ] Isi Data safety berdasarkan perilaku SDK terbaru.
- [ ] Upload AAB ke Internal testing.
- [ ] Uji instalasi dan upgrade dari Internal testing.
- [ ] Uji perangkat API 23 dan perangkat target terbaru.
- [ ] Verifikasi targetSdk 36 di Play Console.

## Quality

- [ ] Uji first launch dan import 3.000 vocabulary.
- [ ] Uji app tetap bekerja offline setelah import.
- [ ] Uji TTS tersedia dan tidak tersedia.
- [ ] Uji review scheduler dan streak dengan perubahan tanggal.
- [ ] Uji reset progress lokal.
- [ ] Review kualitas arti dan contoh kalimat.
