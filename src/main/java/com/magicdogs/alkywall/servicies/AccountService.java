package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.AccountCreateDTO;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ModelMapperConfig modelMapperConfig;

    public Optional<List<AccountDTO>> accountsByUser(Long userId){
        Optional<List<Account>> accountsOptional = accountRepository.findByUserIdUser(userId);
        return accountsOptional.map(accounts -> accounts.stream().map(modelMapperConfig::accountToDTO).toList());
    }

    public Account createAccount(String userEmail, CurrencyType currency) {

        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Account> existingAccount = accountRepository.findByUserAndCurrency(user, currency);
        if (existingAccount.isPresent()) {
            throw new IllegalArgumentException("Account already exists");
        }

        Account account = new Account();
        account.setBalance(0.0);
        account.setUser(user);
        account.setCbu(generateUniqueCbu());
        account.setSoftDelete(false);

        if (currency == CurrencyType.ARS) {
            account.setTransactionLimit(300000.0);
            account.setCurrency(CurrencyType.ARS);
        } else if (currency == CurrencyType.USD) {
            account.setTransactionLimit(1000.0);
            account.setCurrency(CurrencyType.USD);
        }

        return accountRepository.save(account);
    }

    public String generateUniqueCbu() {
        String cbu;
        do {
            cbu = generateCbu();
        } while (!isCbuUnique(cbu));
        return cbu;
    }

    private String generateCbu() {
        StringBuilder cbu = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 22; i++) {
            cbu.append(random.nextInt(10));
        }
        return cbu.toString();
    }

    private boolean isCbuUnique(String cbu) {
        return accountRepository.findByCbu(cbu).isEmpty();
    }
}
