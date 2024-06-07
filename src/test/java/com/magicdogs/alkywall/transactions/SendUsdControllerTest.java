package com.magicdogs.alkywall.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magicdogs.alkywall.controllers.TransactionController;
import com.magicdogs.alkywall.dto.ListTransactionDTO;
import com.magicdogs.alkywall.dto.TransactionDTO;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.enums.TransactionConcept;
import com.magicdogs.alkywall.enums.TypeTransaction;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SendUsdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    @Mock
    private JWTService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @DisplayName("Enviar dinero con datos válidos")
    @WithMockUser(username = "user@example.com")
    @Test
    public void testSendMoney() throws Exception {
        // Mock de los datos de entrada
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setOriginIdAccount(1L);
        transactionDTO.setDestinationIdAccount(2L);
        transactionDTO.setConcept(TransactionConcept.VARIOS);
        transactionDTO.setAmount(100.00);

        when(jwtService.extractUserId(any(String.class))).thenReturn("user@example.com");
        when(jwtService.getJwtFromCookies(any())).thenReturn("jwt-token");

        ListTransactionDTO listTransactionDTO = new ListTransactionDTO();
        listTransactionDTO.setAccountIdAccount(transactionDTO.getOriginIdAccount());
        listTransactionDTO.setType(TypeTransaction.PAYMENT);
        listTransactionDTO.setConcept(transactionDTO.getConcept());
        listTransactionDTO.setAmount(transactionDTO.getAmount());
        when(transactionService.sendMoney(any(TransactionDTO.class), any(CurrencyType.class), any(String.class))).thenReturn(listTransactionDTO);

        mockMvc.perform(post("/transactions/sendUsd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isOk());
    }

    @DisplayName("Validar TransactionDTO")
    @WithMockUser(username = "user@example.com")
    @Test
    public void testInvalidTransactionDTO() throws Exception {
        // Crear un TransactionDTO con campos nulos o valores no válidos
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setOriginIdAccount(null); // establecer un campo requerido como nulo

        // Simular la extracción del token y el usuario del token
        when(jwtService.getJwtFromCookies(any())).thenReturn("jwt-token");
        when(jwtService.extractUserId(any(String.class))).thenReturn("user@example.com");

        // Realizar la solicitud al controlador
        mockMvc.perform(post("/transactions/sendUsd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                // Verificar que la respuesta sea un BadRequest (código 400)
                .andExpect(status().isBadRequest());

        // Agregar más pruebas para los demás campos
        transactionDTO.setOriginIdAccount(1L); // establecer un valor válido
        transactionDTO.setDestinationIdAccount(null); // establecer un campo requerido como nulo
        mockMvc.perform(post("/transactions/sendUsd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isBadRequest());

        transactionDTO.setDestinationIdAccount(2L); // establecer un valor válido
        transactionDTO.setAmount(-100.00); // establecer un valor negativo
        mockMvc.perform(post("/transactions/sendUsd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isBadRequest());

        transactionDTO.setAmount(100.00); // establecer un valor positivo
        transactionDTO.setConcept(null); // establecer un campo requerido como nulo
        mockMvc.perform(post("/transactions/sendUsd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isBadRequest());

    }
}
