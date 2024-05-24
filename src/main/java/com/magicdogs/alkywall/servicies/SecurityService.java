package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.dto.UserRegisterDTO;
import com.magicdogs.alkywall.entities.*;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.RoleRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;


@Component
@AllArgsConstructor
public class SecurityService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;


    public UserRegisterDTO registerUser(UserRegisterDTO registerRequest){
        return register(registerRequest, RoleNameEnum.USER);
    }

    public UserRegisterDTO registerAdmin(UserRegisterDTO registerRequest) {
        return register(registerRequest, RoleNameEnum.ADMIN);
    }


    private UserRegisterDTO register(UserRegisterDTO registerRequest, RoleNameEnum roleName) {
        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        //var newUser = modelMapper.map(registerRequest, User.class);
        var newUser = new User(null, registerRequest.firstName(), registerRequest.lastName(), registerRequest.email(), passwordEncoder.encode(registerRequest.password()), role, null, null, 0);
        var accountARS = new Account(null, CurrencyType.ARS, 300000.00, 0.00, newUser, null, null, false, "0");
        var accountUSD = new Account(null, CurrencyType.USD, 1000.00, 0.00, newUser, null, null, false, "0");
        userRepository.save(newUser);
        accountRepository.save(accountARS);
        accountRepository.save(accountUSD);
        return registerRequest;
    }
}
