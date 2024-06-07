package com.magicdogs.alkywall.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magicdogs.alkywall.controllers.SecurityController;
import com.magicdogs.alkywall.dto.UserRegisterDTO;
//import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.entities.Role;
//import com.magicdogs.alkywall.entities.RoleNameEnum;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.RoleRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import com.magicdogs.alkywall.servicies.AccountService;
import com.magicdogs.alkywall.servicies.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterTest {/*
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegisterDTO validUser;
    private UserRegisterDTO invalidUser;
    private UserRegisterDTO invalidUser2;
    private  UserRegisterDTO usuarioVacio;

   // @InjectMocks
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
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private PasswordEncoder passwordEncoder;




    // TODO: validaciones DE REGISTRO
    @BeforeEach
    void setUp() {
        validUser = new UserRegisterDTO("jana","roz", "tirar4@tirar.com", "1234" );
        usuarioVacio = new UserRegisterDTO("tiras","tirar","tirar@tirar.com","1234");

        invalidUser = new UserRegisterDTO("vicky2","appleido123", "tirar3@tirar.com", "1234");
        //invalidUser2 = new UserRegisterDTO("tom", "peresoso", "peresoso.com", "1234");
        MockitoAnnotations.openMocks(this);

    }
    // por los datos de prueba va a saltar error, pero funciona
    @Test
    void whenValidInput_thenReturns201() throws Exception {

        //when(securityService.registerUser(any(UserRegisterDTO.class))).thenReturn(usuarioVacio);

        //when(securityService.login(any())).thenReturn("fake-jwt-token");

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
        invalidUser = new UserRegisterDTO("","apellido", "tulinda@mama.com", "1234");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("El nombre no puede estar en blanco"));
        //"El nombre no puede estar vacío"
    }
    @Test
    void nameNotNull() throws Exception {
        invalidUser = new UserRegisterDTO(null,"apellido", "tulinda@mama.com", "1234");
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("El nombre no puede estar vacío"));
        //"El nombre no puede ser nulo"
    }



    @Test
    void whenEmailAlreadyExists_thenReturns400() throws Exception {
        when(securityService.registerUser(any(UserRegisterDTO.class))).thenThrow(new IllegalArgumentException("User already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("User already exists"));
    }

    @Test
    void registerShouldCreateUserAndAccounts() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        // Arrange
        UserRegisterDTO registerRequest = new UserRegisterDTO("John", "Doe", "john@example.com", "password");
        Role role = new Role();
        role.setName(RoleNameEnum.USER);
        role.setDescription("usuario");
        when(roleRepository.findByName(any())).thenReturn(java.util.Optional.of(role));
        when(userRepository.findByEmail(any())).thenReturn(java.util.Optional.empty());
        when(accountService.generateUniqueCbu()).thenReturn("uniqueCbu");

        // Act
        securityService.registerUser(registerRequest);

        // Assert
        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(roleRepository, times(1)).findByName(RoleNameEnum.USER);
        verify(accountService, times(2)).generateUniqueCbu();
        verify(accountRepository, times(1)).save(argThat(account -> account.getCurrency() == CurrencyType.ARS && account.getUser().getEmail().equals("john@example.com")));
        verify(accountRepository, times(1)).save(argThat(account -> account.getCurrency() == CurrencyType.USD && account.getUser().getEmail().equals("john@example.com")));
        verify(userRepository, times(1)).save(any(User.class));
    }

    // TEST 29/05/2024
    @Test
    void contextLoads() throws Exception{
        assertThat(securityController).isNotNull();
    }*/
}
