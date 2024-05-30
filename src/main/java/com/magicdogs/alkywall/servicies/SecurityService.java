package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.Constants;
import com.magicdogs.alkywall.dto.UserDTO;
import com.magicdogs.alkywall.dto.UserLoginDTO;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import java.util.Optional;
import com.magicdogs.alkywall.dto.UserRegisterDTO;
import com.magicdogs.alkywall.entities.*;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@AllArgsConstructor
public class SecurityService {
    private final AccountService accountService;
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    public String login (UserLoginDTO us) throws AuthenticationException {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(us.getEmail(), us.getPassword())
        );

        var user2 = (User) authentication.getPrincipal();
        return jwtService.createToken(user2.getEmail(), 60);
    }
    public UserDTO searchUser(UserLoginDTO us){
        UserDTO userReturn;

        Optional<User> userEntity = userRepository.findByEmail(us.getEmail());
        if(userEntity != null){
            userReturn = modelMapper.map(userEntity.orElse(null), UserDTO.class);
            return userReturn;
        }
        return null;

    }

    public UserRegisterDTO registerUser(UserRegisterDTO registerRequest){
        return register(registerRequest, RoleNameEnum.USER);
    }

    public UserRegisterDTO registerAdmin(UserRegisterDTO registerRequest) {
        return register(registerRequest, RoleNameEnum.ADMIN);
    }

    private UserRegisterDTO register(UserRegisterDTO registerRequest, RoleNameEnum roleName) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        var newUser = new User(registerRequest.getFirstName(), registerRequest.getLastName(), registerRequest.getEmail(), passwordEncoder.encode(registerRequest.getPassword()), role, 0);
        var accountARS = new Account(CurrencyType.ARS, Constants.getTransactionLimitArs(), 0.00, newUser, false, accountService.generateUniqueCbu());
        var accountUSD = new Account(CurrencyType.USD, Constants.getTransactionLimitUsd(), 0.00, newUser, false, accountService.generateUniqueCbu());
        userRepository.save(newUser);
        accountRepository.save(accountARS);
        accountRepository.save(accountUSD);
        return registerRequest;
    }
}