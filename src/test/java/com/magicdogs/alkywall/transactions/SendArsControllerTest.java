package com.magicdogs.alkywall.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magicdogs.alkywall.controllers.TransactionController;
import com.magicdogs.alkywall.dto.TransactionDTO;
import com.magicdogs.alkywall.dto.TransactionSendMoneyDTO;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.enums.TransactionConcept;
import com.magicdogs.alkywall.enums.TypeTransaction;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SendArsControllerTest {

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
        TransactionSendMoneyDTO transactionSendMoneyDTO = new TransactionSendMoneyDTO();
        transactionSendMoneyDTO.setOriginIdAccount(1L);
        transactionSendMoneyDTO.setDestinationIdAccount(2L);
        transactionSendMoneyDTO.setConcept(TransactionConcept.VARIOS);
        transactionSendMoneyDTO.setAmount(100.00);

        when(jwtService.extractUserId(any(String.class))).thenReturn("user@example.com");
        when(jwtService.getJwtFromCookies(any())).thenReturn("jwt-token");

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAccountIdAccount(transactionSendMoneyDTO.getOriginIdAccount());
        transactionDTO.setType(TypeTransaction.PAYMENT);
        transactionDTO.setConcept(transactionSendMoneyDTO.getConcept());
        transactionDTO.setAmount(transactionSendMoneyDTO.getAmount());
        when(transactionService.sendMoney(any(TransactionSendMoneyDTO.class), any(CurrencyType.class), any(String.class))).thenReturn(transactionDTO);

        mockMvc.perform(post("/transactions/sendArs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionSendMoneyDTO)))
                .andExpect(status().isOk());
    }

    @DisplayName("Validar TransactionDTO")
    @WithMockUser(username = "user@example.com")
    @Test
    public void testInvalidTransactionDTO() throws Exception {
        // Crear un TransactionDTO con campos nulos o valores no válidos
        TransactionSendMoneyDTO transactionSendMoneyDTO = new TransactionSendMoneyDTO();
        transactionSendMoneyDTO.setOriginIdAccount(null); // establecer un campo requerido como nulo

        // Simular la extracción del token y el usuario del token
        when(jwtService.getJwtFromCookies(any())).thenReturn("jwt-token");
        when(jwtService.extractUserId(any(String.class))).thenReturn("user@example.com");

        // Realizar la solicitud al controlador
        mockMvc.perform(post("/transactions/sendArs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionSendMoneyDTO)))
                // Verificar que la respuesta sea un BadRequest (código 400)
                .andExpect(status().isBadRequest());

        // Agregar más pruebas para los demás campos
        transactionSendMoneyDTO.setOriginIdAccount(1L); // establecer un valor válido
        transactionSendMoneyDTO.setDestinationIdAccount(null); // establecer un campo requerido como nulo
        mockMvc.perform(post("/transactions/sendArs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionSendMoneyDTO)))
                .andExpect(status().isBadRequest());

        transactionSendMoneyDTO.setDestinationIdAccount(2L); // establecer un valor válido
        transactionSendMoneyDTO.setAmount(-100.00); // establecer un valor negativo
        mockMvc.perform(post("/transactions/sendArs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionSendMoneyDTO)))
                .andExpect(status().isBadRequest());

        transactionSendMoneyDTO.setAmount(100.00); // establecer un valor positivo
        transactionSendMoneyDTO.setConcept(null); // establecer un campo requerido como nulo
        mockMvc.perform(post("/transactions/sendArs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionSendMoneyDTO)))
                .andExpect(status().isBadRequest());

    }
}
