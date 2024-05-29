package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.entities.CurrencyType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateDTO {
    @NotNull(message = "Indicar el tipo de moneda")
    private CurrencyType currency;
}