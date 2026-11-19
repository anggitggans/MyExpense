package myexpense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
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

    public List<TransaksiEntity> cari(int userId, String kata, String jenis, String kategori,
            LocalDate dari, LocalDate sampai) {
        List<TransaksiEntity> semua = semuaByUser(userId);
        String q = kata == null ? "" : kata.trim().toLowerCase(Locale.ROOT);
        List<TransaksiEntity> hasil = new ArrayList<>();
        for (TransaksiEntity t : semua) {
            boolean cocokKata = q.isEmpty() || t.getKeterangan().toLowerCase(Locale.ROOT).contains(q)
                    || t.getKategori().toLowerCase(Locale.ROOT).contains(q);
            boolean cocokJenis = jenis == null || jenis.equals("Semua") || t.getJenis().equalsIgnoreCase(jenis);
            boolean cocokKategori = kategori == null || kategori.equals("Semua") || t.getKategori().equalsIgnoreCase(kategori);
            boolean cocokDari = dari == null || !t.getTanggal().isBefore(dari);
            boolean cocokSampai = sampai == null || !t.getTanggal().isAfter(sampai);
            if (cocokKata && cocokJenis && cocokKategori && cocokDari && cocokSampai) hasil.add(t);
        }
        return hasil;
    }

    public java.math.BigDecimal total(int userId, String jenis, LocalDate dari, LocalDate sampai) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "select coalesce(sum(t.nominal), 0) from TransaksiEntity t where t.user.id = :uid and t.jenis = :jenis";
            if (dari != null) hql += " and t.tanggal >= :dari";
            if (sampai != null) hql += " and t.tanggal <= :sampai";
            var query = session.createQuery(hql, java.math.BigDecimal.class)
                    .setParameter("uid", userId).setParameter("jenis", jenis);
            if (dari != null) query.setParameter("dari", dari);
            if (sampai != null) query.setParameter("sampai", sampai);
            return query.getSingleResult();
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
