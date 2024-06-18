package com.magicdogs.alkywall.transactions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.magicdogs.alkywall.dto.*;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.Transaction;
import com.magicdogs.alkywall.enums.*;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.*;
import com.magicdogs.alkywall.servicies.TransactionService;
import com.magicdogs.alkywall.config.ModelMapperConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepositServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ModelMapperConfig modelMapperConfig;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Depósito exitoso")
    @Test
    public void testDepositSuccess() {
        TransactionDepositDTO depositDTO = new TransactionDepositDTO(100.0, AccountType.CAJA_AHORRO, CurrencyType.ARS, TransactionConcept.VARIOS, "test deposit description");

        List<Account> accounts = new ArrayList<>();

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setGender(UserGender.MALE);
        user.setDocumentType(DocumentType.DNI);
        user.setDocumentNumber("12345678");
        user.setEmail("user@example.com");
        user.setPassword("password123");
        user.setSoftDelete(0);

        Account account = new Account(
                AccountType.CAJA_AHORRO,
                CurrencyType.ARS,
                AccountBank.ALKYWALL,
                "1234567890123456789012",
                "alias123",
                10000.0,
                0.0,
                user,
                0
        );

        accounts.add(account);
        user.setAccounts(accounts);

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));

        Transaction transaction = new Transaction();
        transaction.setAmount(100.0);
        transaction.setType(TypeTransaction.DEPOSIT);
        transaction.setConcept(TransactionConcept.VARIOS);
        transaction.setDescription("test deposit description");
        transaction.setAccount(account);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        account.setBalance(account.getBalance() + 100.0);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setIdTransaction(1L);
        transactionDTO.setAmount(100.0);
        transactionDTO.setType(TypeTransaction.DEPOSIT);
        transactionDTO.setConcept(TransactionConcept.VARIOS);
        transactionDTO.setDescription("test deposit description");
        transactionDTO.setAccountIdAccount(1L);
        when(modelMapperConfig.transactionToDTO(any(Transaction.class))).thenReturn(transactionDTO);

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setIdAccount(1L);
        accountDTO.setAccountType(AccountType.CAJA_AHORRO);
        accountDTO.setCurrency(CurrencyType.ARS);
        accountDTO.setBank(AccountBank.ALKYWALL);
        accountDTO.setCbu("1234567890123456789012");
        accountDTO.setAlias("alias123");
        accountDTO.setTransactionLimit(10000.0);
        accountDTO.setBalance(100.0);
        when(modelMapperConfig.accountToDTO(any(Account.class))).thenReturn(accountDTO);

        TransactionAccountDTO result = transactionService.deposit(depositDTO, "user@example.com");

        assertNotNull(result);
        assertNotNull(result.getTransaction());
        assertNotNull(result.getAccount());
        assertEquals(100.0, result.getTransaction().getAmount());
        assertEquals(100.0, result.getAccount().getBalance());

        verify(userRepository).findByEmail("user@example.com");
        verify(transactionRepository).save(any(Transaction.class));
        verify(accountRepository).save(any(Account.class));
    }

    @DisplayName("Depósito fallido - Usuario no encontrado")
    @Test
    public void testDepositUserNotFound() {
        TransactionDepositDTO depositDTO = new TransactionDepositDTO(100.0, AccountType.CAJA_AHORRO, CurrencyType.ARS, TransactionConcept.VARIOS, "test deposit description");

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> transactionService.deposit(depositDTO, "user@example.com"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(userRepository).findByEmail("user@example.com");
    }

    @DisplayName("Depósito fallido - Cuenta no encontrada")
    @Test
    public void testDepositAccountNotFound() {
        TransactionDepositDTO depositDTO = new TransactionDepositDTO(100.0, AccountType.CAJA_AHORRO, CurrencyType.ARS, TransactionConcept.VARIOS, "test deposit description");

        List<Account> accounts = new ArrayList<>();

        User user = Mockito.mock(User.class);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setGender(UserGender.MALE);
        user.setDocumentType(DocumentType.DNI);
        user.setDocumentNumber("12345678");
        user.setEmail("user@example.com");
        user.setPassword("password123");
        user.setSoftDelete(0);

        Account account = new Account(
                AccountType.CAJA_AHORRO,
                CurrencyType.ARS,
                AccountBank.ALKYWALL,
                "0987654321098765432109",
                "alias456",
                5000.0,
                0.0,
                user,
                0
        );

        accounts.add(account);
        user.setAccounts(accounts);

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));

        when(user.getAccountIn(any(AccountType.class), any(CurrencyType.class))).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () -> transactionService.deposit(depositDTO, "user@example.com"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Cuenta no encontrada", exception.getMessage());

        verify(userRepository).findByEmail("user@example.com");
    }
}
