package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.entities.RoleNameEnum;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.entities.User;
import java.util.List;
import java.util.Objects;
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

    public List<UserDto> getUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(modelMapperConfig::userToDTO).toList();
    }

    /**
     * Realiza una baja logica del usuario que entra por parametro
     * (1: baja, 0: activo)
     * @param id
     */
    public void softDeleteUser(Long id, String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        boolean isAdmin = user.getRole().getName() == RoleNameEnum.ADMIN;
        boolean isSameUser = Objects.equals(user.getIdUser(), id);

        if (!isAdmin && !isSameUser) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No se puede eliminar el usuario porque no tiene permiso de administrador");
        }

        user.setSoftDelete(1);
        userRepository.save(user);
    }
}
