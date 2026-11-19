# CatatUang / MyExpense - Java Swing + Spring + Hibernate

Aplikasi desktop pengelolaan pemasukan dan pengeluaran pribadi. Seluruh antarmuka dan logika aplikasi dibuat dengan Java (Swing); Spring Framework digunakan untuk Dependency Injection dan Hibernate ORM untuk akses MariaDB.

## Fitur yang tersedia
1. Registrasi, login, lupa password, dan logout.
2. Dashboard berisi total pemasukan, pengeluaran, dan saldo dari database pengguna aktif.
3. Tambah transaksi pemasukan/pengeluaran dengan tanggal, kategori, nominal, dan keterangan.
4. Edit dan hapus transaksi (dengan konfirmasi).
5. Riwayat transaksi.
6. Pencarian berdasarkan keterangan/kategori.
7. Filter jenis, kategori, dan rentang tanggal. Format tanggal: `yyyy-MM-dd`.
8. Laporan pengeluaran per kategori dan ringkasan bulan berjalan.
9. Pengaturan profil dan ganti password.
10. Jendela Swing dapat diubah ukurannya dan menggunakan layout responsif.

## Teknologi
- Java 21, Swing
- Spring Framework Context 6.2.3
- Hibernate ORM 6.6.11.Final
- MariaDB Java Client 3.5.2
- Maven

## Persiapan database
1. Jalankan MySQL/MariaDB melalui XAMPP atau layanan MariaDB.
2. Jalankan `database_setup.sql` di phpMyAdmin atau client SQL.
3. Periksa `src/main/resources/hibernate.cfg.xml`. Default: `jdbc:mariadb://127.0.0.1:3306/catatuang`, user `root`, password kosong. Sesuaikan dengan konfigurasi lokal.
4. Hibernate membuat/memperbarui tabel entity secara otomatis saat aplikasi pertama kali dijalankan. Data transaksi terhubung ke `user_id`, sehingga transaksi antar akun terpisah.

## Menjalankan di NetBeans 21
1. Extract ZIP.
2. NetBeans: File > Open Project, pilih folder yang berisi `pom.xml`.
3. Tunggu Maven mengunduh dependency.
4. Pastikan database aktif dan konfigurasi koneksi benar.
5. Jalankan `myexpense.Myexpense` atau klik Run Project.

## Akun awal
Jika tabel users masih kosong, Hibernate membuat akun awal:
- Email: `admin@myexpense.com`
- Password: `admin123`
Segera ganti password setelah login.

## Catatan
- Semua operasi data transaksi menggunakan Hibernate ORM, bukan query JDBC manual.
- Filter tanggal menerima format ISO `yyyy-MM-dd`.
- Pengaturan anggaran bulanan pada diagram laporan bersifat opsional dan belum disertakan pada versi ini.
- Aplikasi ini merupakan aplikasi desktop Java Swing, bukan website HTML/PHP.
