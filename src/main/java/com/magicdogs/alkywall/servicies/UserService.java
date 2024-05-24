package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapperConfig modelMapperConfig;

    /**
     * Retorna una lista de usuarios desde la base de datos
     * y la mapea a una lista de usuarios DTO
     * @return Lista de usuarios
     */
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
