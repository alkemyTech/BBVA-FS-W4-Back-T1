package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedTermPageDTO {
    @Schema(description = "Lista de una pagina de plazos fijos")
    private List<?> fixedTerms;
    @Schema(description = "Direccion a la siguiente pagina de plazos fijos si es que existen ")
    private String nextPage;
    @Schema(description = "Direccion a la pagina anterior de plazos fijos si es que existen ")
    private String prevPage;
    @Schema(description = "Cantidad de paginas existentes")
    private int countPages;
}
