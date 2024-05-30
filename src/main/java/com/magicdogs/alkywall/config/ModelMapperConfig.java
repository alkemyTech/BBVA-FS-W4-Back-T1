package com.magicdogs.alkywall.config;

import com.magicdogs.alkywall.dto.*;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.FixedTermDeposit;
import com.magicdogs.alkywall.entities.Transaction;
import com.magicdogs.alkywall.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public UserDto userToDTO(User user){return modelMapper().map(user, UserDto.class);}

    public AccountDTO accountToDTO(Account account){return modelMapper().map(account, AccountDTO.class);}

    public TransactionBalanceDTO transactionBalanceToDTO(Transaction transaction){
        return modelMapper().map(transaction, TransactionBalanceDTO.class);
    }

    public FixedTermsBalanceDTO fixedTermsBalanceToDTO(FixedTermDeposit fixedTermDeposit){
        return modelMapper().map(fixedTermDeposit, FixedTermsBalanceDTO.class);
    }

    public ListTransactionDTO listTransactionDTO(Transaction transaction){return modelMapper().map(transaction, ListTransactionDTO.class);}
}