package com.magicdogs.alkywall.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {



    @NotEmpty(message = "El correo electrónico no puede estar vacío")
    @NotNull(message = "El correo electrónico no puede ser nulo")
    @NotBlank(message = "El correo electrónico no puede estar en blanco")
    @Email(message = "El correo electrónico debe ser válido")
    private String email;
    @NotEmpty(message = "La contraseña no puede estar vacía")
    @NotNull(message = "La contraseña no puede ser nula")
    @NotBlank(message = "La contraseña no puede estar en blanco")
    private String password;

}
