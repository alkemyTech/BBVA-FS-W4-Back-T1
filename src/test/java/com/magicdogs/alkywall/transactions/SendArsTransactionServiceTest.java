package com.magicdogs.alkywall.transactions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.TransactionDTO;
import com.magicdogs.alkywall.dto.TransactionSendMoneyDTO;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.Transaction;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.enums.TransactionConcept;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.repositories.TransactionRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import com.magicdogs.alkywall.servicies.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

public class SendArsTransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapperConfig modelMapperConfig;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Envio de dinero en pesos exitoso")
    @Test
    public void testSendArsSuccessfully() {
        // Mock de los datos de entrada
        TransactionSendMoneyDTO transactionSendMoneyDTO = new TransactionSendMoneyDTO();
        transactionSendMoneyDTO.setOriginIdAccount(1L);
        transactionSendMoneyDTO.setDestinationIdAccount(2L);
        transactionSendMoneyDTO.setConcept(TransactionConcept.VARIOS);
        transactionSendMoneyDTO.setAmount(100.00);

        CurrencyType currencyType = CurrencyType.ARS;
        String userEmail = "test@example.com";

        // Mock de las respuestas de los repositorios
        User userOrigin = new User();
        Account accountOrigin = new Account();
        accountOrigin.setCurrency(currencyType);
        accountOrigin.setBalance(300.0);
        accountOrigin.setTransactionLimit(600.0);
        Account accountDestination = new Account();
        accountDestination.setCurrency(currencyType);
        accountDestination.setBalance(300.0);
        accountDestination.setTransactionLimit(300.0);

        Transaction transaction = new Transaction();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userOrigin));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountDestination));
        when(accountRepository.findByIdAccountAndUser(1L, userOrigin)).thenReturn(Optional.of(accountOrigin));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
        when(modelMapperConfig.transactionToDTO(transaction)).thenReturn(new TransactionDTO());

        // Ejecutar el método y verificar el resultado
        TransactionDTO result = transactionService.sendMoney(transactionSendMoneyDTO, currencyType, userEmail);

        assertNotNull(result);

        // Verificar que se llamaron a los métodos necesarios
        verify(userRepository).findByEmail(userEmail);
        verify(accountRepository).findById(2L);
        verify(accountRepository).findByIdAccountAndUser(1L, userOrigin);
        verify(modelMapperConfig).transactionToDTO(transaction);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @DisplayName("Cuenta destino no encontrada")
    @Test
    public void testSendMoney_AccountDestinationNotFound() {
        // Mock de los datos de entrada
        TransactionSendMoneyDTO transactionSendMoneyDTO = new TransactionSendMoneyDTO();
        transactionSendMoneyDTO.setDestinationIdAccount(2L);
        CurrencyType currencyType = CurrencyType.ARS;
        String userEmail = "test@example.com";

        // Mock del repositorio de cuentas para que devuelva Optional.empty()
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        // Verificar que se lance ApiException con el mensaje adecuado
        ApiException exception = assertThrows(ApiException.class, () -> {
            transactionService.sendMoney(transactionSendMoneyDTO, currencyType, userEmail);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Cuenta destino no encontrada", exception.getMessage());
    }

    @DisplayName("Usuario no encontrado")
    @Test
    public void testSendMoney_UserNotFound() {
        // Mock de los datos de entrada
        TransactionSendMoneyDTO transactionSendMoneyDTO = new TransactionSendMoneyDTO();
        transactionSendMoneyDTO.setOriginIdAccount(1L);
        transactionSendMoneyDTO.setDestinationIdAccount(2L);
        CurrencyType currencyType = CurrencyType.ARS;
        String userEmail = "test@example.com";

        Account accountDestination = new Account();
        accountDestination.setCurrency(currencyType);
        accountDestination.setBalance(300.0);
        accountDestination.setTransactionLimit(300.0);
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountDestination));

        // Mock del repositorio de usuarios para que devuelva Optional.empty()
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // Verificar que se lance ApiException con el mensaje adecuado
        ApiException exception = assertThrows(ApiException.class, () -> {
            transactionService.sendMoney(transactionSendMoneyDTO, currencyType, userEmail);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @DisplayName("Cuenta origen no encontrada")
    @Test
    public void testSendMoney_AccountOriginNotFound() {
        // Mock de los datos de entrada
        TransactionSendMoneyDTO transactionSendMoneyDTO = new TransactionSendMoneyDTO();
        transactionSendMoneyDTO.setOriginIdAccount(1L);
        transactionSendMoneyDTO.setDestinationIdAccount(2L);
        CurrencyType currencyType = CurrencyType.ARS;
        String userEmail = "test@example.com";

        Account accountDestination = new Account();
        accountDestination.setCurrency(currencyType);
        accountDestination.setBalance(300.0);
        accountDestination.setTransactionLimit(300.0);

        // Mock del repositorio de usuarios para que devuelva un usuario válido
        User userOrigin = new User();
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userOrigin));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountDestination));

        // Mock del repositorio de cuentas para que devuelva Optional.empty()
        when(accountRepository.findByIdAccountAndUser(1L, userOrigin)).thenReturn(Optional.empty());

        // Verificar que se lance ApiException con el mensaje adecuado
        ApiException exception = assertThrows(ApiException.class, () -> {
            transactionService.sendMoney(transactionSendMoneyDTO, currencyType, userEmail);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Cuenta origen no encontrada", exception.getMessage());
    }

    @DisplayName("Tipo de moneda no válida")
    @Test
    public void testSendMoney_InvalidCurrencyType() {
        // Mock de los datos de entrada
        TransactionSendMoneyDTO transactionSendMoneyDTO = new TransactionSendMoneyDTO();
        transactionSendMoneyDTO.setOriginIdAccount(1L);
        transactionSendMoneyDTO.setDestinationIdAccount(2L);
        CurrencyType originCurrencyType = CurrencyType.ARS;
        CurrencyType destinationCurrencyType = CurrencyType.USD; // Tipo de moneda diferente
        String userEmail = "test@example.com";

        // Mock del repositorio de usuarios para que devuelva un usuario válido
        User userOrigin = new User();
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userOrigin));
        // Mock del repositorio de cuentas para que devuelva cuentas con tipos de moneda diferentes
        Account accountOrigin = new Account();
        accountOrigin.setCurrency(originCurrencyType);
        Account accountDestination = new Account();
        accountDestination.setCurrency(destinationCurrencyType);
        when(accountRepository.findByIdAccountAndUser(1L, userOrigin)).thenReturn(Optional.of(accountOrigin));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountDestination));

        // Verificar que se lance ApiException con el mensaje adecuado
        ApiException exception = assertThrows(ApiException.class, () -> {
            transactionService.sendMoney(transactionSendMoneyDTO, originCurrencyType, userEmail);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Tipos de monedas distintos", exception.getMessage());
    }

    @DisplayName("Balance insuficiente")
    @Test
    public void testSendMoney_InsufficientBalance() {
        // Mock de los datos de entrada
        TransactionSendMoneyDTO transactionSendMoneyDTO = new TransactionSendMoneyDTO();
        transactionSendMoneyDTO.setOriginIdAccount(1L);
        transactionSendMoneyDTO.setDestinationIdAccount(2L);
        transactionSendMoneyDTO.setAmount(500.0); // Monto mayor que el balance de la cuenta de origen
        CurrencyType currencyType = CurrencyType.ARS;
        String userEmail = "test@example.com";

        // Mock del repositorio de usuarios para que devuelva un usuario válido
        User userOrigin = new User();
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userOrigin));
        // Mock del repositorio de cuentas para que devuelva cuentas válidas
        Account accountOrigin = new Account();
        accountOrigin.setCurrency(currencyType);
        accountOrigin.setBalance(300.0); // Balance insuficiente
        accountOrigin.setTransactionLimit(600.0);
        Account accountDestination = new Account();
        accountDestination.setCurrency(currencyType);
        accountDestination.setBalance(300.0);
        accountDestination.setTransactionLimit(300.0);
        when(accountRepository.findByIdAccountAndUser(1L, userOrigin)).thenReturn(Optional.of(accountOrigin));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountDestination));

        // Verificar que se lance ApiException con el mensaje adecuado
        ApiException exception = assertThrows(ApiException.class, () -> {
            transactionService.sendMoney(transactionSendMoneyDTO, currencyType, userEmail);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Balance insuficiente", exception.getMessage());
    }

    @DisplayName("Límite de transacción insuficiente")
    @Test
    public void testSendMoney_InsufficientTransactionLimit() {
        // Mock de los datos de entrada
        TransactionSendMoneyDTO transactionSendMoneyDTO = new TransactionSendMoneyDTO();
        transactionSendMoneyDTO.setOriginIdAccount(1L);
        transactionSendMoneyDTO.setDestinationIdAccount(2L);
        transactionSendMoneyDTO.setAmount(500.0); // Monto mayor que el límite de transacción de la cuenta de origen
        CurrencyType currencyType = CurrencyType.ARS;
        String userEmail = "test@example.com";

        // Mock del repositorio de usuarios para que devuelva un usuario válido
        User userOrigin = new User();
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userOrigin));
        // Mock del repositorio de cuentas para que devuelva cuentas válidas
        Account accountOrigin = new Account();
        accountOrigin.setCurrency(currencyType);
        accountOrigin.setBalance(600.0);
        accountOrigin.setTransactionLimit(400.0); // Límite insuficiente
        Account accountDestination = new Account();
        accountDestination.setCurrency(currencyType);
        accountDestination.setBalance(300.0);
        accountDestination.setTransactionLimit(300.0);
        when(accountRepository.findByIdAccountAndUser(1L, userOrigin)).thenReturn(Optional.of(accountOrigin));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountDestination));

        // Verificar que se lance ApiException con el mensaje adecuado
        ApiException exception = assertThrows(ApiException.class, () -> {
            transactionService.sendMoney(transactionSendMoneyDTO, currencyType, userEmail);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Limite insuficiente", exception.getMessage());
    }

    @DisplayName("Validar actualizaciones de balances")
    @Test
    public void testSendMoney_ValidateBalanceUpdates() {
        // Mock de los datos de entrada
        TransactionSendMoneyDTO transactionSendMoneyDTO = new TransactionSendMoneyDTO();
        transactionSendMoneyDTO.setOriginIdAccount(1L);
        transactionSendMoneyDTO.setDestinationIdAccount(2L);
        transactionSendMoneyDTO.setAmount(100.0);
        CurrencyType currencyType = CurrencyType.ARS;
        String userEmail = "test@example.com";

        // Mock del repositorio de usuarios para que devuelva un usuario válido
        User userOrigin = new User();
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(userOrigin));
        // Mock del repositorio de cuentas para que devuelva cuentas válidas
        Account accountOrigin = new Account();
        accountOrigin.setCurrency(currencyType);
        accountOrigin.setBalance(600.0);
        accountOrigin.setTransactionLimit(600.0);
        Account accountDestination = new Account();
        accountDestination.setCurrency(currencyType);
        accountDestination.setBalance(300.0);
        accountDestination.setTransactionLimit(300.0);
        when(accountRepository.findByIdAccountAndUser(1L, userOrigin)).thenReturn(Optional.of(accountOrigin));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(accountDestination));

        // Mock de las transacciones guardadas
        Transaction transactionIncomeDestination = new Transaction();
        Transaction transactionPaymentOrigin = new Transaction();
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transactionIncomeDestination)
                .thenReturn(transactionPaymentOrigin);
        when(modelMapperConfig.transactionToDTO(transactionPaymentOrigin)).thenReturn(new TransactionDTO());

        // Ejecutar el método y verificar el resultado
        transactionService.sendMoney(transactionSendMoneyDTO, currencyType, userEmail);

        // Verificar que los balances se actualizaron correctamente
        assertEquals(500.0, accountOrigin.getBalance());
        assertEquals(400.0, accountDestination.getBalance());

        // Verificar que las transacciones se guardaron correctamente
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

}
