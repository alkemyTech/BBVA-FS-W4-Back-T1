package com.magicdogs.alkywall.accounts;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.AccountBalanceDTO;
import com.magicdogs.alkywall.dto.AccountDTO;
import com.magicdogs.alkywall.dto.FixedTermsBalanceDTO;
import com.magicdogs.alkywall.dto.TransactionBalanceDTO;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.servicies.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountBalanceServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ModelMapperConfig modelMapperConfig;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Test cuenta no encontrada")
    @Test
    public void testAccountBalanceNotFound(){
        String email = "user@example.com";
        when(accountRepository.findByUserEmail(email)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> {accountService.getAccountBalance(email);});
        verify(accountRepository).findByUserEmail(email);
    }

    @DisplayName("Test usuario sin cuentas")
    @Test
    public void testAccountBalanceUserHasNoAccounts() {
        String userEmail = "user@example.com";
        when(accountRepository.findByUserEmail(userEmail)).thenReturn(Optional.of(List.of()));
        assertThrows(ApiException.class, () -> {accountService.getAccountBalance(userEmail);});
        verify(accountRepository).findByUserEmail(userEmail);
    }

    @DisplayName("Test obtener balance exitoso")
    @Test
    public void testAccountBalanceSuccess(){
        String email = "user@example.com";

        //Cuentas mockeadas
        Account account1 = new Account(); //Cuenta 1 en ARS
        account1.setCurrency(CurrencyType.ARS);
        account1.setTransactions(new ArrayList<>());
        account1.setFixedTermDeposits(new ArrayList<>());

        Account account2 = new Account(); //Cuenta 2 en US
        account2.setCurrency(CurrencyType.USD);
        account2.setTransactions(new ArrayList<>());
        account2.setFixedTermDeposits(new ArrayList<>());

        List<Account> accounts = List.of(account1, account2); //Lista de cuentas 1 y 2

        //findByUserEmail traera la lista mockeada anteriormente
        when(accountRepository.findByUserEmail(email)).thenReturn(Optional.of(accounts));

        //Mock de TransactionBalanceDTO
        TransactionBalanceDTO transactionDTO = new TransactionBalanceDTO();
        when(modelMapperConfig.transactionBalanceToDTO(any())).thenReturn(transactionDTO);

        //Mock de FixedTermsBalanceDTO
        FixedTermsBalanceDTO fixedTermDTO = new FixedTermsBalanceDTO();
        when(modelMapperConfig.fixedTermsBalanceToDTO(any())).thenReturn(fixedTermDTO);

        //Mock de account 1 y 2 a AccountDTO
        AccountDTO accountDTOArs = new AccountDTO();
        when(modelMapperConfig.accountToDTO(account1)).thenReturn(accountDTOArs);
        AccountDTO accountDTOUsd = new AccountDTO();
        when(modelMapperConfig.accountToDTO(account2)).thenReturn(accountDTOUsd);

        //AccountBalanceDTO es o que tiene que devolver el metodo
        AccountBalanceDTO accountBalanceDTO = accountService.getAccountBalance(email);

        assertNotNull(accountBalanceDTO);
        assertFalse(accountBalanceDTO.getAccountArs().isEmpty());
        assertEquals(accountDTOArs, accountBalanceDTO.getAccountArs().getFirst());
        assertEquals(accountDTOUsd, accountBalanceDTO.getAccountUsd());

        verify(accountRepository).findByUserEmail(email);
    }
}
