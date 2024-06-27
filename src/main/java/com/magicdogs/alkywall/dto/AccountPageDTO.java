package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountPageDTO {
    @Schema(description = "Lista con todas las cuentas de una pagina")
    private List<?> accounts;
    @Schema(description = "Direccion a la siguiente pagina si es que existe")
    private String nextPage;
    @Schema(description = "Direccion a la pagina anterior si es que existe")
    private String prevPage;
    @Schema(description = "Cantidad de paginas totales existentes")
    private int countPages;

}
