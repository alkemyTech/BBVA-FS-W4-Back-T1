package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.AccountBalanceDTO;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.entities.*;
import com.magicdogs.alkywall.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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

    public AccountBalanceDTO getAccountBalance(){
        List<Transaction> transactions = null;
        List<FixedTermDeposit> fixedTerms = null;
        Double incomes = 0.0;
        Double payments = 0.0;
        Double balance;
        for(Transaction t: transactions){
            if(t.getType().equals(TypeTransaction.INCOME)){incomes += t.getAmount();}
            else {payments += t.getAmount();}
        }
        balance = incomes - payments;

        return null;
    }
}
