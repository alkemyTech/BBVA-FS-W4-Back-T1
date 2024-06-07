package com.magicdogs.alkywall.accounts;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.magicdogs.alkywall.controllers.AccountController;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.dto.AccountUpdateDTO;
import com.magicdogs.alkywall.servicies.AccountService;
import com.magicdogs.alkywall.servicies.JWTService;
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

@SpringBootTest
@AutoConfigureMockMvc
public class AccountUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private AccountService accountService;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private AccountController accountController;

    private AccountUpdateDTO accountUpdateDTO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        accountUpdateDTO = new AccountUpdateDTO(1000.0);
    }

    @DisplayName("Actualizar cuenta - Éxito")
    @WithMockUser(username = "user@example.com")
    @Test
    public void testUpdateAccountSuccess() throws Exception {
        AccountDTO accountDTO = new AccountDTO();
        when(jwtService.getJwtFromCookies(any())).thenReturn("jwt-token");
        when(jwtService.extractUserId(any(String.class))).thenReturn("user@example.com");
        when(accountService.updateAccount(anyLong(), anyString(), anyDouble())).thenReturn(accountDTO);

        mockMvc.perform(put("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountUpdateDTO)))
                .andExpect(status().isOk());
    }

    @DisplayName("Actualizar cuenta - Límite de transacción nulo")
    @Test
    public void testUpdateAccountTransactionLimitNull() throws Exception {
        accountUpdateDTO.setTransactionLimit(null);

        mockMvc.perform(put("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountUpdateDTO)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Actualizar cuenta - Límite de transacción negativo")
    @Test
    public void testUpdateAccountTransactionLimitNegative() throws Exception {
        accountUpdateDTO.setTransactionLimit(-500.0);

        mockMvc.perform(put("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountUpdateDTO)))
                .andExpect(status().isBadRequest());
    }
}