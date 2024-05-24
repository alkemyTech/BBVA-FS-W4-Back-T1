package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.dto.UserLoginDTO;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SecurityService {
    private UserRepository userRepository;
    //private ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    //private final PasswordEncoder passwordEncoder;

    public String login (UserLoginDTO us) throws AuthenticationException {

        /*User user = userRepository.findByEmail(us.getEmail());
        if(user != null && us.getPassword().equals(user.getPassword())){
            us.setLastName(user.getFirstName());
            us.setFistName(user.getFirstName());
            us.setAutenticado(true);
        }
        else{
            us.setAutenticado(false);
        }*/
       // return  us;
        // ACA LO S BUCA A ALA BSE DE DATOS.
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(us.getEmail(), us.getPassword())
        );

        var user2 = (User) authentication.getPrincipal();
        return jwtService.createToken(user2.getEmail(), 60);
    }


}
