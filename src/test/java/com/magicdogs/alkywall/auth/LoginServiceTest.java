package com.magicdogs.alkywall.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.enums.DocumentType;
import com.magicdogs.alkywall.enums.UserGender;
import com.magicdogs.alkywall.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.dto.UserLoginDTO;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.SecurityService;
import org.springframework.security.core.AuthenticationException;

import java.time.LocalDate;
import java.util.Optional;

public class LoginServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityService securityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Inicio de sesión exitoso")
    @Test
    public void testLoginSuccess() {
        UserLoginDTO userLoginDTO = new UserLoginDTO("user@example.com", "password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        User user2 = new User();
        user2.setEmail("user@example.com");
        when(authentication.getPrincipal()).thenReturn(user2);

        when(jwtService.createToken("user@example.com", 60)).thenReturn("fake-jwt-token");

        String result = securityService.login(userLoginDTO);

        assertEquals("fake-jwt-token", result);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).createToken("user@example.com", 60);
    }

    @DisplayName("Autenticación fallida")
    @Test
    public void testLoginAuthenticationFailure() {
        UserLoginDTO userLoginDTO = new UserLoginDTO("user@example.com", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Bad credentials") {});

        assertThrows(AuthenticationException.class, () -> securityService.login(userLoginDTO));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @DisplayName("Fallo en JWTService")
    @Test
    public void testJwtServiceFailure() {
        UserLoginDTO userLoginDTO = new UserLoginDTO("user@example.com", "password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        User user = new User();
        user.setEmail("user@example.com");
        when(authentication.getPrincipal()).thenReturn(user);

        when(jwtService.createToken("user@example.com", 60)).thenThrow(new RuntimeException("JWT creation failed"));

        assertThrows(RuntimeException.class, () -> securityService.login(userLoginDTO));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).createToken("user@example.com", 60);
    }

    @DisplayName("Búsqueda de usuario existente")
    @Test
    public void testSearchUserExistingUser() {
        // Arrange
        UserLoginDTO userLoginDTO = new UserLoginDTO("user@example.com", "password");

        User userEntity = new User();
        userEntity.setEmail("user@example.com");
        userEntity.setFirstName("User");
        userEntity.setLastName("Example");
        userEntity.setBirthDate(LocalDate.of(1990, 1, 1));
        userEntity.setGender(UserGender.MALE);
        userEntity.setDocumentType(DocumentType.DNI);
        userEntity.setDocumentNumber("33456789");
        userEntity.setSoftDelete(0);

        UserDto userDTO = new UserDto();
        userDTO.setEmail("user@example.com");
        userDTO.setFirstName("User");
        userDTO.setLastName("Example");
        userDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        userDTO.setGender(UserGender.MALE);
        userDTO.setDocumentType(DocumentType.DNI);
        userDTO.setDocumentNumber("33456789");

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(userEntity));
        when(modelMapper.map(userEntity, UserDto.class)).thenReturn(userDTO);

        // Act
        UserDto userReturn = securityService.searchUser(userLoginDTO);

        // Assert
        assertNotNull(userReturn);
        assertEquals("user@example.com", userReturn.getEmail());
        assertEquals("User", userReturn.getFirstName());
        assertEquals("Example", userReturn.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), userReturn.getBirthDate());
        assertEquals(UserGender.MALE, userReturn.getGender());
        assertEquals(DocumentType.DNI, userReturn.getDocumentType());
        assertEquals("33456789", userReturn.getDocumentNumber());

        verify(userRepository).findByEmail("user@example.com");
    }

    @DisplayName("Búsqueda de usuario inexistente")
    @Test
    public void testSearchUserNonExistingUser() {

        UserLoginDTO userLoginDTO = new UserLoginDTO("nonexistent@example.com", "password");

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        UserDto userDTO = securityService.searchUser(userLoginDTO);

        assertNull(userDTO);

        verify(userRepository).findByEmail("nonexistent@example.com");
    }
}