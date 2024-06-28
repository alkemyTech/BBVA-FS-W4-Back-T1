package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedTermsBalanceDTO {

    @Schema(description = "ID del plazo fijo")
    private Long idDeposit;

    @Schema(description = "Cantidad del depósito")
    private Double amount;

    @Schema(description = "Interés del plazo fijo")
    private Double interest;

    @Schema(description = "Fecha y hora de creación del depósito")
    private LocalDateTime creationDate;

    @Schema(description = "Fecha y hora de cierre del depósito")
    private LocalDateTime closingDate;
    @Schema(description = "Monto total a recibir")
    private Double amountTotalToReceive;

}
