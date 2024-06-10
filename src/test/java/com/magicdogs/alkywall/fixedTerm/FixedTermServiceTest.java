package com.magicdogs.alkywall.fixedTerm;

import com.magicdogs.alkywall.constants.Constants;
import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.FixedTermCreateDTO;
import com.magicdogs.alkywall.dto.FixedTermSimulatedDTO;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.FixedTermDeposit;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.FixedTermDepositRepository;
import com.magicdogs.alkywall.servicies.AccountService;
import com.magicdogs.alkywall.servicies.FixedTermService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FixedTermServiceTest {
    @Mock
    private ModelMapperConfig modelMapperConfig;

    @Mock
    private FixedTermDepositRepository fixedTermDepositRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private FixedTermService fixedTermService;

    private FixedTermCreateDTO validFixedTermCreateDTO;
    private Account validAccount;
    private FixedTermDeposit validFixedTermDeposit;
    private FixedTermSimulatedDTO validFixedTermSimulatedDTO;

    @BeforeEach
    @WithMockUser(username = "user@example.com")
    void setUp() {
        validFixedTermCreateDTO = new FixedTermCreateDTO();
        validFixedTermCreateDTO.setAmount(1000.0);
        validFixedTermCreateDTO.setClosingDate(LocalDate.now().plusDays(Constants.getFixedTermMinimunDays() + 1));

        validAccount = new Account();
        validAccount.setCurrency(CurrencyType.ARS);
        validAccount.setBalance(2000.0);

        validFixedTermDeposit = new FixedTermDeposit();
        validFixedTermDeposit.setAccount(validAccount);
        validFixedTermDeposit.setAmount(1000.0);
        validFixedTermDeposit.setCreationDate(LocalDateTime.now()); // Inicializa creationDate
        validFixedTermDeposit.setClosingDate(LocalDateTime.now().plusDays(Constants.getFixedTermMinimunDays() + 1));
        validFixedTermDeposit.setInterest(Constants.getFixedTermInterestPercent());

        validFixedTermSimulatedDTO = new FixedTermSimulatedDTO();
        validFixedTermSimulatedDTO.setIdDeposit(1L);
        validFixedTermSimulatedDTO.setAmount(1000.0);
        validFixedTermSimulatedDTO.setInterest(Constants.getFixedTermInterestPercent());
        validFixedTermSimulatedDTO.setCreationDate(validFixedTermDeposit.getCreationDate());
        validFixedTermSimulatedDTO.setClosingDate(validFixedTermDeposit.getClosingDate());
        validFixedTermSimulatedDTO.setInterestTotal(100.0);  // Suponiendo que este es el valor esperado
        validFixedTermSimulatedDTO.setInterestTodayWin(1.0); // Suponiendo que este es el valor esperado
        validFixedTermSimulatedDTO.setAmountTotalToReceive(1100.0); // Suponiendo que este es el valor esperado
        //mockStatic(Constants.class);
    }



    @Test
    void testCreateFixedTermDepositSuccess() {
        when(accountService.getAccountsByUserEmail(anyString())).thenReturn(Optional.of(List.of(validAccount)));
        when(modelMapperConfig.fixedTermsCreateDTOToEntitie(any(FixedTermCreateDTO.class))).thenReturn(validFixedTermDeposit);
        when(fixedTermDepositRepository.save(any(FixedTermDeposit.class))).thenReturn(validFixedTermDeposit);
        when(modelMapperConfig.fixedTermsEntitieToSimulatedDTO(any(FixedTermDeposit.class))).thenReturn(validFixedTermSimulatedDTO);

        FixedTermSimulatedDTO result = fixedTermService.createFixedTermDeposit(validFixedTermCreateDTO, "test@example.com");

        assertNotNull(result);
        assertEquals(validFixedTermSimulatedDTO, result);  // Verifica que el resultado sea igual a la simulación esperada
        verify(fixedTermDepositRepository, times(1)).save(validFixedTermDeposit);
    }

    @Test
    void testCreateFixedTermDepositUserNotFound() {
        when(accountService.getAccountsByUserEmail(anyString())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            fixedTermService.createFixedTermDeposit(validFixedTermCreateDTO, "test@example.com");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void testCreateFixedTermDepositAccountNotFound() {
        when(accountService.getAccountsByUserEmail(anyString())).thenReturn(Optional.of(Collections.emptyList()));

        ApiException exception = assertThrows(ApiException.class, () -> {
            fixedTermService.createFixedTermDeposit(validFixedTermCreateDTO, "test@example.com");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Cuenta no encontrada por usuario", exception.getMessage());
    }

    @Test
    void testCreateFixedTermDepositInvalidDate() {
        validFixedTermCreateDTO.setClosingDate(LocalDate.now().minusDays(6));

        when(accountService.getAccountsByUserEmail(anyString())).thenReturn(Optional.of(List.of(validAccount)));
         ApiException exception = assertThrows(ApiException.class, () -> {
            fixedTermService.createFixedTermDeposit(validFixedTermCreateDTO, "test@example.com");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Fecha inválida, debe ser una fecha futura", exception.getMessage());
    }


    @Test
    void testCreateFixedTermDepositInsufficientBalance() {
        validAccount.setBalance(500.0);
        when(accountService.getAccountsByUserEmail(anyString())).thenReturn(Optional.of(List.of(validAccount)));

        ApiException exception = assertThrows(ApiException.class, () -> {
            fixedTermService.createFixedTermDeposit(validFixedTermCreateDTO, "test@example.com");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Balance insuficiente", exception.getMessage());
    }

    // hacmeelo en el front.
   /*  @Test
    void testCreateFixedTermDepositDurationTooShort() {
       PowerMockito.mockStatic(Constants.class);
        when(Constants.getFixedTermMinimunDays()).thenReturn(30);

        validFixedTermCreateDTO.setClosingDate(LocalDate.now().plusDays(2));
        when(accountService.getAccountsByUserEmail(anyString())).thenReturn(Optional.of(List.of(validAccount)));
        when(modelMapperConfig.fixedTermsCreateDTOToEntitie(any(FixedTermCreateDTO.class)))
                .thenReturn(validFixedTermDeposit);

        ApiException exception = assertThrows(ApiException.class, () -> {
            fixedTermService.createFixedTermDeposit(validFixedTermCreateDTO, "test@example.com");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertTrue(exception.getMessage().contains("La fecha no puede ser a menos de " + Constants.getFixedTermMinimunDays() + " dias"));
    }
*/
}
