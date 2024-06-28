package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPageDTO {

    @Schema(description = "Lista de usuarios")
    private List<UserDTO> users;
    @Schema(description = "Enlace a la siguiente página")
    private String nextPage;
    @Schema(description = "Enlace a la página anterior")
    private String prevPage;
    @Schema(description = "Total de páginas")
    private Integer totalPages;
}
