# MyExpense - Hibernate

Versi ini mempertahankan antarmuka Swing dan aturan login/registrasi/reset password dari project MyExpense asli, tetapi mengganti repository JDBC manual dengan Hibernate ORM.

## Kebutuhan
- JDK 21
- Apache NetBeans 21
- XAMPP / MariaDB berjalan pada port 3306
- Database `catatuang` (sudah ada di XAMPP pengguna)

## Menjalankan di NetBeans
1. Buka NetBeans.
2. Pilih File > Open Project.
3. Pilih folder yang berisi `pom.xml`.
4. Tunggu Maven mengunduh dependency.
5. Pastikan MySQL/MariaDB XAMPP aktif.
6. Klik kanan project > Run.

Hibernate memakai `hibernate.cfg.xml` dan `hibernate.hbm2ddl.auto=update`. Hibernate dapat membuat/memperbarui tabel entity yang dipetakan di database `catatuang`; tetap lakukan backup database sebelum pengujian.

Akun demo yang dibuat bila tabel users kosong:
- Email: admin@myexpense.com
- Password: admin123

## Catatan
- Koneksi database: `127.0.0.1:3306/catatuang`, user `root`, password kosong.
- Repository akun menggunakan Hibernate (`HibernateUserRepository`).
- `JdbcUserRepository.java` sudah tidak dipakai dan tidak disertakan.
