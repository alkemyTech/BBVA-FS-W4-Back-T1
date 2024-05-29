package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO para una transacción")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {

    @NotNull
    @NotBlank
    @NotEmpty
    @Schema(description = "Id de la cuenta destino")
    private int destinationIdAccount;

    @Schema(description = "Cantidad de dinero a enviar")
    @NotNull
    @NotBlank
    @Positive
    @NotEmpty
    private double amount;

    @Schema(description = "Descripcion de la transaccion")
    private String description;

}
