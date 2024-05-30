package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.Constants;
import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.FixedTermCreateDTO;
import com.magicdogs.alkywall.dto.FixedTermSimulatedDTO;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.entities.FixedTermDeposit;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.FixedTermDepositRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class FixedTermService {
    private final ModelMapperConfig modelMapperConfig;
    private final FixedTermDepositRepository fixedTermDepositRepository;
    private final AccountService accountService;

    public FixedTermSimulatedDTO createFixedTermDeposit (FixedTermCreateDTO fixedTermCreateDTO, String email){
        List<Account> accounts = accountService.getAccountsByUserEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Account cuentaEncontrada = accounts.stream()
                .filter(t -> Objects.equals(t.getCurrency(), CurrencyType.ARS))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cuenta no encontrada por usuario"));


        // Combinar LocalDate y LocalTime para crear LocalDateTime
        LocalTime currentTime = LocalTime.now();
        LocalDateTime diaHoraFinal = LocalDateTime.of(fixedTermCreateDTO.getClosingDate(), currentTime);
        // Validar que la fecha de cierre es futura
        if (diaHoraFinal.isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Fecha inválida, debe ser una fecha futura");
        }
        Long cantDias = Duration.between(LocalDateTime.now(), diaHoraFinal).toDays();
        int cantDiasMinimo = Constants.getFixedTermMinimunDays();
        if (cantDias < cantDiasMinimo) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La fecha no puede ser a menos de "+Constants.getFixedTermMinimunDays()+" dias" );
        }
        if(cuentaEncontrada.getBalance() < fixedTermCreateDTO.getAmount())   throw new ApiException(HttpStatus.BAD_REQUEST, "Balance insuficiente" );;

        FixedTermDeposit fixedTermDeposit = modelMapperConfig.fixedTermsCreateDTOToEntitie(fixedTermCreateDTO);
        fixedTermDeposit.setAccount(cuentaEncontrada);
        fixedTermDeposit.setClosingDate(diaHoraFinal);
        fixedTermDeposit.setInterest(Constants.getFixedTermInterestPercent());

        // POSIBLE FIX PORQUE NO GUARDA LA ACTUALIZACION DE BALANCE DEL ACCOUNT
        cuentaEncontrada.setBalance(cuentaEncontrada.getBalance() - fixedTermDeposit.getAmount());
        fixedTermDeposit = fixedTermDepositRepository.save(fixedTermDeposit);

        FixedTermSimulatedDTO fixedTermSimulatedDTO = modelMapperConfig.fixedTermsEntitieToSimulatedDTO(fixedTermDeposit);
        fixedTermSimulatedDTO.setInterestTotal(fixedTermDeposit.interestTotalWin());
        fixedTermSimulatedDTO.setInterestTodayWin(fixedTermDeposit.interestPartialWin());
        fixedTermSimulatedDTO.setAmountTotalToReceive(fixedTermDeposit.getAmountTotalToReceive());
        return fixedTermSimulatedDTO;
    }



}
