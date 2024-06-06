package com.magicdogs.alkywall.transactions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.ListTransactionDTO;
import com.magicdogs.alkywall.dto.TransactionDTO;
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
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setOriginIdAccount(1L);
        transactionDTO.setDestinationIdAccount(2L);
        transactionDTO.setConcept(TransactionConcept.VARIOS);
        transactionDTO.setAmount(100.00);

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
        when(modelMapperConfig.listTransactionDTO(transaction)).thenReturn(new ListTransactionDTO());

        // Ejecutar el método y verificar el resultado
        ListTransactionDTO result = transactionService.sendMoney(transactionDTO, currencyType, userEmail);

        assertNotNull(result);

        // Verificar que se llamaron a los métodos necesarios
        verify(userRepository).findByEmail(userEmail);
        verify(accountRepository).findById(2L);
        verify(accountRepository).findByIdAccountAndUser(1L, userOrigin);
        verify(modelMapperConfig).listTransactionDTO(transaction);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    @DisplayName("Cuenta destino no encontrada")
    @Test
    public void testSendMoney_AccountDestinationNotFound() {
        // Mock de los datos de entrada
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setDestinationIdAccount(2L);
        CurrencyType currencyType = CurrencyType.ARS;
        String userEmail = "test@example.com";

        // Mock del repositorio de cuentas para que devuelva Optional.empty()
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        // Verificar que se lance ApiException con el mensaje adecuado
        ApiException exception = assertThrows(ApiException.class, () -> {
            transactionService.sendMoney(transactionDTO, currencyType, userEmail);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Cuenta destino no encontrada", exception.getMessage());
    }

    @DisplayName("Usuario no encontrado")
    @Test
    public void testSendMoney_UserNotFound() {
        // Mock de los datos de entrada
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setOriginIdAccount(1L);
        transactionDTO.setDestinationIdAccount(2L);
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
            transactionService.sendMoney(transactionDTO, currencyType, userEmail);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @DisplayName("Cuenta origen no encontrada")
    @Test
    public void testSendMoney_AccountOriginNotFound() {
        // Mock de los datos de entrada
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setOriginIdAccount(1L);
        transactionDTO.setDestinationIdAccount(2L);
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
            transactionService.sendMoney(transactionDTO, currencyType, userEmail);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Cuenta origen no encontrada", exception.getMessage());
    }

    @DisplayName("Tipo de moneda no válida")
    @Test
    public void testSendMoney_InvalidCurrencyType() {
        // Mock de los datos de entrada
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setOriginIdAccount(1L);
        transactionDTO.setDestinationIdAccount(2L);
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
            transactionService.sendMoney(transactionDTO, originCurrencyType, userEmail);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Tipos de monedas distintos", exception.getMessage());
    }

}
