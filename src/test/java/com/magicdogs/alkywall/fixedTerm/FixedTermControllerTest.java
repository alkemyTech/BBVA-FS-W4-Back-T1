package com.magicdogs.alkywall.fixedTerm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.magicdogs.alkywall.constants.Constants;
import com.magicdogs.alkywall.controllers.FixedTermController;
import com.magicdogs.alkywall.dto.FixedTermCreateDTO;
import com.magicdogs.alkywall.dto.FixedTermSimulatedDTO;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.FixedTermDeposit;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.servicies.FixedTermService;
import com.magicdogs.alkywall.servicies.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FixedTermControllerTest {
    @Mock
    private FixedTermService fixedTermService;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private FixedTermController fixedTermController;

    @Mock
    private BindingResult bindingResult;

    private FixedTermCreateDTO validFixedTermCreateDTO;
    private Account validAccount;
    private FixedTermDeposit validFixedTermDeposit;
    private FixedTermSimulatedDTO validFixedTermSimulatedDTO;
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    @WithMockUser(username = "user@example.com")
    void setUp() {
        validFixedTermCreateDTO = new FixedTermCreateDTO();
        validFixedTermCreateDTO.setAmount(1000.0);
        validFixedTermCreateDTO.setClosingDate(LocalDate.now().plusDays(Constants.getFixedTermMinimunDays() + 1));

        validAccount = new Account();
        validAccount.setCurrency(CurrencyType.ARS);
        validAccount.setBalance(2000.0);

        validFixedTermDeposit = new FixedTermDeposit();
        validFixedTermDeposit.setAccount(validAccount);
        validFixedTermDeposit.setAmount(1000.0);
        validFixedTermDeposit.setCreationDate(LocalDateTime.now()); // Inicializa creationDate
        validFixedTermDeposit.setClosingDate(LocalDateTime.now().plusDays(Constants.getFixedTermMinimunDays() + 1));
        validFixedTermDeposit.setInterest(Constants.getFixedTermInterestPercent());

        validFixedTermSimulatedDTO = new FixedTermSimulatedDTO();
        validFixedTermSimulatedDTO.setIdDeposit(1L);
        validFixedTermSimulatedDTO.setAmount(1000.0);
        validFixedTermSimulatedDTO.setInterest(Constants.getFixedTermInterestPercent());
        validFixedTermSimulatedDTO.setCreationDate(validFixedTermDeposit.getCreationDate());
        validFixedTermSimulatedDTO.setClosingDate(validFixedTermDeposit.getClosingDate());
        validFixedTermSimulatedDTO.setInterestTotal(100.0);  // Suponiendo que este es el valor esperado
        validFixedTermSimulatedDTO.setInterestTodayWin(1.0); // Suponiendo que este es el valor esperado
        validFixedTermSimulatedDTO.setAmountTotalToReceive(1100.0); // Suponiendo que este es el valor esperado

        //MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(fixedTermController).build();
        lenient().when(jwtService.getJwtFromCookies(any())).thenReturn(null); // Simular token inválido

    }


    @Test
        void testCreateFixedTerm() {
            // Arrange
            FixedTermCreateDTO fixedTermCreateDTO = new FixedTermCreateDTO();
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(jwtService.getJwtFromCookies(request)).thenReturn("dummyToken");
            when(jwtService.extractUserId("dummyToken")).thenReturn("user@example.com");
            when(fixedTermService.createFixedTermDeposit(fixedTermCreateDTO, "user@example.com"))
                    .thenReturn(validFixedTermSimulatedDTO); // Mock respuesta del servicio


            // Act
            ResponseEntity<?> response = fixedTermController.createFixedTerm(fixedTermCreateDTO, request);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            // Puedes agregar más aserciones dependiendo de tu lógica de negocio y cómo quieres probarla.
        }
    @Test
    void testCreateFixedTermWithValidToken() {
        // Arrange
        FixedTermCreateDTO fixedTermCreateDTO = new FixedTermCreateDTO();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtService.getJwtFromCookies(request)).thenReturn("dummyToken");
        when(jwtService.extractUserId("dummyToken")).thenReturn("user@example.com");
        when(fixedTermService.createFixedTermDeposit(fixedTermCreateDTO, "user@example.com"))
                .thenReturn(validFixedTermSimulatedDTO); // Mock respuesta del servicio

        // Act
        ResponseEntity<?> response = fixedTermController.createFixedTerm(fixedTermCreateDTO, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateFixedTermWithInvalidToken5() {
        // Arrange
        FixedTermCreateDTO fixedTermCreateDTO = new FixedTermCreateDTO();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtService.getJwtFromCookies(request)).thenReturn(null); // Simular token inválido

        // Act
        ResponseEntity<?> response = fixedTermController.createFixedTerm(fixedTermCreateDTO, request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


    // NUEVOS
    @Test
    void testCreateFixedTermWithNullAmount() throws Exception {
        FixedTermCreateDTO fixedTermCreateDTO = new FixedTermCreateDTO(null, LocalDate.now().plusDays(10));


        mockMvc.perform(post("/fixedTerm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedTermCreateDTO)))
                    .andExpect(status().isBadRequest()).andDo(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    System.out.println("Response JSON: " + contentAsString);
                });
              //  .andExpect(jsonPath("$").value("El monto no puede ser nulo"));

    }

    @Test
    void testCreateFixedTermWithNegativeAmount() throws Exception {
        FixedTermCreateDTO fixedTermCreateDTO = new FixedTermCreateDTO(-100.0, LocalDate.now().plusDays(10));

        mockMvc.perform(post("/fixedTerm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedTermCreateDTO)))
                .andExpect(status().isBadRequest())
                .andDo(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    System.out.println("Response JSON: " + contentAsString);
                    System.out.println("solo result y response: "+result.getResponse());
                });
                //.andExpect(jsonPath("$").value("El monto debe ser mayor a cero"));
    }

    @Test
    void testCreateFixedTermWithNullDate() throws Exception {
        FixedTermCreateDTO fixedTermCreateDTO = new FixedTermCreateDTO(100.0, null);

        mockMvc.perform(post("/fixedTerm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedTermCreateDTO)))
                .andExpect(status().isBadRequest());
                //.andExpect(jsonPath("$").string("La fecha no puede ser nula"));

    }

    @Test
    void testCreateFixedTermWithPastDate() throws Exception {
        FixedTermCreateDTO fixedTermCreateDTO = new FixedTermCreateDTO(100.0, LocalDate.now().minusDays(10));
        //HttpServletRequest request = mock(HttpServletRequest.class);
        //when(jwtService.getJwtFromCookies(request)).thenReturn(null); // Simular token inválido
        mockMvc.perform(post("/fixedTerm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedTermCreateDTO)))
                .andExpect(status().isBadRequest());
                //.andExpect(jsonPath("$").value("La fecha debe ser en el futuro"));
    }

    @Test
    void testCreateFixedTermWithInvalidToken() throws Exception {
        FixedTermCreateDTO fixedTermCreateDTO = new FixedTermCreateDTO(100.0, LocalDate.now().plusDays(10));
        when(jwtService.getJwtFromCookies(any())).thenReturn(null); // Simular token inválido
         mockMvc.perform(post("/fixedTerm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fixedTermCreateDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value("Unauthorized: No token provided"));
    }
}
