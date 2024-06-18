package com.magicdogs.alkywall.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magicdogs.alkywall.controllers.SecurityController;
import com.magicdogs.alkywall.dto.UserDto;
import com.magicdogs.alkywall.dto.UserRegisterDTO;
import com.magicdogs.alkywall.enums.DocumentType;
import com.magicdogs.alkywall.enums.UserGender;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.RoleRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import com.magicdogs.alkywall.servicies.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SecurityService securityService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    @Autowired
    private SecurityController securityController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AccountRepository accountRepository;

    private UserRegisterDTO validUser;
    private UserRegisterDTO invalidUser;
    private  UserRegisterDTO usuarioVacio;

    @BeforeEach
    void setUp() {
        validUser = new UserRegisterDTO(
                "Juan",                         // firstName
                "Pérez",                        // lastName
                LocalDate.of(1990, 1, 1),       // birthDate
                UserGender.MALE,                // gender
                DocumentType.DNI,               // documentType
                "123456789",                    // documentNumber
                "juan.perez14@example.com",       // email
                "SecurePassword123"             // password
        );

        usuarioVacio = new UserRegisterDTO();

        invalidUser = new UserRegisterDTO(
                "Juan",                         // firstName
                "Pérez",                        // lastName
                LocalDate.of(1990, 1, 1),       // birthDate
                UserGender.MALE,                // gender
                DocumentType.CUIT,          // documentType
                "123456789",                    // documentNumber
                "juan.perez@example.com",       // email
                ""                              // password (inválido)
        );
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenValidInput_thenReturns201() throws Exception {

        var validUserDTO = new UserDto(
                "Juan",                         // firstName
                "Pérez",                        // lastName
                LocalDate.of(1990, 1, 1),       // birthDate
                UserGender.MALE,                // gender
                DocumentType.DNI,               // documentType
                "123456789",                    // documentNumber
                "juan.perez14@example.com"       // email
        );

        when(securityService.registerUser(any(UserRegisterDTO.class))).thenReturn(validUserDTO);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(validUser.getEmail()));
    }

    @Test
    void whenInvalidInput_thenReturns400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void nameEmpty() throws Exception {
        invalidUser = new UserRegisterDTO(
                " ",                             // firstName (inválido)
                "Pérez",                        // lastName
                LocalDate.of(1990, 1, 1),       // birthDate
                UserGender.MALE,                // gender
                DocumentType.CUIT,              // documentType
                "123456789",                    // documentNumber
                "juan.perez@example.com",       // email
                "SecurePassword123"             // password
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("El nombre no puede estar vacío"));
    }

    @Test
    void nameNotNull() throws Exception {
        invalidUser =  new UserRegisterDTO(
                null,                         // firstName
                "Pérez",                        // lastName
                LocalDate.of(1990, 1, 1),       // birthDate
                UserGender.MALE,                // gender
                DocumentType.CUIT,          // documentType
                "123456789",                    // documentNumber
                "juan.perez@example.com",       // email
                "SecurePassword123"             // password
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("El nombre no puede estar vacío"));
    }

    @Test
    void whenEmailAlreadyExists_thenReturns400() throws Exception {
        when(securityService.registerUser(any(UserRegisterDTO.class))).thenThrow(new IllegalArgumentException("Ya existe un usuario registrado con ese Email"));

        validUser =  new UserRegisterDTO(
                "jana",                             // firstName (inválido)
                "Pérez",                        // lastName
                LocalDate.of(1990, 1, 1),       // birthDate
                UserGender.MALE,                // gender
                DocumentType.CUIT,              // documentType
                "123456789",                    // documentNumber
                "user2@example.com",       // email
                "SecurePassword123"             // password
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Ya existe un usuario registrado con ese Email"));
    }

    @Test
    void invalidUserNullLastName () throws Exception {
        invalidUser =  new UserRegisterDTO(
                "juan",                         // firstName
                null,                        // lastName
                LocalDate.of(1990, 1, 1),       // birthDate
                UserGender.MALE,                // gender
                DocumentType.CUIT,          // documentType
                "123456789",                    // documentNumber
                "juan.perez@example.com",       // email
                "SecurePassword123"             // password
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("El apellido no puede estar vacío"));
    }

    @Test
    void invalidUserEmptyLastName () throws Exception {
        invalidUser =  new UserRegisterDTO(
                "juan",                         // firstName
                " ",                        // lastName
                LocalDate.of(1990, 1, 1),       // birthDate
                UserGender.MALE,                // gender
                DocumentType.CUIT,          // documentType
                "123456789",                    // documentNumber
                "juan.perez@example.com",       // email
                "SecurePassword123"             // password
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("El apellido no puede estar vacío"));
    }

    @Test
    void invalidUserNullBirthDate() throws Exception {
        invalidUser =  new UserRegisterDTO(
                "juan",                         // firstName
                "Perez",                        // lastName
                null,       // birthDate
                UserGender.MALE,                // gender
                DocumentType.CUIT,          // documentType
                "123456789",                    // documentNumber
                "juan.perez@example.com",       // email
                "SecurePassword123"             // password
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("La fecha de nacimiento no puede ser nula"));
    }

    @Test
    void invalidUserFutureBirthDate() throws Exception {
        invalidUser =  new UserRegisterDTO(
                "juan",                         // firstName
                "Perez",                        // lastName
                LocalDate.of(2024, 7, 1),       // birthDate
                UserGender.MALE,                // gender
                DocumentType.CUIT,          // documentType
                "123456789",                    // documentNumber
                "juan.perez@example.com",       // email
                "SecurePassword123"             // password
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("La fecha de nacimiento no puede ser en el futuro"));
    }

    @Test
    void invalidUserInvalidMail() throws Exception {
        invalidUser =  new UserRegisterDTO(
                "juan",                         // firstName
                "Perez",                        // lastName
                LocalDate.of(2024, 5, 1),       // birthDate
                UserGender.MALE,                // gender
                DocumentType.CUIT,          // documentType
                "123456789",                    // documentNumber
                "juan.perezexample.com",       // email
                "SecurePassword123"             // password
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("El correo electrónico debe ser válido"));
    }
}

