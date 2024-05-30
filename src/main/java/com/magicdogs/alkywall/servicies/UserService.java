package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.UserDTO;
import com.magicdogs.alkywall.entities.User;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private final ModelMapperConfig modelMapperConfig;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public Page<UserDTO> getUsers(int pagina, int tamanio){
        Page<User> users = userRepository.findAll(PageRequest.of(pagina, tamanio));
        return users.map(modelMapperConfig::userToDTO);
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
