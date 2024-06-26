package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.dto.UserPageDTO;
import com.magicdogs.alkywall.dto.UserUpdateDTO;
import com.magicdogs.alkywall.enums.RoleNameEnum;
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
import com.magicdogs.alkywall.dto.UserDTO;
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

    public UserPageDTO getUsers(Integer softDelete, Integer page, Integer size){
        Page<User> users = userRepository.findAllBySoftDelete(softDelete, PageRequest.of(page, size));
        var totalPages = users.getTotalPages();

        if (totalPages <= page) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "No existe el numero de pagina");
        }

        String next = "", prev = "";
        if (users.hasNext()){
            next = "/users?page="+(page+1);
        }

        if (users.hasPrevious()){
            prev = "/users?page="+(page-1);
        }

        var users_page = users.map(modelMapperConfig::userToDTO);
        return new UserPageDTO(users_page.getContent(), next, prev, totalPages);
    }

    /**
     * Realiza una baja logica del usuario que entra por parametro
     * (1: baja, 0: activo)
     * @param id
     */
    public void softDeleteUser(Long id, String userEmail) {
        var userLogged = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario autenticado no encontrado"));

        var userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario a eliminar no encontrado"));

        boolean isAdmin = userLogged.getRole().getName() == RoleNameEnum.ADMIN;
        boolean isSameUser = Objects.equals(userLogged, userToDelete);

        if (!isAdmin && !isSameUser) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No se puede eliminar el usuario porque no tiene permiso de administrador");
        }

        userToDelete.setSoftDelete(1);
        userRepository.save(userToDelete);
    }

    public UserDTO update(Long id, String userEmail, UserUpdateDTO userUpdateDTO) {
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

        if (userUpdateDTO.getBirthDate() != null) {
            user.setBirthDate(userUpdateDTO.getBirthDate());
        }

        if (userUpdateDTO.getGender() != null) {
            user.setGender(userUpdateDTO.getGender());
        }

        if (userUpdateDTO.getDocumentNumber() != null) {
            user.setDocumentNumber(userUpdateDTO.getDocumentNumber());
        }

        if (userUpdateDTO.getPassword() != null) {
            if (userUpdateDTO.getPassword().isBlank() || userUpdateDTO.getPassword().isEmpty()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "La contraseña no puede estar vacia");
            }
            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        userRepository.save(user);
        return modelMapperConfig.userToDTO(user);
    }

    public UserDTO userDetails(Long id, String email){
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if(!user.getIdUser().equals(id)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No coincide el id con el usuario autenticado");
        }

        return modelMapperConfig.userToDTO(user);
    }
}
