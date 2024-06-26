package com.magicdogs.alkywall.config;

import com.magicdogs.alkywall.dto.*;
import com.magicdogs.alkywall.entities.*;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(){return new ModelMapper();}

    public UserDTO userToDTO(User user){return modelMapper().map(user, UserDTO.class);}

    public AccountDTO accountToDTO(Account account){return modelMapper().map(account, AccountDTO.class);}

//    public TransactionBalanceDTO transactionBalanceToDTO(Transaction transaction){
//        return modelMapper().map(transaction, TransactionBalanceDTO.class);
//    }

    public FixedTermsBalanceDTO fixedTermsBalanceToDTO(FixedTermDeposit fixedTermDeposit){
        return modelMapper().map(fixedTermDeposit, FixedTermsBalanceDTO.class);
    }

    public FixedTermDeposit fixedTermsCreateDTOToEntitie(FixedTermCreateDTO fixedTermCreateDTO){
        return modelMapper().map(fixedTermCreateDTO, FixedTermDeposit.class);
    }

    public FixedTermSimulatedDTO fixedTermsToSimulatedDTO(FixedTermDeposit FixedTermDeposit){
        return modelMapper().map(FixedTermDeposit, FixedTermSimulatedDTO.class);
    }

    public TransactionDTO transactionToDTO(Transaction transaction){return modelMapper().map(transaction, TransactionDTO.class);}

    public ThirdAccount ThirdAccountDTOToEntity(ThirdAccountCreateDTO dto){
        return modelMapper().map(dto,ThirdAccount.class);
    }

    public ThirdAccountDTO ThirdAccountToDTO(ThirdAccount thirdAccount){
        return modelMapper().map(thirdAccount, ThirdAccountDTO.class);
    }

    public AccountThirdDTO accountThirdToDTO(Account account){return modelMapper().map(account, AccountThirdDTO.class);}
}