package com.magicdogs.alkywall.seeders;

import com.magicdogs.alkywall.constants.Constants;
import com.magicdogs.alkywall.entities.*;
import com.magicdogs.alkywall.enums.*;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.TransactionRepository;
import com.magicdogs.alkywall.utils.AliasGenerator;
import com.magicdogs.alkywall.utils.CbuGenerator;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import com.magicdogs.alkywall.repositories.RoleRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@AllArgsConstructor
public class Seeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AliasGenerator aliasGenerator;
    private final CbuGenerator cbuGenerator;

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
                            LocalDate.of(1990, 9, 14),
                            UserGender.FEMALE,
                            DocumentType.DNI,
                            "36845984",
                            email,
                            passwordEncoder.encode(password),
                            adminRole,
                            0
                    );
                    userRepository.save(admin);
                    createAccountsAndTransactionsForUser(admin);
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
                            LocalDate.of(1990, 9, 14),
                            UserGender.MALE,
                            DocumentType.CUIT,
                            "20395556667",
                            email,
                            passwordEncoder.encode(password),
                            userRole,
                            0
                    );
                    userRepository.save(user);
                    createAccountsAndTransactionsForUser(user);
                }
            });
            System.out.println("Datos creados.");
        } else {
            System.out.println("Datos ya existen.");
        }
    }

    private void createAccountsAndTransactionsForUser(User user) {
        boolean createArsAccount = random.nextBoolean();
        boolean createUsdAccount = random.nextBoolean();

        if (createArsAccount) {
            var accountARS = new Account(
                    AccountType.CAJA_AHORRO,
                    CurrencyType.ARS,
                    AccountBank.ALKYWALL,
                    cbuGenerator.generateUniqueCbu(),
                    aliasGenerator.generateUniqueAlias(user.getFirstName(), user.getLastName()),
                    Constants.getTransactionLimitArs(),
                    random.nextDouble(100000),
                    user,
                    0
            );
            accountRepository.save(accountARS);
            createTransactionsForAccount(accountARS);
        }

        if (createUsdAccount) {
            var accountUSD = new Account(
                    AccountType.CAJA_AHORRO,
                    CurrencyType.USD,
                    AccountBank.ALKYWALL,
                    cbuGenerator.generateUniqueCbu(),
                    aliasGenerator.generateUniqueAlias(user.getFirstName(), user.getLastName()),
                    Constants.getTransactionLimitUsd(),
                    random.nextDouble(500),
                    user,
                    0
            );
            accountRepository.save(accountUSD);
            createTransactionsForAccount(accountUSD);
        }

        if (!createArsAccount && !createUsdAccount) {
            createAccountsAndTransactionsForUser(user);
        }
    }

    private void createTransactionsForAccount(Account account) {
        IntStream.range(0, 2).forEach(i -> {
            double percentage = 0.20 + (0.50 - 0.20) * random.nextDouble();
            BigDecimal amount = BigDecimal.valueOf(account.getBalance())
                    .multiply(BigDecimal.valueOf(percentage))
                    .setScale(2, RoundingMode.HALF_UP);
            boolean addDescription = random.nextBoolean();

            Transaction transaction = new Transaction(
                    amount.doubleValue(),
                    getRandomTransactionType(),
                    TransactionConcept.VARIOS,
                    addDescription ? faker.lorem().sentence() : "",
                    account
            );
            transactionRepository.save(transaction);
        });
    }

    private TypeTransaction getRandomTransactionType() {
        TypeTransaction[] types = TypeTransaction.values();
        return types[random.nextInt(types.length)];
    }
}
