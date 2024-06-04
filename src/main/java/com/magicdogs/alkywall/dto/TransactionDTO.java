package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.enums.TransactionConcept;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @NotNull(message = "El id de la cuenta no puede ser nulo")
    @Schema(description = "Id de la cuenta destino")
    private Long destinationIdAccount;

    @NotNull(message = "El monto no puede ser nulo")
    @Positive(message = "El monto debe ser mayor a cero")
    @Schema(description = "Cantidad de dinero a enviar")
    private Double amount;

    @NotNull(message = "El id de la cuenta no puede ser nulo")
    @Schema(description = "Id de la cuenta origen")
    private Long originIdAccount;

    @NotNull(message = "El concepto no puede ser nulo")
    @Schema(description = "Concepto de la transacción")
    private TransactionConcept concept;

    @Schema(description = "Descripcion de la transaccion")
    private String description;
}
