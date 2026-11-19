package myexpense;

import javax.swing.SwingUtilities;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Titik masuk aplikasi MyExpense.
 * Spring membuat dan mengelola dependency aplikasi (Dependency Injection).
 */
public class Myexpense {

    private static AnnotationConfigApplicationContext springContext;

    public static void main(String[] args) {
        springContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        AuthService authService = springContext.getBean(AuthService.class);

        SwingUtilities.invokeLater(() -> new Login(authService).setVisible(true));
    }

    /** Dipakai kembali saat pengguna keluar dari dashboard dan kembali ke login. */
    public static UserRepository getUserRepository() {
        if (springContext == null || !springContext.isActive()) throw new IllegalStateException("Spring belum dijalankan.");
        return springContext.getBean(UserRepository.class);
    }

    public static TransaksiRepository getTransaksiRepository() {
        if (springContext == null || !springContext.isActive()) throw new IllegalStateException("Spring belum dijalankan.");
        return springContext.getBean(TransaksiRepository.class);
    }

    public static AuthService getAuthService() {
        if (springContext == null || !springContext.isActive()) {
            throw new IllegalStateException("Spring belum dijalankan. Jalankan aplikasi melalui Myexpense.");
        }
        return springContext.getBean(AuthService.class);
    }

    public static void shutdownSpring() {
        if (springContext != null && springContext.isActive()) {
            springContext.close();
        }
        HibernateUtil.shutdown();
    }
}
