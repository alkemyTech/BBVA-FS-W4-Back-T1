package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.dto.UserUpdateDTO;
import com.magicdogs.alkywall.entities.RoleNameEnum;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private final ModelMapperConfig modelMapperConfig;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public Page<UserDto> getUsers(int pagina, int tamanio){
        Page<User> users = userRepository.findAll(PageRequest.of(pagina, tamanio));
        return users.map(modelMapperConfig::userToDTO);
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

    public UserDto update(Long id, String userEmail, UserUpdateDTO userUpdateDTO) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        boolean isSameUser = Objects.equals(user.getIdUser(), id);
        if (!isSameUser) {
            throw new ApiException(HttpStatus.CONFLICT, "El usuario loggeado no coincide con el id recibido");
        }

        if (userUpdateDTO.getFirstName() != null) {
            user.setFirstName(userUpdateDTO.getFirstName());
        }
        if (userUpdateDTO.getLastName() != null) {
            user.setLastName(userUpdateDTO.getLastName());
        }
        if (userUpdateDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        userRepository.save(user);
        return modelMapperConfig.userToDTO(user);

    }
}
