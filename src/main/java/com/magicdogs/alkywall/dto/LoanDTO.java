package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO para un prestamos")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoanDTO {
    @Schema(description = "Monto total a pagar")
    private double totalAmount;

    @Schema(description = "Monto a pagar por mes")
    private double monthSubscription;

    @Schema(description = "Interes del prestamo")
    private double interest;
}
