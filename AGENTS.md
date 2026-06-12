<!--
Tujuan: Menetapkan aturan kerja wajib untuk agent yang mengerjakan repository ini.
Caller: Semua AI agent dan developer yang melakukan analisis atau perubahan kode.
Dependensi: SYSTEM_MAP.md, DEV_PROGRESS.md, source code, build tools, dan database lokal aplikasi.
Main Functions: Aturan navigasi, dokumentasi, database/query, serta handoff lintas agent.
Side Effects: Mewajibkan pembaruan dokumentasi dan catatan progres saat repository berubah.
-->

# Aturan Navigasi dan Konteks

## Mandatory Map Check

Setiap awal sesi baru, WAJIB baca `SYSTEM_MAP.md` di root folder sebagai kompas utama arsitektur, tech stack, dan lokasi fungsi kunci. Jangan lakukan blind scan.

## Fallback Map

Jika `SYSTEM_MAP.md` belum ada atau diduga usang terhadap kondisi kode saat ini, buat atau perbarui dulu secara ringkas sebelum analisis lanjutan.

## Trace-by-Function / Trace-by-Flow

Gunakan peta untuk menentukan titik mulai, lalu telusuri alur berurutan:

`Trigger/Entry Point (UI/CLI/API/Event) -> Handler/Controller -> Business Logic/Service -> Data Access/Repository -> Database/Storage`

## Universal Layer Mapping

Jika istilah Controller/Service/Repo tidak dipakai, map ke padanan terdekat seperti Handler, Usecase, Domain, Adapter, atau DAO tanpa memaksa nama layer.

## Efisiensi Tanpa rg

Jangan gunakan `rg`. Gunakan `SYSTEM_MAP.md` dan Header Doc untuk langsung ke target.

## Universal Exclusions

Selalu abaikan folder dependensi, build, IDE, dan cache:

`node_modules`, `.venv`, `venv`, `env`, `vendor`, `target`, `.gradle`, `bin`, `obj`, `pkg`, `.git`, `.vscode`, `.idea`, `__pycache__`, `dist`, `build`, `tmp`, `coverage`, `.next`, `.nuxt`, `.cache`.

Abaikan juga artifact `*.log`, `*.lock`, `*.min.*`, dan `*.map` kecuali user secara eksplisit meminta pemeriksaan file tersebut.

## Super Efisien

Minimalkan command dan file read. Jangan baca seluruh file besar jika tidak diperlukan. Untuk file lebih dari 500 baris, baca per blok fungsi atau class terkait, bukan seluruh file kecuali diminta user.

## Pre-Edit Trace Note

Sebelum edit, tulis singkat dalam 1-2 kalimat: file target dan alur fungsi yang akan disentuh.

## Persetujuan Inisiatif

Jika ada perubahan di luar request user, wajib minta izin sebelum eksekusi.

## Modularitas

Pecah logika ke modul atau file kecil sesuai tanggung jawab (Single Responsibility). Jangan menumpuk banyak logic dalam satu file.

# Dokumentasi Wajib

## Header Doc

Setiap file yang dibuat atau diubah wajib memiliki header doc singkat di bagian paling atas file, sesuai gaya komentar bahasanya (`//`, `#`, `'`, atau `/* */`).

Isi minimal Header Doc:

- Tujuan: tujuan file atau module.
- Caller: pemanggil atau pengguna utama.
- Dependensi: service, repository, atau API utama.
- Main Functions: fungsi atau class public/utama.
- Side Effects: DB read/write, HTTP call, atau file I/O.

## Synchronized Documentation

Setiap perubahan logic wajib diikuti pembaruan Header Doc agar tetap akurat, ringkas, konsisten, dan mudah dipindai.

## Synchronized Map Update

Jika menambah atau menghapus file, atau mengubah flow fungsi utama yang tercatat, WAJIB update `SYSTEM_MAP.md` pada bagian terkait di sesi yang sama.

## Larangan

Dilarang menambah atau mengubah logic tanpa menyesuaikan Header Doc.

# Standar Database dan Query

## Minimum Cost

Rancang query dan data access dengan prinsip minimum I/O, minimum cost, dan minimum lock contention.

## Evaluasi Wajib

Selalu evaluasi:

- Cardinality dan selectivity filter.
- Pemakaian index/key.
- Join order dan join strategy.
- Dampak CPU, memory, disk, dan network.

## Anti-Boros Resource

Hindari proses berulang, temp table yang tidak perlu, write berlapis, dan N+1 query jika dapat diringkas dengan rencana query yang lebih efisien.

## Strategi Efisien Kontekstual

Pilih strategi sesuai konteks seperti upsert, merge, batch, incremental, atau query rewrite, bukan satu template untuk semua kasus.

## Scalability dan Consistency

Pastikan aman untuk data besar: transactional consistency tepat, locking minimal, dan performa stabil saat data tumbuh.

## Justifikasi DB-Heavy

Sebelum finalisasi perubahan DB-heavy, jelaskan singkat alasan efisiensi, trade-off, dan risiko performa yang dihindari.

# Kontinuitas Progress dan Handoff Lintas Agent

## Tujuan

Karena pengerjaan proyek dapat berpindah akun, AI agent, atau terputus di tengah jalan, setiap agent wajib menjaga catatan progres aktif agar sesi berikutnya dapat langsung melanjutkan tanpa mengulang analisis dari nol.

## File Wajib: DEV_PROGRESS.md

Gunakan `DEV_PROGRESS.md` di root project sebagai catatan progres pengembangan aktif sekaligus handoff resmi antar-agent.

## Mandatory Progress Check

Setiap awal sesi baru, setelah membaca `SYSTEM_MAP.md`, WAJIB baca `DEV_PROGRESS.md` sebelum melakukan analisis, eksekusi command, atau edit file.

## Fungsi Masing-Masing

- `SYSTEM_MAP.md`: peta arsitektur, struktur project, flow fungsi utama, data, config, dan integrasi.
- `DEV_PROGRESS.md`: status task aktif, progres terakhir, file yang sudah dibaca atau diubah, keputusan teknis, blocker, dan langkah lanjut paling akurat.
- Header Doc: konteks file atau module tertentu.

Jangan mencampur fungsi ketiganya.

## Fallback Progress

Jika `DEV_PROGRESS.md` belum ada:

- Buat secara ringkas setelah memahami kondisi awal proyek atau task aktif dari user.
- Jangan mengisi dengan asumsi yang tidak terverifikasi.
- Jika belum ada task aktif yang jelas, tulis status awal secara jujur.

Jika `DEV_PROGRESS.md` ada tetapi tampak usang:

- Perbarui hanya berdasarkan kondisi terbaru yang benar-benar sudah diverifikasi.
- Jangan menghapus konteks penting yang masih relevan.

## Isi Minimum DEV_PROGRESS.md

`DEV_PROGRESS.md` wajib memiliki bagian berikut:

- `# Active Task`
- `# Current Status`
- `# What Has Been Confirmed`
- `# Work Completed`
- `# In Progress`
- `# Next Exact Steps`
- `# Files Already Read`
- `# Files Modified`
- `# Important Functions / Flows Touched`
- `# Decisions Made`
- `# Errors / Blockers`
- `# Validation Status`
- `# Do Not Repeat`
- `# Resume Note for Next Agent`

Status task harus salah satu dari:

- Not Started
- Investigating
- In Progress
- Partially Completed
- Blocked
- Completed

Validation Status minimal mencatat:

- Build: Not run / Passed / Failed
- Test: Not run / Passed / Failed
- Lint: Not run / Passed / Failed
- Manual Check: Not run / Passed / Failed

## Aturan Update DEV_PROGRESS.md

`DEV_PROGRESS.md` WAJIB diperbarui:

1. Setelah agent memahami task aktif dan menentukan arah kerja.
2. Setelah menemukan fakta teknis penting.
3. Setelah melakukan perubahan file yang berarti.
4. Setelah menyelesaikan satu milestone.
5. Setelah menemukan blocker atau error penting.
6. Sebelum berpindah fokus ke area lain dalam repository.
7. Sebelum sesi berakhir jika pekerjaan belum selesai.
8. Lebih sering ketika task panjang atau konteks mulai banyak.

## Progress Snapshot Rule

`DEV_PROGRESS.md` harus diperlakukan sebagai snapshot progres terbaru, bukan sekadar catatan penutup sesi. Jangan menunggu sampai akhir sesi untuk memperbaruinya.

## Anti-Reset Rule

Agent berikutnya DILARANG mengulang analisis proyek dari nol jika:

- `SYSTEM_MAP.md` tersedia dan masih relevan.
- `DEV_PROGRESS.md` tersedia dan masih relevan.
- Task aktif masih sama.

Agent baru harus:

1. Baca `SYSTEM_MAP.md`.
2. Baca `DEV_PROGRESS.md`.
3. Ringkas pemahaman status terakhir dalam 1-3 kalimat.
4. Langsung lanjut dari bagian `Next Exact Steps`.

Penelusuran ulang hanya boleh dilakukan jika:

- Ada indikasi `SYSTEM_MAP.md` atau `DEV_PROGRESS.md` tidak akurat.
- Kondisi kode telah berubah.
- Validasi membutuhkan pengecekan langsung.
- User memberikan task baru yang berbeda.

## Task Completion Rule

Jika task selesai:

- Ubah status menjadi `Completed`.
- Tulis validasi yang sudah dilakukan.
- Ringkas hasil final.
- Kosongkan atau perbarui `In Progress` dan `Next Exact Steps` bila tidak ada lanjutan.
- Jika user memberi task baru, update `DEV_PROGRESS.md` agar task lama tidak menyesatkan agent berikutnya.
