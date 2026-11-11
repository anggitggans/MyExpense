package myexpense;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/** Entity Hibernate yang dipetakan ke tabel users. */
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
    @UniqueConstraint(name = "uk_users_no_hp", columnNames = "no_hp")
})
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "no_hp", nullable = false, length = 20)
    private String noHp;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected UserEntity() { }

    public UserEntity(String nama, String email, String noHp, String passwordHash) {
        this.nama = nama;
        this.email = email;
        this.noHp = noHp;
        this.passwordHash = passwordHash;
    }

    public Integer getId() { return id; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public String getNoHp() { return noHp; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
