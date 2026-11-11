package myexpense;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

/** Implementasi UserRepository sepenuhnya menggunakan Hibernate ORM (tanpa SQL/JDBC manual). */
public class HibernateUserRepository implements UserRepository {

    @Override
    public User cariByEmail(String email) throws DbException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            UserEntity entity = session.createQuery(
                    "from UserEntity u where lower(u.email) = :email", UserEntity.class)
                    .setParameter("email", email.toLowerCase())
                    .uniqueResult();
            return entity == null ? null : toUser(entity);
        } catch (Exception e) {
            throw new DbException(pesanDatabase(e), e);
        }
    }

    @Override
    public boolean noHpSudahAda(String noHp) throws DbException {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long jumlah = session.createQuery(
                    "select count(u.id) from UserEntity u where u.noHp = :noHp", Long.class)
                    .setParameter("noHp", noHp)
                    .getSingleResult();
            return jumlah > 0;
        } catch (Exception e) {
            throw new DbException(pesanDatabase(e), e);
        }
    }

    @Override
    public void buat(String nama, String email, String noHp, String passwordHash) throws DbException {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(new UserEntity(nama, email.toLowerCase(), noHp, passwordHash));
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DbException(pesanDatabase(e), e);
        }
    }

    @Override
    public void ubahPassword(int userId, String passwordHash) throws DbException {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            UserEntity user = session.get(UserEntity.class, userId);
            if (user == null) throw new IllegalArgumentException("Pengguna tidak ditemukan.");
            user.setPasswordHash(passwordHash);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new DbException(pesanDatabase(e), e);
        }
    }

    private User toUser(UserEntity entity) {
        return new User(entity.getId(), entity.getNama(), entity.getEmail(), entity.getPasswordHash());
    }

    private String pesanDatabase(Exception e) {
        Throwable sebab = e;
        while (sebab.getCause() != null) sebab = sebab.getCause();
        String detail = sebab.getMessage() == null ? e.getMessage() : sebab.getMessage();
        if (detail != null && (detail.contains("Communications link failure")
                || detail.contains("Connection refused"))) {
            return "Tidak dapat terhubung ke MySQL. Pastikan MySQL XAMPP sudah Start dan database catatuang sudah dibuat.";
        }
        if (e instanceof ConstraintViolationException) {
            return "Email atau nomor handphone sudah digunakan.";
        }
        return "Kesalahan Hibernate/database: " + detail;
    }
}
