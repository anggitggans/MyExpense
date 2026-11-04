package myexpense;

import java.sql.*;

/**
 * Menyimpan akun di MySQL (database "catatuang", sama dengan versi web).
 * Database dan tabel dibuat otomatis jika belum ada.
 * Butuh library "MySQL JDBC Driver" pada proyek NetBeans.
 */
public class JdbcUserRepository implements UserRepository {
    private static final String HOST = "127.0.0.1", PORT = "3306", DB = "catatuang";
    private static final String USER = "root", PASS = "";

    private boolean siap = false;

    private Connection sambung() throws DbException {
        try {
            if (!siap) {
                try (Connection c = DriverManager.getConnection(
                        "jdbc:mysql://" + HOST + ":" + PORT + "/?useSSL=false&allowPublicKeyRetrieval=true", USER, PASS);
                     Statement s = c.createStatement()) {
                    s.execute("CREATE DATABASE IF NOT EXISTS `" + DB + "` CHARACTER SET utf8mb4");
                }
            }
            Connection c = DriverManager.getConnection(
                    "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB + "?useSSL=false&allowPublicKeyRetrieval=true", USER, PASS);
            if (!siap) { buatTabel(c); siap = true; }
            return c;
        } catch (SQLException e) {
            String pesan = "Tidak dapat terhubung ke database MySQL.\n"
                    + "Pastikan MySQL di XAMPP sudah di-Start dan library \"MySQL JDBC Driver\" sudah ditambahkan ke proyek.";
            if (e.getMessage() != null && e.getMessage().contains("No suitable driver"))
                pesan = "Library \"MySQL JDBC Driver\" belum ditambahkan ke proyek.\n"
                      + "Klik kanan Libraries > Add Library > MySQL JDBC Driver.";
            throw new DbException(pesan, e);
        }
    }

    private void buatTabel(Connection c) throws SQLException {
        try (Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, nama VARCHAR(100) NOT NULL,"
                    + "email VARCHAR(150) NOT NULL UNIQUE, no_hp VARCHAR(20) NOT NULL UNIQUE,"
                    + "password_hash VARCHAR(255) NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            s.execute("CREATE TABLE IF NOT EXISTS transaksi ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, user_id INT NOT NULL, tanggal DATE NOT NULL,"
                    + "jenis VARCHAR(12) NOT NULL, kategori VARCHAR(50) NOT NULL, keterangan VARCHAR(255) DEFAULT '',"
                    + "nominal DECIMAL(14,2) NOT NULL, FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");
        }
        try (Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT COUNT(*) FROM users")) {
            r.next();
            if (r.getInt(1) == 0) {
                try (PreparedStatement p = c.prepareStatement(
                        "INSERT INTO users (nama, email, no_hp, password_hash) VALUES (?,?,?,?)")) {
                    p.setString(1, "Admin");
                    p.setString(2, "admin@myexpense.com");
                    p.setString(3, "081200000000");
                    p.setString(4, PasswordUtil.hash("admin123"));
                    p.executeUpdate();
                }
            }
        }
    }

    @Override public User cariByEmail(String email) throws DbException {
        try (Connection c = sambung();
             PreparedStatement p = c.prepareStatement(
                     "SELECT id, nama, email, password_hash FROM users WHERE LOWER(email) = LOWER(?)")) {
            p.setString(1, email);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? new User(r.getInt(1), r.getString(2), r.getString(3), r.getString(4)) : null;
            }
        } catch (SQLException e) { throw new DbException("Kesalahan database: " + e.getMessage(), e); }
    }

    @Override public boolean noHpSudahAda(String noHp) throws DbException {
        try (Connection c = sambung();
             PreparedStatement p = c.prepareStatement("SELECT 1 FROM users WHERE no_hp = ?")) {
            p.setString(1, noHp);
            try (ResultSet r = p.executeQuery()) { return r.next(); }
        } catch (SQLException e) { throw new DbException("Kesalahan database: " + e.getMessage(), e); }
    }

    @Override public void buat(String nama, String email, String noHp, String hash) throws DbException {
        try (Connection c = sambung();
             PreparedStatement p = c.prepareStatement(
                     "INSERT INTO users (nama, email, no_hp, password_hash) VALUES (?,?,?,?)")) {
            p.setString(1, nama); p.setString(2, email.toLowerCase()); p.setString(3, noHp); p.setString(4, hash);
            p.executeUpdate();
        } catch (SQLException e) { throw new DbException("Kesalahan database: " + e.getMessage(), e); }
    }

    @Override public void ubahPassword(int userId, String hash) throws DbException {
        try (Connection c = sambung();
             PreparedStatement p = c.prepareStatement("UPDATE users SET password_hash = ? WHERE id = ?")) {
            p.setString(1, hash); p.setInt(2, userId);
            p.executeUpdate();
        } catch (SQLException e) { throw new DbException("Kesalahan database: " + e.getMessage(), e); }
    }
}
