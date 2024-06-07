package com.magicdogs.alkywall.accounts;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import com.magicdogs.alkywall.servicies.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;

public class AccountUpdateServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ModelMapperConfig modelMapperConfig;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @DisplayName("Actualizar cuenta - Usuario no encontrado")
    @Test
    public void testUpdateAccountUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () ->
                accountService.updateAccount(1L, "user@example.com", 1000.0)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @DisplayName("Actualizar cuenta - Cuenta no encontrada")
    @Test
    public void testUpdateAccountNotFound() {
        User user = new User();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(accountRepository.findByIdAccountAndUser(anyLong(), any(User.class))).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () ->
                accountService.updateAccount(1L, "user@example.com", 1000.0)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("La cuenta no existe", exception.getMessage());
    }

    @DisplayName("Actualizar cuenta - Éxito")
    @Test
    public void testUpdateAccountSuccess() {
        User user = new User();
        Account account = new Account();
        account.setTransactionLimit(500.0);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(accountRepository.findByIdAccountAndUser(anyLong(), any(User.class))).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(modelMapperConfig.accountToDTO(any(Account.class))).thenReturn(new AccountDTO());

        AccountDTO accountDTO = accountService.updateAccount(1L, "user@example.com", 1000.0);

        assertNotNull(accountDTO);
        verify(accountRepository).save(account);
        assertEquals(1000.0, account.getTransactionLimit());
    }
}
