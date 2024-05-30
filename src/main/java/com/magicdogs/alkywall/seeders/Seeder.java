package com.magicdogs.alkywall.seeders;

import com.magicdogs.alkywall.entities.RoleNameEnum;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import com.magicdogs.alkywall.entities.Role;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.repositories.RoleRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.stream.IntStream;

@Component
@AllArgsConstructor
public class Seeder implements CommandLineRunner {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker(Locale.forLanguageTag("es"));

    @Override
    public void run(String... args) {

        String emailSearchAdmin = "admin0@example.com";
        if (userRepository.findByEmail(emailSearchAdmin).isEmpty()) {

            Role adminRole = roleRepository.findByName(RoleNameEnum.ADMIN)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));
            Role userRole = roleRepository.findByName(RoleNameEnum.USER)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));

            // Crear 10 usuarios administradores si no existen
            IntStream.range(0, 10).forEach(i -> {
                String email = "admin" + i + "@example.com";
                if (userRepository.findByEmail(email).isEmpty()) {
                    String password = "admin" + i;
                    User admin = new User(
                            faker.name().firstName(),
                            faker.name().lastName(),
                            email,
                            passwordEncoder.encode(password),
                            adminRole,
                            0
                    );
                    userRepository.save(admin);
                }
            });

            // Crear 10 usuarios regulares si no existen
            IntStream.range(0, 10).forEach(i -> {
                String email = "user" + i + "@example.com";
                if (userRepository.findByEmail(email).isEmpty()) {
                    String password = "user" + i;
                    User user = new User(
                            faker.name().firstName(),
                            faker.name().lastName(),
                            email,
                            passwordEncoder.encode(password),
                            userRole,
                            0
                    );
                    userRepository.save(user);
                }
            });
            System.out.println("Datos de usuario creados.");
        } else  {
            System.out.println("Datos de usuario ya existen.");
        }
    }
}