package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.entities.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateDTO {

    @NotNull
    @NotBlank
    @NotEmpty
    @Schema(description = "Tipo de moneda de la cuenta", example = "ARS")
    private CurrencyType currency;

    @NotNull
    @NotBlank
    @NotEmpty
    @Schema(description = "CBU de la cuenta")
    private String cbu;
}
