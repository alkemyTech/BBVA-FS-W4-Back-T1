package com.magicdogs.alkywall.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.magicdogs.alkywall.entities.Role;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.enums.DocumentType;
import com.magicdogs.alkywall.enums.RoleNameEnum;
import com.magicdogs.alkywall.enums.UserGender;
import com.magicdogs.alkywall.repositories.UserRepository;
import com.magicdogs.alkywall.servicies.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserEntityTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role userRole;
    private Role adminRole;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("John", "Doe", LocalDate.of(1990, 1, 1), UserGender.MALE,
                DocumentType.DNI, "12345678", "john.doe@example.com", "password", userRole, 0);
        userRole = new Role(1L, RoleNameEnum.USER, "Regular User", LocalDateTime.now(), LocalDateTime.now());
        adminRole = new Role(1L, RoleNameEnum.ADMIN, "Admin", LocalDateTime.now(), LocalDateTime.now());
    }

    @DisplayName("Crear usuario con campos obligatorios y timestamps generados")
    @Test
    public void testCreateUserWithMandatoryFields() {

        user.onCreate();

        assertNotNull(user.getCreationDate());
        assertNotNull(user.getUpdateDate());
        assertEquals(user.getCreationDate(), user.getUpdateDate());

        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userRepository.save(user);

        assertEquals("John", savedUser.getFirstName());
        assertEquals("Doe", savedUser.getLastName());
        assertEquals("john.doe@example.com", savedUser.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @DisplayName("Validar unicidad del email")
    @Test
    public void testUniqueEmailValidation() {

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        User newUser = new User("Jane", "Doe", LocalDate.of(1992, 2, 2), UserGender.FEMALE,
                DocumentType.DNI, "87654321", "john.doe@example.com", "newpassword", userRole, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already exists");
            }
            userRepository.save(newUser);
        });

        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }


    @Test
    @DisplayName("Generación de timestamps al crear un usuario")
    public void testTimestampsOnRoleCreation() {

        user.onCreate();

        assertNotNull(user.getCreationDate());
        assertNotNull(user.getUpdateDate());
        assertEquals(user.getCreationDate(), user.getUpdateDate());
    }

    @Test
    @DisplayName("Generación de timestamps al actualizar un usuario")
    public void testTimestampsOnRoleUpdate() {
        user.onCreate();

        LocalDateTime creationDate = user.getCreationDate();
        LocalDateTime updateDate = user.getUpdateDate();

        // Simular el paso del tiempo
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        user.onUpdate();

        assertEquals(creationDate, user.getCreationDate());
        assertNotEquals(updateDate, user.getUpdateDate());
        assertTrue(user.getUpdateDate().isAfter(updateDate));
    }


    @DisplayName("Verificar borrado lógico del usuario")
    @Test
    public void testSoftDeleteUser() {

        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        user.setIdUser(1L);
        user.setRole(adminRole);

        userService.softDeleteUser(user.getIdUser(), "john.doe@example.com");

        verify(userRepository, times(1)).save(user);
    }
}
