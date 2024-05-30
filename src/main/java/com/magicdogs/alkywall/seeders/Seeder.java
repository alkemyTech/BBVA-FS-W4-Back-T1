package com.magicdogs.alkywall.seeders;

import com.magicdogs.alkywall.Constants;
import com.magicdogs.alkywall.entities.*;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.servicies.AccountService;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import com.magicdogs.alkywall.repositories.RoleRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@AllArgsConstructor
public class Seeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker(Locale.forLanguageTag("es"));
    private final Random random = new Random();

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
                    createAccountsForUser(admin);
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
                    createAccountsForUser(user);
                }
            });

            System.out.println("Datos creados.");
        } else {
            System.out.println("Datos ya existen.");
        }
    }

    private void createAccountsForUser(User user) {
        boolean createArsAccount = random.nextBoolean();
        boolean createUsdAccount = random.nextBoolean();

        if (createArsAccount) {
            var accountARS = new Account(
                    CurrencyType.ARS,
                    Constants.getTransactionLimitArs(),
                    random.nextInt(100000),
                    user,
                    false,
                    accountService.generateUniqueCbu()
            );
            accountRepository.save(accountARS);
        }

        if (createUsdAccount) {
            var accountUSD = new Account(
                    CurrencyType.USD,
                    Constants.getTransactionLimitUsd(),
                    random.nextInt(500),
                    user,
                    false,
                    accountService.generateUniqueCbu()
            );
            accountRepository.save(accountUSD);
        }

        if (!createArsAccount && !createUsdAccount) {
            createAccountsForUser(user);
        }
    }
}
