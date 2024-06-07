package com.magicdogs.alkywall.accounts;

import com.magicdogs.alkywall.controllers.AccountController;
import com.magicdogs.alkywall.dto.AccountBalanceDTO;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.dto.FixedTermsBalanceDTO;
import com.magicdogs.alkywall.dto.TransactionBalanceDTO;
import com.magicdogs.alkywall.servicies.AccountService;
import com.magicdogs.alkywall.servicies.JWTService;
import com.magicdogs.alkywall.servicies.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
public class AccountBalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTService jwtService;

    @MockBean
    private AccountService accountService;

    @MockBean
    private UserService userService;

    private String token;
    private String email;

    @BeforeEach
    void setUp() {
        token = "mockedToken";
        email = "mockedUser@example.com";

        AccountDTO accountUsd = new AccountDTO();
        List<AccountDTO> accountArs = new ArrayList<>();
        List<TransactionBalanceDTO> history = new ArrayList<>();
        List<FixedTermsBalanceDTO> fixedTerms = new ArrayList<>();

        AccountBalanceDTO accountBalanceDTO = new AccountBalanceDTO(accountArs, accountUsd, history, fixedTerms);

        when(jwtService.getJwtFromCookies(any(HttpServletRequest.class))).thenReturn(token);
        when(jwtService.extractUserId(token)).thenReturn(email);
        when(accountService.getAccountBalance(email)).thenReturn(accountBalanceDTO);
    }

    @Test
    @DisplayName("Test obtener balance exitoso")
    @WithMockUser(username = "mockedUser@example.com", roles = {"USER"})
    void testAccountsBalance() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/balance")
                        .with(SecurityMockMvcRequestPostProcessors.user("mockedUser@example.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountArs").isArray())
                .andExpect(jsonPath("$.accountUsd").exists())
                .andExpect(jsonPath("$.history").isArray())
                .andExpect(jsonPath("$.fixedTerms").isArray());
    }

    @Test
    @DisplayName("Test para usuario no autorizado")
    void testAccountsBalanceUnauthorized() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/balance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Test para balance null")
    @WithMockUser(username = "mockedUser@example.com", roles = {"USER"})
    void testNoAccountBalance() throws Exception {

        when(accountService.getAccountBalance(email)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/balance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test para cuando el balance retorna vacio")
    @WithMockUser(username = "mockedUser@example.com", roles = {"USER"})
    void testEmptyAccountBalance() throws Exception {
        AccountBalanceDTO emptyAccountBalanceDTO = new AccountBalanceDTO();

        when(accountService.getAccountBalance(email)).thenReturn(emptyAccountBalanceDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/balance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountArs").isEmpty())
                .andExpect(jsonPath("$.accountUsd").doesNotExist())
                .andExpect(jsonPath("$.history").isEmpty())
                .andExpect(jsonPath("$.fixedTerms").isEmpty());
    }
}
