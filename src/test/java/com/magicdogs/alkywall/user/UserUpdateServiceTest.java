package com.magicdogs.alkywall.user;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.UserDTO;
import com.magicdogs.alkywall.dto.UserUpdateDTO;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.enums.DocumentType;
import com.magicdogs.alkywall.enums.UserGender;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.UserRepository;
import com.magicdogs.alkywall.servicies.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserUpdateServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapperConfig modelMapperConfig;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserUpdateDTO userUpdateDTO;
    private UserDTO userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setIdUser(1L);
        user.setEmail("user@email.com");

        userDto = new UserDTO("nombre", "apellido", LocalDate.of(1992, 2, 2), UserGender.MALE, DocumentType.DNI, "35987654", "user@email.com");
        userUpdateDTO = new UserUpdateDTO("nombre", "apellido", "password");
    }

    @Test
    @DisplayName("Test update exitoso")
    void updateSuccess() {
        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(modelMapperConfig.userToDTO(user)).thenReturn(userDto);

        UserDTO result = userService.update(1L, "user@email.com", userUpdateDTO);

        assertEquals("nombre", result.getFirstName());
        assertEquals("apellido", result.getLastName());
        assertEquals("user@email.com", result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Test usuario no encontrado")
    void updateUserNotFound() {
        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            userService.update(1L, "user@email.com", userUpdateDTO);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

   @Test
   @DisplayName("Test id no coincide")
    void updateConflict() {
        user.setIdUser(2L);
        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));

        ApiException exception = assertThrows(ApiException.class, () -> {
            userService.update(1L, "user@email.com", userUpdateDTO);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("El usuario loggeado no coincide con el id recibido", exception.getMessage());
    }

    @Test
    @DisplayName("Test password vacia")
    void updateEmptyPassword() {
        userUpdateDTO.setPassword("");

        when(userRepository.findByEmail("user@email.com")).thenReturn(Optional.of(user));

        ApiException exception = assertThrows(ApiException.class, () -> {
            userService.update(1L, "user@email.com", userUpdateDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("La contraseña no puede estar vacia", exception.getMessage());
    }
}
