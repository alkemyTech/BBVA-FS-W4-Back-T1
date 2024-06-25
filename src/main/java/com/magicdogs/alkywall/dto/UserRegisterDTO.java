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

    @NotBlank(message = "El nombre no puede estar vacío")
    private String firstName;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String lastName;

    @NotNull(message = "La fecha de nacimiento no puede ser nula")
    @Past(message = "La fecha de nacimiento no puede ser en el futuro")
    private LocalDate birthDate;

    @NotNull(message = "El género no puede ser nulo")
    private UserGender gender;

    @NotNull(message = "El tipo de documento no puede ser nulo")
    private DocumentType documentType;

    @NotBlank(message = "El número de documento no puede estar vacío")
    private String documentNumber;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El correo electrónico debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
}
