package com.magicdogs.alkywall.dto;

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
public class TransactionUpdateDTO {
    @NotNull(message = "La descripción no puede ser nula")
    @NotBlank(message = "La descripción no puede estar en blanco")
    @NotEmpty(message = "La dscripción no puede estar vacía")
    @Schema(description = "Descripción de la transacción")
    private String description;
}
