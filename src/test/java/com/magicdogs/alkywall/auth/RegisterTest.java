package com.magicdogs.alkywall.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magicdogs.alkywall.controllers.SecurityController;
import com.magicdogs.alkywall.dto.UserLoginDTO;
import com.magicdogs.alkywall.dto.UserRegisterDTO;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.Role;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.enums.DocumentType;
import com.magicdogs.alkywall.enums.RoleNameEnum;
import com.magicdogs.alkywall.enums.UserGender;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.RoleRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import com.magicdogs.alkywall.servicies.AccountService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private SecurityService securityService;

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
                "juan.perez13@example.com",       // email
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
        //invalidUser2 = new UserRegisterDTO("tom", "peresoso", "peresoso.com", "1234");
        MockitoAnnotations.openMocks(this);

    }
    // por los datos de prueba va a saltar error, pero funciona
    @Test
    void whenValidInput_thenReturns201() throws Exception {

        when(securityService.registerUser(any(UserRegisterDTO.class))).thenReturn(validUser);

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
                "",                             // firstName (inválido)
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
                .andExpect(jsonPath("$").value("El nombre no puede estar en blanco"));
        //"El nombre no puede estar vacío"
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
                .andExpect(jsonPath("$").value(
                        anyOf(
                                is("El nombre no puede ser nulo"),
                                is("El nombre no puede estar en blanco"),
                                is("El nombre no puede estar vacío")
                        )
                ));
        //"El nombre no puede ser nulo"
    }

    @Test
    void whenEmailAlreadyExists_thenReturns400() throws Exception {
        when(securityService.registerUser(any(UserRegisterDTO.class))).thenThrow(new IllegalArgumentException("User already exists"));
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
                .andExpect(jsonPath("$").value("El apellido no puede estar en blanco"));
        //"El apellido no puede estar en blanco"
    }
    @Test
    void invalidUserEmptyLastName () throws Exception {
        invalidUser =  new UserRegisterDTO(
                "juan",                         // firstName
                "",                        // lastName
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
                .andExpect(jsonPath("$").value("El apellido no puede estar en blanco"));
        //"El apellido no puede estar en blanco"
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
        //"El apellido no puede estar en blanco"
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
        //"El apellido no puede estar en blanco"
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
        //"El apellido no puede estar en blanco"
    }
}
