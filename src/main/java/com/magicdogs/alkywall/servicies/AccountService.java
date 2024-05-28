package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.AccountBalanceDTO;
import com.magicdogs.alkywall.dto.AccountCreateDTO;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.entities.*;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        Optional<List<Account>> accountsOptional = Optional.ofNullable(accountRepository.findByUserIdUser(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found")));
        return accountsOptional.map(accounts -> accounts.stream().map(modelMapperConfig::accountToDTO).toList());
    }

    public AccountCreateDTO createAccount(String userEmail, CurrencyType currency) {

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        Optional<Account> existingAccount = accountRepository.findByUserAndCurrency(user, currency);
        if (existingAccount.isPresent()) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "Account already exists");
        }

        var account = new Account(currency, 0.00, 0.00, user, false, generateUniqueCbu());

        if (currency == CurrencyType.ARS) {
            account.setTransactionLimit(300000.00);
        } else if (currency == CurrencyType.USD) {
            account.setTransactionLimit(1000.00);
        }

        Account savedAccount = accountRepository.save(account);

        return modelMapperConfig.accountCreateToDTO(savedAccount);
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

    /**
     * Retorna el balance de cada cuenta con
     * su historial de transacciones y plazos fijos.
     * Las cuentas se diferencian por su tipo de moneda
     * @param userEmail
     * @return DTO con el balance de las cuentas
     */
    public List<AccountBalanceDTO> getAccountBalance(String userEmail){
        Optional<List<Account>> accounts = accountRepository.findByUserEmail(userEmail);
        List<AccountBalanceDTO> accountsBalanceDTO = new ArrayList<>();

        if(accounts.isPresent()){
            for(Account ac: accounts.get()){
                AccountBalanceDTO accountBalanceDTO = new AccountBalanceDTO();
                if(ac.getCurrency().equals(CurrencyType.ARS)){
                    accountBalanceDTO.setAccountArs(ac.getBalance());
                    accountBalanceDTO.setHistory(ac.getTransactions().stream().map(modelMapperConfig::transactionBalanceToDTO).toList());
                    accountBalanceDTO.setFixedTerms(ac.getFixedTermDeposits().stream().map(modelMapperConfig::fixedTermsBalanceToDTO).toList());
                }else if(ac.getCurrency().equals(CurrencyType.USD)){
                    accountBalanceDTO.setAccountUsd(ac.getBalance());
                    accountBalanceDTO.setHistory(ac.getTransactions().stream().map(modelMapperConfig::transactionBalanceToDTO).toList());
                    accountBalanceDTO.setFixedTerms(ac.getFixedTermDeposits().stream().map(modelMapperConfig::fixedTermsBalanceToDTO).toList());
                }
                accountsBalanceDTO.add(accountBalanceDTO);
            }
        }
        return accountsBalanceDTO;
    }
}
