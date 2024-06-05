package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.enums.CurrencyType;
import com.magicdogs.alkywall.enums.TypeTransaction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionBalanceDTO {

    @Schema(description = "ID de la transacción")
    private Long idTransaction;

    @Schema(description = "Monto de la transacción")
    private Double amount;

    @Schema(description = "Tipo de transacción")
    private TypeTransaction type;

    @Schema(description = "Descripción de la transacción")
    private String description;

    @Schema(description = "Moneda de la transacción")
    private CurrencyType accountCurrency;

    @Schema(description = "Id de la cuenta con la que se hizo transacción")
    private Long accountIdAccount;

    @Schema(description = "Fecha y hora de la transacción")
    private LocalDateTime transactionDate;
}
