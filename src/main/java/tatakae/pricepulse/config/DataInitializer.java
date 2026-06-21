package tatakae.pricepulse.config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tatakae.pricepulse.model.User;
import tatakae.pricepulse.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@pricepulse.com")) {
            User admin = new User(
                    "admin@pricepulse.com",
                    passwordEncoder.encode("admin123"),
                    "ROLE_ADMIN"
            );
            userRepository.save(admin);
            System.out.println("Admin user created: admin@pricepulse.com");
        }
    }
}
