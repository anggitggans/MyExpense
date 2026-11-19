package myexpense;

/** Data akun pengguna. */
public class User {
    public final int id;
    public final String nama, email, passwordHash;

    public User(int id, String nama, String email, String passwordHash) {
        this.id = id; this.nama = nama; this.email = email; this.passwordHash = passwordHash;
    }
}
