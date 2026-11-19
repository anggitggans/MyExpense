package myexpense;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/** Membuat satu SessionFactory untuk dipakai seluruh repository Hibernate. */
public final class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() { }

    private static SessionFactory buildSessionFactory() {
        try {
            SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
            buatAkunAdminJikaBelumAda(factory);
            return factory;
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("Gagal menginisialisasi Hibernate: " + ex.getMessage());
        }
    }

    private static void buatAkunAdminJikaBelumAda(SessionFactory factory) {
        try (var session = factory.openSession()) {
            Long jumlah = session.createQuery("select count(u.id) from UserEntity u", Long.class)
                    .getSingleResult();
            if (jumlah == 0) {
                var tx = session.beginTransaction();
                session.persist(new UserEntity("Admin", "admin@myexpense.com",
                        "081200000000", PasswordUtil.hash("admin123")));
                tx.commit();
            }
        }
    }

    public static SessionFactory getSessionFactory() { return SESSION_FACTORY; }

    public static void shutdown() {
        if (SESSION_FACTORY != null && !SESSION_FACTORY.isClosed()) SESSION_FACTORY.close();
    }
}
