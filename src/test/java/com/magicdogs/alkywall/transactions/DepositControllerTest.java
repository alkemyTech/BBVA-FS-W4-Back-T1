package com.magicdogs.alkywall.transactions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magicdogs.alkywall.controllers.TransactionController;
import com.magicdogs.alkywall.dto.*;
import com.magicdogs.alkywall.enums.*;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
public class DepositControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private TransactionController transactionController;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionDepositDTO depositDTO;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        depositDTO = new TransactionDepositDTO();
        depositDTO.setAmount(100.0);
        depositDTO.setAccountType(AccountType.CAJA_AHORRO);
        depositDTO.setCurrency(CurrencyType.ARS);
        depositDTO.setConcept(TransactionConcept.VARIOS);
        depositDTO.setDescription("test deposit description");
    }

    @DisplayName("Depósito exitoso")
    @Test
    public void testDepositSuccess() throws Exception {
        ListTransactionDTO transactionDTO = new ListTransactionDTO();
        transactionDTO.setIdTransaction(1L);
        transactionDTO.setAmount(100.0);
        transactionDTO.setType(TypeTransaction.DEPOSIT);
        transactionDTO.setConcept(TransactionConcept.VARIOS);
        transactionDTO.setDescription("test deposit description");
        transactionDTO.setAccountIdAccount(1L);
        transactionDTO.setTransactionDate(LocalDateTime.now());

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setIdAccount(1L);
        accountDTO.setAccountType(AccountType.CAJA_AHORRO);
        accountDTO.setCurrency(CurrencyType.ARS);
        accountDTO.setBank(AccountBank.ALKYWALL);
        accountDTO.setCbu("1234567890123456789012");
        accountDTO.setAlias("my-savings-account");
        accountDTO.setTransactionLimit(5000.00);
        accountDTO.setBalance(100.00);

        TransactionAccountDTO transactionAccountDTO = new TransactionAccountDTO();
        transactionAccountDTO.setTransaction(transactionDTO);
        transactionAccountDTO.setAccount(accountDTO);

        when(jwtService.getJwtFromCookies(any())).thenReturn("jwt-token");
        when(jwtService.extractUserId(any(String.class))).thenReturn("user@example.com");
        when(transactionService.deposit(any(TransactionDepositDTO.class), any(String.class))).thenReturn(transactionAccountDTO);

        mockMvc.perform(post("/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transaction.amount").value(100.0))
                .andExpect(jsonPath("$.account.balance").value(100.0));
    }

    @DisplayName("Monto nulo")
    @Test
    public void testDepositAmountNull() throws Exception {
        TransactionDepositDTO depositDTO = new TransactionDepositDTO(null, AccountType.CAJA_AHORRO, CurrencyType.ARS, TransactionConcept.VARIOS, "test deposit description");

        mockMvc.perform(post("/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositDTO)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Monto negativo")
    @Test
    public void testDepositAmountNegative() throws Exception {
        TransactionDepositDTO depositDTO = new TransactionDepositDTO(-100.0, AccountType.CAJA_AHORRO, CurrencyType.ARS, TransactionConcept.VARIOS, "test deposit description");

        mockMvc.perform(post("/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositDTO)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Account type null")
    @Test
    public void testDepositAccountTypeNull() throws Exception {
        TransactionDepositDTO depositDTO = new TransactionDepositDTO(100.0, null, CurrencyType.ARS, TransactionConcept.VARIOS, "test deposit description");

        mockMvc.perform(post("/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositDTO)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Currency null")
    @Test
    public void testDepositCurrencyNull() throws Exception {
        TransactionDepositDTO depositDTO = new TransactionDepositDTO(100.0, AccountType.CAJA_AHORRO, null, TransactionConcept.VARIOS, "test deposit description");

        mockMvc.perform(post("/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositDTO)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Concept null")
    @Test
    public void testDepositConceptNull() throws Exception {
        TransactionDepositDTO depositDTO = new TransactionDepositDTO(100.0, AccountType.CAJA_AHORRO, CurrencyType.ARS, null, "test deposit description");

        mockMvc.perform(post("/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositDTO)))
                .andExpect(status().isBadRequest());
    }
}
