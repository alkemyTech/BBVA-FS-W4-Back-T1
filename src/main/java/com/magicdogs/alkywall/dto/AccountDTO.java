package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.entities.CurrencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    @NotNull
    @NotBlank
    @NotEmpty
    private Long idAccount;

    @NotNull
    @NotBlank
    @NotEmpty
    private CurrencyType currency;

    @NotNull
    @NotBlank
    @NotEmpty
    private String cbu;

    @NotNull
    @NotBlank
    @NotEmpty
    private Double transactionLimit;
}
