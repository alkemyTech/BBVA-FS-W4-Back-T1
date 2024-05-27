package com.magicdogs.alkywall.config;

import com.magicdogs.alkywall.dto.AccountCreateDTO;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.entities.Account;
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

    public AccountCreateDTO accountCreateToDTO(Account account){return modelMapper().map(account, AccountCreateDTO.class);}

}