package com.magicdogs.alkywall.accounts;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.magicdogs.alkywall.constants.Constants;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.Role;
import com.magicdogs.alkywall.entities.User;
import com.magicdogs.alkywall.enums.*;
import com.magicdogs.alkywall.repositories.AccountRepository;
import com.magicdogs.alkywall.servicies.AccountService;
import com.magicdogs.alkywall.servicies.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class AccountEntityTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private AccountService accountService;

    private User user;
    private Role userRole;
    private Account account;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("John", "Doe", LocalDate.of(1990, 1, 1), UserGender.MALE,
                DocumentType.DNI, "12345678", "john.doe@example.com", "password", userRole, 0);
        userRole = new Role(1L, RoleNameEnum.USER, "Regular User", LocalDateTime.now(), LocalDateTime.now());
        account = new Account(AccountType.CAJA_AHORRO, CurrencyType.ARS, AccountBank.ALKYWALL, "1234567890123456789012", "Mi cuenta", Constants.getTransactionLimitArs(), 0.0, user, 0);
    }

    // Verificar que se puedan crear cuentas con los campos obligatorios y asociadas a un usuario
    @Test
    @DisplayName("Crear una cuenta con campos obligatorios")
    public void testAccountCreationWithMandatoryFields() {
        assertNotNull(account);
        assertEquals(AccountType.CAJA_AHORRO, account.getAccountType());
        assertEquals(CurrencyType.ARS, account.getCurrency());
        assertEquals(AccountBank.ALKYWALL, account.getBank());
        assertEquals("1234567890123456789012", account.getCbu());
        assertEquals("Mi cuenta", account.getAlias());
        assertEquals(Constants.getTransactionLimitArs(), account.getTransactionLimit());
        assertEquals(0.0, account.getBalance());
        assertEquals(user, account.getUser());
        assertEquals(0, account.getSoftDelete());

        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account savedAccount = accountRepository.save(account);

        verify(accountRepository, times(1)).save(savedAccount);
    }

    // Validar que el campo currency sea un Enum que contenga los tipos de moneda permitidos
    @Test
    @DisplayName("Chequear la validez del tipo de moneda")
    public void testCurrencyEnumValidity() {
        CurrencyType[] validCurrencies = {CurrencyType.ARS, CurrencyType.USD};

        for (CurrencyType currency : validCurrencies) {
            assertNotNull(currency);
        }
    }

    // Verificar que los timestamps (creationDate y updateDate) se generen correctamente al crear una cuenta
    @Test
    @DisplayName("Generación de timestamps al crear una cuenta")
    public void testTimestampGenerationOnAccountCreation() {
        account.onCreate();

        LocalDateTime currentDateTime = LocalDateTime.now();
        assertNotNull(account.getCreationDate());
        assertNotNull(account.getUpdateDate());
        assertEquals(account.getCreationDate(), account.getUpdateDate());
        assertTrue(account.getCreationDate().isBefore(currentDateTime) || account.getCreationDate().isEqual(currentDateTime));
    }

    // Asegurarse de que el softDelete funcione correctamente al eliminar una cuenta
    @Test
    @DisplayName("Asegurar de que el softDelete funcione correctamente al eliminar una cuenta")
    public void testSoftDelete() {
        when(jwtService.extractUserId(anyString())).thenReturn(user.getEmail());
        when(accountRepository.findByIdAccountAndUserEmail(eq(account.getIdAccount()), eq(user.getEmail()))).thenReturn(Optional.of(account));

        accountService.softDeleteAccount(account.getIdAccount(), user.getEmail());

        assertEquals(1, account.getSoftDelete());
        verify(accountRepository, times(1)).save(account);
    }
}
