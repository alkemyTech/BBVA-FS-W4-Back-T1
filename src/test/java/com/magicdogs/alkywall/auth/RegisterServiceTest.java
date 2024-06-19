package com.magicdogs.alkywall.auth;


import com.magicdogs.alkywall.dto.UserRegisterDTO;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.Role;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.enums.DocumentType;
import com.magicdogs.alkywall.enums.RoleNameEnum;
import com.magicdogs.alkywall.enums.UserGender;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.RoleRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.SecurityService;
import com.magicdogs.alkywall.utils.AliasGenerator;
import com.magicdogs.alkywall.utils.CbuGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AliasGenerator aliasGenerator;

    @Mock
    private CbuGenerator cbuGenerator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setEmail("test@example.com");

        when(userRepository.findByEmail(registerDTO.getEmail())).thenReturn(Optional.of(new User()));

        ApiException exception = assertThrows(ApiException.class, () -> {
            securityService.registerUser(registerDTO);
        });

        assertEquals("Ya existe un usuario registrado con ese Email", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void testRegisterUser_RoleNotFound() {
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setEmail("test@example.com");

        lenient().when(userRepository.findByEmail(registerDTO.getEmail())).thenReturn(Optional.empty());
        lenient().when(roleRepository.findByName(RoleNameEnum.USER)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            securityService.registerUser(registerDTO);
        });

        assertEquals("No se ha encontrado el rol indicado", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(accountRepository, never()).save(any(Account.class));
    }


    // completo visto con alan
    @Test
    void testRegisterSuccess3() {
        // Arrange
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setFirstName("Test");
        userRegisterDTO.setLastName("User");
        userRegisterDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        userRegisterDTO.setGender(UserGender.MALE);
        userRegisterDTO.setDocumentType(DocumentType.DNI);
        userRegisterDTO.setDocumentNumber("12345678");
        userRegisterDTO.setEmail("testuser@example.com");
        userRegisterDTO.setPassword("testPassword");

        User user = new User();
        user.setEmail(userRegisterDTO.getEmail());

        Role role = new Role();

        role.setName(RoleNameEnum.USER);
        user.setRole(role);
        when(userRepository.findByEmail(userRegisterDTO.getEmail())).thenReturn(Optional.empty());
        Role expectedRole = new Role( 2L, RoleNameEnum.USER, "Usuario", null, null);
        // Can be simplified to just `new Role()`
        expectedRole.setName(RoleNameEnum.USER);
        when(roleRepository.findByName(any(RoleNameEnum.class))).thenReturn(Optional.of(expectedRole));
        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        securityService.register(userRegisterDTO, RoleNameEnum.USER);

        // Assert
        verify(roleRepository).findByName(RoleNameEnum.USER);
        verify(passwordEncoder).encode("testPassword");
        verify(userRepository).save(any(User.class));
    }

    // una prueba del error
    @Test
    void testMockRoleRepository() {
        Role expectedRole = new Role(2L, RoleNameEnum.USER, "Usuario", null, null);
        when(roleRepository.findByName(RoleNameEnum.USER)).thenReturn(Optional.of(expectedRole));

        Optional<Role> role = roleRepository.findByName(RoleNameEnum.USER);
        System.out.println("Mocked role: " + role);

        assertEquals(expectedRole, role.orElse(null));
    }
}
