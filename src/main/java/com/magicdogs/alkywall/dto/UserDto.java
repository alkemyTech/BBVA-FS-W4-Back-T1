package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO para un usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @Schema(description = "Nombre de usuario")
    private String firstName;

    @Schema(description = "Apellido de usuario")
    private String lastName;

    @Schema(description = "Indicador de eliminación suave (0 = no eliminado, 1 = eliminado)")
    private Integer softDelete;

    @Schema(description = "Correo electrónico del usuario")
    private String email;
}