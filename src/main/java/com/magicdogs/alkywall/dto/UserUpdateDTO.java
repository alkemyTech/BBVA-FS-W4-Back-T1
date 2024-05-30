package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    @Schema(description = "Nombre de usuario")
    private String firstName;

    @Schema(description = "Apellido del usuario")
    private String lastName;

    @Schema(description = "Contraseña del usuario")
    private String password;
}
