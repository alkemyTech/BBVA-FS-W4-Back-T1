package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionPageDTO {

    @Schema(description = "Lista de transacciones")
    private List<TransactionDTO> transactios;
    @Schema(description = "Enlace a la página siguiente")
    private String nextPage;
    @Schema(description = "Enlace a la pagina anterior")
    private String prevPage;
    @Schema(description = "Cantidad de páginas")
    private int countPages;
    @Schema(description = "Monto máximo de transacción")
    private Double maxAmmout;
}
