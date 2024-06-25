package com.magicdogs.alkywall.servicies;

import com.magicdogs.alkywall.constants.Constants;
import com.magicdogs.alkywall.config.ModelMapperConfig;
import com.magicdogs.alkywall.dto.*;
import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.enums.AccountType;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.entities.FixedTermDeposit;
import com.magicdogs.alkywall.exceptions.ApiException;
import com.magicdogs.alkywall.repositories.FixedTermDepositRepository;
import com.magicdogs.alkywall.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class FixedTermService {
    private final ModelMapperConfig modelMapperConfig;
    private final FixedTermDepositRepository fixedTermDepositRepository;
    private final AccountService accountService;
    private final UserRepository userRepository;

    public FixedTermSimulatedDTO createFixedTermDeposit (FixedTermCreateDTO fixedTermCreateDTO, String email){
        List<Account> accounts = accountService.getAccountsByUserEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Account cuentaEncontrada = accounts.stream()
                .filter(t -> Objects.equals(t.getCurrency(), CurrencyType.ARS) && Objects.equals(t.getAccountType(), AccountType.CAJA_AHORRO))
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

        FixedTermSimulatedDTO fixedTermSimulatedDTO = modelMapperConfig.fixedTermsToSimulatedDTO(fixedTermDeposit);
        fixedTermSimulatedDTO.setInterestTotal(fixedTermDeposit.interestTotalWin());
        fixedTermSimulatedDTO.setInterestTodayWin(fixedTermDeposit.interestPartialWin());
        fixedTermSimulatedDTO.setAmountTotalToReceive(fixedTermDeposit.getAmountTotalToReceive());
        return fixedTermSimulatedDTO;
    }


    public FixedTermSimulatedDTO simulateFixedTerm ( FixedTermCreateDTO fixedTermCreateDTO){

        if (fixedTermCreateDTO.getAmount()<=0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El monto debe ser positivo");
        }
        FixedTermDeposit fixedTermDeposit = modelMapperConfig.fixedTermsCreateDTOToEntitie(fixedTermCreateDTO);
        fixedTermDeposit.setCreationDate(LocalDateTime.now());
        fixedTermDeposit.setClosingDate(LocalDateTime.of(fixedTermCreateDTO.getClosingDate(), LocalTime.now()));
        fixedTermDeposit.setInterest(Constants.getFixedTermInterestPercent());
        FixedTermSimulatedDTO fixedTermSimulatedDTO = modelMapperConfig.fixedTermsToSimulatedDTO(fixedTermDeposit);
        fixedTermSimulatedDTO.setInterestTotal(fixedTermDeposit.interestTotalWin());
        fixedTermSimulatedDTO.setInterestTodayWin(fixedTermDeposit.interestPartialWin());
        fixedTermSimulatedDTO.setAmountTotalToReceive(fixedTermDeposit.getAmountTotalToReceive());
        return fixedTermSimulatedDTO;
    }

    public Object getFixedTerms(String userEmail, int page, int size) {
        if(page < 0 || size <= 0) throw new ApiException(HttpStatus.NOT_FOUND, "El numero de pagina o de size no pueden ser negativos.");

        var user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        var fixedTerms = fixedTermDepositRepository.findByAccount_UserIdUserOrderByClosingDateDesc(user.getIdUser(), PageRequest.of(page, size)).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<FixedTermSimulatedDTO> fixedTermsDTOs = new ArrayList<>();

        for (FixedTermDeposit fixedTermDeposit : fixedTerms.getContent()) {
            FixedTermSimulatedDTO fixedTermSimulatedDTO = modelMapperConfig.fixedTermsToSimulatedDTO(fixedTermDeposit);
            fixedTermSimulatedDTO.setInterestTotal(fixedTermDeposit.interestTotalWin());
            fixedTermSimulatedDTO.setInterestTodayWin(fixedTermDeposit.interestPartialWin());
            fixedTermSimulatedDTO.setAmountTotalToReceive(fixedTermDeposit.getAmountTotalToReceive());
            fixedTermsDTOs.add(fixedTermSimulatedDTO);
        }

        int cant = fixedTerms.getTotalPages();

        if (cant <= page ) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, "No existe el numero de pagina");
        }

        String next = "", prev = "";
        if (fixedTerms.hasNext())
        {next = "/fixedTerm?page="+(page+1);
        }

        if (fixedTerms.hasPrevious()) {
            prev = "/fixedTerm?page="+(page-1);
        }

        return new FixedTermPageDTO(fixedTermsDTOs, next, prev, cant);    }
}
