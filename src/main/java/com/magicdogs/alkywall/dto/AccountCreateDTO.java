package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.enums.AccountType;
import com.magicdogs.alkywall.enums.CurrencyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateDTO {
    @NotNull(message = "Indicar el tipo de cuenta")
    @Schema(description = "Tipo de cuenta", example = "CAJA_AHORRO")
    private AccountType accountType;

    @NotNull(message = "Indicar el tipo de moneda")
    @Schema(description = "Tipo de moneda de la cuenta", example = "ARS")
    private CurrencyType currency;
}