package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedTermSimulatedDTO {
    @Schema(description = "Identificador unico del plazo fijo simulado")
    private Long idDeposit;
    @Schema(description = "Monto del plazo fijo simulado")
    private Double amount;
    @Schema(description = "Taza de interes que se ganaria por el plazo fijo simulado")
    private Double interest;
    @Schema(description = "Fecha y hora de inicio por el plazo fijo simulado")
    private LocalDateTime creationDate;
    @Schema(description = "Fecha y hora de cierre por el plazo fijo simulado")
    private LocalDateTime closingDate;
    @Schema(description = "Interes total que se ganaria por el plazo fijo simulado")
    private Double interestTotal;
    @Schema(description = "Interes ganado por cada dia del plazo fijo simulado")
    private Double interestTodayWin;
    @Schema(description = "Monto total a recibir por el plazo fijo simulado")
    private  Double amountTotalToReceive;
}
