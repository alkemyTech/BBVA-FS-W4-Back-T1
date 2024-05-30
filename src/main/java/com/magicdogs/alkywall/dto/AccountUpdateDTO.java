package com.magicdogs.alkywall.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpdateDTO {
    @NotNull(message = "Indicar el límite de transacción deseado")
    @Positive(message = "El valor del límite debe ser mayor que 0")
    private Double transactionLimit;
}
