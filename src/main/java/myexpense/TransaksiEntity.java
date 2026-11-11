package myexpense;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Entity Hibernate untuk tabel transaksi MyExpense. */
@Entity
@Table(name = "transaksi")
public class TransaksiEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_transaksi_user"))
    private UserEntity user;

    @Column(name = "tanggal", nullable = false)
    private LocalDate tanggal;

    @Column(name = "jenis", nullable = false, length = 12)
    private String jenis;

    @Column(name = "kategori", nullable = false, length = 50)
    private String kategori;

    @Column(name = "keterangan", length = 255)
    private String keterangan = "";

    @Column(name = "nominal", nullable = false, precision = 14, scale = 2)
    private BigDecimal nominal;

    protected TransaksiEntity() { }

    public TransaksiEntity(UserEntity user, LocalDate tanggal, String jenis,
            String kategori, String keterangan, BigDecimal nominal) {
        this.user = user;
        this.tanggal = tanggal;
        this.jenis = jenis;
        this.kategori = kategori;
        this.keterangan = keterangan;
        this.nominal = nominal;
    }

    public Integer getId() { return id; }
    public UserEntity getUser() { return user; }
    public LocalDate getTanggal() { return tanggal; }
    public String getJenis() { return jenis; }
    public String getKategori() { return kategori; }
    public String getKeterangan() { return keterangan; }
    public BigDecimal getNominal() { return nominal; }

    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
    public void setNominal(BigDecimal nominal) { this.nominal = nominal; }
}
