package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;


    public List<AccountDTO> accountsByUser(Long userId){
        return null;
    }
}
