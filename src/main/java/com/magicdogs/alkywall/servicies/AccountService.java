package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.AccountBalanceDTO;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.entities.*;
import com.magicdogs.alkywall.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final ModelMapperConfig modelMapperConfig;

    public Optional<List<AccountDTO>> accountsByUser(Long userId){
        Optional<List<Account>> accountsOptional = accountRepository.findByUserIdUser(userId);
        return accountsOptional.map(accounts -> accounts.stream().map(modelMapperConfig::accountToDTO).toList());
    }

    public AccountDTO createAccount(User user, CurrencyType currency) {
        Optional<Account> existingAccount = accountRepository.findByUserAndCurrency(user, currency);
        if (existingAccount.isPresent()) {
            throw new IllegalStateException("La cuenta ya existe.");
        }

        Account account = new Account();
        account.setUser(user);
        account.setCurrency(currency);
        account.setBalance(0.0);

        if (currency == CurrencyType.ARS) {
            account.setTransactionLimit(300000.0);
        } else if (currency == CurrencyType.USD) {
            account.setTransactionLimit(1000.0);
        }

        Account savedAccount = accountRepository.save(account);
        return modelMapperConfig.accountToDTO(savedAccount);
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
