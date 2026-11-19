package myexpense;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurasi Spring Framework untuk aplikasi MyExpense.
 * Spring mengelola objek repository, service, dan repository transaksi.
 * Hibernate tetap menjadi ORM yang mengakses database MariaDB.
 */
@Configuration
public class SpringConfig {

    @Bean
    public UserRepository userRepository() {
        return new HibernateUserRepository();
    }

    @Bean
    public AuthService authService(UserRepository userRepository) {
        return new AuthService(userRepository);
    }

    @Bean
    public TransaksiRepository transaksiRepository() {
        return new TransaksiRepository();
    }
}
