package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.enums.DocumentType;
import com.magicdogs.alkywall.enums.UserGender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {

    @NotEmpty(message = "El nombre no puede estar vacío")
    @NotNull(message = "El nombre no puede ser nulo")
    @NotBlank(message = "El nombre no puede estar en blanco")
    private String firstName;

    @NotEmpty(message = "El apellido no puede estar vacío")
    @NotNull(message = "El apellido no puede ser nulo")
    @NotBlank(message = "El apellido no puede estar en blanco")
    private String lastName;

    @NotNull(message = "La fecha de nacimiento no puede ser nula")
    @Past(message = "La fecha de nacimiento no puede ser en el futuro")
    private LocalDate birthDate;

    @NotNull(message = "El género no puede ser nulo")
    private UserGender gender;

    @NotNull(message = "El tipo de documento no puede ser nulo")
    private DocumentType documentType;

    @NotEmpty(message = "El número de documento no puede estar vacío")
    @NotNull(message = "El número de documento no puede ser nulo")
    @NotBlank(message = "El número de documento no puede estar en blanco")
    private String documentNumber;

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
