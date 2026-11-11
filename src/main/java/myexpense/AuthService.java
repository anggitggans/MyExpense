package myexpense;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** Aturan login, pendaftaran, dan reset password (tanpa tampilan, agar mudah diuji). */
public class AuthService {
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern HP = Pattern.compile("^(\\+62|62|0)8[0-9]{7,12}$");

    /** Hasil pendaftaran: kosong errors + user != null berarti sukses. */
    public static class HasilDaftar {
        public final Map<String, String> errors = new LinkedHashMap<>();
        public String umum = "";
        public boolean sukses() { return errors.isEmpty() && umum.isEmpty(); }
    }

    private final UserRepository repo;

    public AuthService(UserRepository repo) { this.repo = repo; }

    public static String normalisasiHp(String hp) { return hp.replaceAll("[\\s\\-().]", ""); }

    /** Mengembalikan user bila email & password cocok, selain itu null. */
    public User login(String email, String password) throws UserRepository.DbException {
        if ("admin".equalsIgnoreCase(email.trim())) email = "admin@myexpense.com"; // kompatibel dengan akun lama
        User u = repo.cariByEmail(email.trim());
        return (u != null && PasswordUtil.verify(password, u.passwordHash)) ? u : null;
    }

    public HasilDaftar daftar(String nama, String email, String noHp, String password) throws UserRepository.DbException {
        HasilDaftar h = new HasilDaftar();
        nama = nama.trim(); email = email.trim(); noHp = normalisasiHp(noHp.trim());

        if (nama.isEmpty()) h.errors.put("nama", "Nama wajib diisi.");
        if (email.isEmpty()) h.errors.put("email", "Email wajib diisi.");
        else if (!EMAIL.matcher(email).matches()) h.errors.put("email", "Format email tidak valid.");
        if (noHp.isEmpty()) h.errors.put("noHp", "Nomor handphone wajib diisi.");
        else if (!HP.matcher(noHp).matches()) h.errors.put("noHp", "Nomor handphone tidak valid (contoh: 081234567890).");
        if (password.isEmpty()) h.errors.put("password", "Password wajib diisi.");
        else if (password.length() < 6) h.errors.put("password", "Password minimal 6 karakter.");
        if (!h.errors.isEmpty()) return h;

        boolean emailAda = repo.cariByEmail(email) != null;
        boolean hpAda = repo.noHpSudahAda(noHp);
        if (emailAda || hpAda) {
            h.umum = "Akun sudah terdaftar. Silakan login atau gunakan email/nomor handphone lain.";
            if (emailAda) h.errors.put("email", "Email ini sudah terdaftar.");
            if (hpAda) h.errors.put("noHp", "Nomor handphone ini sudah terdaftar.");
            return h;
        }
        try {
            repo.buat(nama, email, noHp, PasswordUtil.hash(password));
        } catch (UserRepository.DbException e) {
            // kemungkinan dua pendaftaran bersamaan (UNIQUE) -> cek ulang
            if (repo.cariByEmail(email) != null || repo.noHpSudahAda(noHp)) {
                h.umum = "Akun sudah terdaftar. Silakan login atau gunakan email/nomor handphone lain.";
                return h;
            }
            throw e;
        }
        return h;
    }

    /** Langkah 1 lupa password: kembalikan user jika email ada, selain itu null. */
    public User cekEmail(String email) throws UserRepository.DbException {
        return repo.cariByEmail(email.trim());
    }

    /** Langkah 2: mengembalikan pesan error, atau "" bila berhasil. */
    public String resetPassword(User u, String baru, String konfirmasi) throws UserRepository.DbException {
        if (baru.isEmpty()) return "Password baru wajib diisi.";
        if (baru.length() < 6) return "Password minimal 6 karakter.";
        if (konfirmasi.isEmpty()) return "Konfirmasi password wajib diisi.";
        if (!baru.equals(konfirmasi)) return "Konfirmasi password tidak sama.";
        repo.ubahPassword(u.id, PasswordUtil.hash(baru));
        return "";
    }
}
