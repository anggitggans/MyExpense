package myexpense;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Hash password PBKDF2-SHA256. Formatnya sama dengan versi web (PHP):
 * pbkdf2$iterasi$salt(hex)$hash(hex), sehingga satu akun bisa dipakai di keduanya.
 */
public final class PasswordUtil {
    private static final int ITERASI = 120000;

    private PasswordUtil() {}

    public static String hash(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return "pbkdf2$" + ITERASI + "$" + hex(salt) + "$" + hex(derive(password, salt, ITERASI, 32));
    }

    public static boolean verify(String password, String tersimpan) {
        if (tersimpan == null || !tersimpan.startsWith("pbkdf2$")) return false; // bcrypt lama tidak didukung
        String[] p = tersimpan.split("\\$");
        if (p.length != 4) return false;
        try {
            byte[] calc = derive(password, unhex(p[2]), Integer.parseInt(p[1]), p[3].length() / 2);
            return MessageDigest.isEqual(calc, unhex(p[3]));
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static byte[] derive(String pw, byte[] salt, int iter, int bytes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(pw.toCharArray(), salt, iter, bytes * 8);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String hex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }

    private static byte[] unhex(String s) {
        byte[] r = new byte[s.length() / 2];
        for (int i = 0; i < r.length; i++) r[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        return r;
    }
}
