package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO para simular un prestamo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanSimulateDTO {
    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser mayor a cero")
    @Schema(description = "Monto solicitado")
    private Double amount;

    @NotNull(message = "La cantidad de meses no puede ser nula")
    @Positive(message = "La cantidad de meses debe ser mayor a cero")
    @Schema(description = "Plazo en meses")
    private Integer term;
}
