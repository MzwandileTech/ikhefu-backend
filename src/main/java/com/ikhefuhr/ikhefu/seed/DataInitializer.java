package com.ikhefuhr.ikhefu.seed;

import com.ikhefuhr.ikhefu.entity.User;
import com.ikhefuhr.ikhefu.enums.Role;
import com.ikhefuhr.ikhefu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-email}")
    private String adminEmail;

    @Value("${app.seed.admin-password}")
    private String adminPassword;

    @Override
    public void run(String... args) {


        if (userRepository.findByEmail(adminEmail).isEmpty()) {

            User admin = User.builder()
                    .firstName("Jabulani")
                    .lastName("Donda")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(admin);

            System.out.println("========================================");
            System.out.println("Default Administrator Created");
            System.out.println("Email: " + adminEmail);
            System.out.println("Password: "+ adminPassword);
            System.out.println("========================================");

        } else {

            System.out.println("Administrator already exists.");
        }
    }
}