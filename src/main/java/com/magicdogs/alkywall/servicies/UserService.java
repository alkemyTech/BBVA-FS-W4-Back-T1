package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private final ModelMapperConfig modelMapperConfig;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public List<UserDto> getUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(modelMapperConfig::userToDTO).toList();
    }

    /**
     * Realiza una baja logica del usuario que entra por parametro
     * (1: baja, 0: activo)
     * @param id
     */
    public void softDeleteUser(Long id){
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(value -> value.setSoftDelete(1));
        userRepository.save(user.get());
    }
}
