package myexpense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

/** CRUD transaksi menggunakan Hibernate ORM. Tidak menggunakan query JDBC manual. */
public class TransaksiRepository {

    public void tambah(int userId, LocalDate tanggal, String jenis, String kategori,
            String keterangan, BigDecimal nominal) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            UserEntity pemilik = session.get(UserEntity.class, userId);
            if (pemilik == null) throw new IllegalArgumentException("Pengguna tidak ditemukan.");
            session.persist(new TransaksiEntity(pemilik, tanggal, jenis, kategori, keterangan, nominal));
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public List<TransaksiEntity> semuaByUser(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from TransaksiEntity t where t.user.id = :userId order by t.tanggal desc, t.id desc",
                    TransaksiEntity.class)
                    .setParameter("userId", userId)
                    .getResultList();
        }
    }

    public TransaksiEntity cariById(int transaksiId, int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from TransaksiEntity t where t.id = :id and t.user.id = :userId",
                    TransaksiEntity.class)
                    .setParameter("id", transaksiId)
                    .setParameter("userId", userId)
                    .uniqueResult();
        }
    }

    public void ubah(int transaksiId, int userId, LocalDate tanggal, String jenis,
            String kategori, String keterangan, BigDecimal nominal) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            TransaksiEntity data = session.createQuery(
                    "from TransaksiEntity t where t.id = :id and t.user.id = :userId",
                    TransaksiEntity.class)
                    .setParameter("id", transaksiId)
                    .setParameter("userId", userId)
                    .uniqueResult();
            if (data == null) throw new IllegalArgumentException("Transaksi tidak ditemukan.");
            data.setTanggal(tanggal);
            data.setJenis(jenis);
            data.setKategori(kategori);
            data.setKeterangan(keterangan);
            data.setNominal(nominal);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void hapus(int transaksiId, int userId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            TransaksiEntity data = session.createQuery(
                    "from TransaksiEntity t where t.id = :id and t.user.id = :userId",
                    TransaksiEntity.class)
                    .setParameter("id", transaksiId)
                    .setParameter("userId", userId)
                    .uniqueResult();
            if (data != null) session.remove(data);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw e;
        }
    }
}
