package com.magicdogs.alkywall.accounts;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.magicdogs.alkywall.controllers.AccountController;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.dto.AccountPageDTO;
import com.magicdogs.alkywall.enums.AccountBank;
import com.magicdogs.alkywall.enums.AccountType;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.servicies.AccountService;
import com.magicdogs.alkywall.servicies.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @Mock
    private JWTService jwtService;

    private List<AccountDTO> accountDTOList;

    @BeforeEach
    public void setup() {
        AccountDTO account1 = new AccountDTO(1L, AccountType.CAJA_AHORRO, CurrencyType.ARS, AccountBank.ALKYWALL, "1234567890123456789012", "my-savings-account", 5000.00, 100.00);
        AccountDTO account2 = new AccountDTO(2L, AccountType.CUENTA_CORRIENTE, CurrencyType.USD, AccountBank.ALKYWALL, "9876543210987654321098", "my-checking-account", 10000.00, 200.00);
        accountDTOList = Arrays.asList(account1, account2);
    }

    @DisplayName("Obtener lista de cuentas por ID de usuario - Éxito")
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAccountListByUserSuccess() throws Exception {
        AccountPageDTO accountPageDTO = new AccountPageDTO(accountDTOList, "/accounts/1?page=1", "", 1);

        when(accountService.accountsByUser(1L, 0, 0, 10)).thenReturn(accountPageDTO);

        mockMvc.perform(get("/accounts/1")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts").isArray())
                .andExpect(jsonPath("$.accounts[0].idAccount").value(1L))
                .andExpect(jsonPath("$.nextPage").value(""));
    }

    @DisplayName("Obtener lista de cuentas por ID de usuario - Acceso denegado")
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testAccountListByUserAccessDenied() throws Exception {
        mockMvc.perform(get("/accounts/1")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}