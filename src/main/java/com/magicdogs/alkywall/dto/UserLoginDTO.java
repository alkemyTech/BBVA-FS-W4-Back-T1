package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @NotEmpty(message = "El correo electrónico no puede estar vacío")
    @NotNull(message = "El correo electrónico no puede ser nulo")
    @NotBlank(message = "El correo electrónico no puede estar en blanco")
    @Email(message = "El correo electrónico debe ser válido")
    @Schema(description = "Correo electrónico del usuario")
    private String email;

    @NotEmpty(message = "La contraseña no puede estar vacía")
    @NotNull(message = "La contraseña no puede ser nula")
    @NotBlank(message = "La contraseña no puede estar en blanco")
    @Schema(description = "Contraseña del usuario")
    private String password;

}
