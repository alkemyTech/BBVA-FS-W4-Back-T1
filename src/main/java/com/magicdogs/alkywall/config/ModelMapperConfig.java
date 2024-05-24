package com.magicdogs.alkywall.config;

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
    public ModelMapper modelMapp(){
        return new ModelMapper();
    }

    public UserDto userToDTO(User user){return modelMapp().map(user, UserDto.class);}

    public AccountDTO accountToDTO(Account account){return modelMapp().map(account, AccountDTO.class);}
}
