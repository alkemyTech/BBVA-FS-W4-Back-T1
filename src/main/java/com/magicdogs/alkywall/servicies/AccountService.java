package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.entities.Account;
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
        Optional<List<Account>> accountsOptional = accountRepository.findByUserId(userId);
        return accountsOptional.map(accounts -> accounts.stream().map(modelMapperConfig::accountToDTO).toList());
    }
}
