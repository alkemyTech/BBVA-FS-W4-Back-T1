package com.magicdogs.alkywall.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magicdogs.alkywall.controllers.SecurityController;
import com.magicdogs.alkywall.enums.DocumentType;
import com.magicdogs.alkywall.enums.UserGender;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.dto.UserLoginDTO;
import com.magicdogs.alkywall.servicies.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SecurityService securityService;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private SecurityController securityController;

    @Autowired
    private ObjectMapper objectMapper;

    private UserLoginDTO userLoginDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(securityController).build();
        userLoginDTO = new UserLoginDTO("user@example.com", "password");
    }

    @DisplayName("Inicio de sesion exitoso")
    @Test
    public void testLoginSuccess() throws Exception {
        UserDto userReturn = new UserDto("nombre", "apellido", LocalDate.of(1992, 2, 2), UserGender.MALE, DocumentType.DNI, "35987654", "user@example.com");

        when(securityService.login(any(UserLoginDTO.class))).thenReturn("jwt-token");
        when(securityService.searchUser(any(UserLoginDTO.class))).thenReturn(userReturn);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(cookie().exists("jwt-token"))
                .andExpect(cookie().httpOnly("jwt-token", true))
                .andExpect(cookie().secure("jwt-token", true))
                .andExpect(cookie().path("jwt-token", "/"));
    }

    @DisplayName("Inicio de sesion fallido")
    @Test
    public void testLoginAuthenticationFailure() throws Exception {
        when(securityService.login(any(UserLoginDTO.class))).thenThrow(new AuthenticationException("Bad credentials") {});

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value("Usuario o contraseña invalido: Bad credentials"));
    }

    @DisplayName("Validación fallida - Email vacío")
    @Test
    public void testLoginEmailEmpty() throws Exception {
        UserLoginDTO invalidLoginDTO = new UserLoginDTO("", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginDTO)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Validación fallida - Email inválido")
    @Test
    public void testLoginEmailInvalid() throws Exception {
        UserLoginDTO invalidLoginDTO = new UserLoginDTO("invalid-email", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginDTO)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Validación fallida - Contraseña vacía")
    @Test
    public void testLoginPasswordEmpty() throws Exception {
        UserLoginDTO invalidLoginDTO = new UserLoginDTO("user@example.com", "");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginDTO)))
                .andExpect(status().isBadRequest());
    }
}