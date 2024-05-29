package com.magicdogs.alkywall.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para iniciar sesion con un usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {

    @NotEmpty
    @NotNull
    @NotBlank
    @Email
    @Schema(description = "Correo electrónico del usuario")
    private String email;

    @NotEmpty
    @NotNull
    @NotBlank
    @Schema(description = "Contraseña del usuario")
    private String password;

}
