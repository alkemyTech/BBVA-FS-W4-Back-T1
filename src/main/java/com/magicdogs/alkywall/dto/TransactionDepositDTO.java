package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.enums.CurrencyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO para un depósito")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDepositDTO {
    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser mayor a cero")
    @Schema(description = "Cantidad de dinero a depositar")
    private Double amount;

    @NotNull(message = "Indicar el tipo de moneda")
    @Schema(description = "Tipo de cuenta")
    private CurrencyType currency;
}
