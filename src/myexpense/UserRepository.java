package myexpense;

/** Akses data akun. Implementasi asli: JdbcUserRepository (MySQL). */
public interface UserRepository {
    /** Dilempar bila database tidak bisa diakses (MySQL mati, driver belum ditambahkan, dsb). */
    class DbException extends Exception {
        public DbException(String pesan, Throwable sebab) { super(pesan, sebab); }
    }

    User cariByEmail(String email) throws DbException;          // null bila tidak ada
    boolean noHpSudahAda(String noHp) throws DbException;
    void buat(String nama, String email, String noHp, String passwordHash) throws DbException;
    void ubahPassword(int userId, String passwordHash) throws DbException;
}
