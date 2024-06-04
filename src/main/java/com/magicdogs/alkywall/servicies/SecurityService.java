package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.constants.Constants;
import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.dto.UserLoginDTO;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.enums.AccountType;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.enums.RoleNameEnum;
import com.magicdogs.alkywall.repositories.UserRepository;
import com.magicdogs.alkywall.utils.AliasGenerator;
import com.magicdogs.alkywall.utils.CbuGenerator;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import java.util.Optional;
import com.magicdogs.alkywall.dto.UserRegisterDTO;
import com.magicdogs.alkywall.entities.*;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SecurityService {
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final AliasGenerator aliasGenerator;
    private final CbuGenerator cbuGenerator;

    private final PasswordEncoder passwordEncoder;

    public String login (UserLoginDTO us) throws AuthenticationException {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(us.getEmail(), us.getPassword())
        );

        var user2 = (User) authentication.getPrincipal();
        return jwtService.createToken(user2.getEmail(), 60);
    }

    public UserDto searchUser(UserLoginDTO us){
        UserDto userReturn;

        Optional<User> userEntity = userRepository.findByEmail(us.getEmail());
        if(userEntity != null){
            userReturn = modelMapper.map(userEntity.orElse(null), UserDto.class);
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

    public UserRegisterDTO register(UserRegisterDTO registerRequest, RoleNameEnum roleName) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        var newUser = new User(registerRequest.getFirstName(), registerRequest.getLastName(), registerRequest.getEmail(), passwordEncoder.encode(registerRequest.getPassword()), role, 0);
        userRepository.save(newUser);

        var accountARS = new Account(AccountType.CAJA_AHORRO, CurrencyType.ARS, cbuGenerator.generateUniqueCbu(), aliasGenerator.generateUniqueAlias(newUser.getFirstName(), newUser.getLastName()), Constants.getTransactionLimitArs(), 0.0, newUser, 0);
        accountRepository.save(accountARS);

        var accountUSD = new Account(AccountType.CAJA_AHORRO, CurrencyType.USD, cbuGenerator.generateUniqueCbu(), aliasGenerator.generateUniqueAlias(newUser.getFirstName(), newUser.getLastName()), Constants.getTransactionLimitUsd(), 0.0, newUser, 0);
        accountRepository.save(accountUSD);

        return registerRequest;
    }

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
}