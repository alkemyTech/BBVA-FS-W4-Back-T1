package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.dto.UserLoginDTO;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.AuthenticationException;

public class SecurityService {
    private UserRepository userRepository;
    private ModelMapper modelMapper;

    public UserLoginDTO login (UserLoginDTO us) throws AuthenticationException {

        User user = userRepository.findByEmail(us.getEmail());
        if(user != null && us.getPassword().equals(user.getPassword())){
            us.setLastName(user.getFirstName());
            us.setFistName(user.getFirstName());
            us.setAutenticado(true);
        }
        else{
            us.setAutenticado(false);
        }
        return  us;
      /*  var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        var user = (TaskUser) authentication.getPrincipal();
        return jwtService.createToken(user.getUsername(), 60);*/
    }


}
