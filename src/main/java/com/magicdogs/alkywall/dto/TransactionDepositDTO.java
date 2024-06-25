package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.enums.AccountType;
import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.enums.TransactionConcept;
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

    @NotNull(message = "Indicar el tipo de cuenta")
    @Schema(description = "Tipo de cuenta")
    private AccountType accountType;

    @NotNull(message = "Indicar el tipo moneda")
    @Schema(description = "Tipo de moneda")
    private CurrencyType currency;

    @NotNull(message = "El concepto no puede ser nulo")
    @Schema(description = "Concepto de la transacción")
    private TransactionConcept concept;

    @Schema(description = "Descripcion de la transaccion")
    private String description;
}
