package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.enums.DocumentType;
import com.magicdogs.alkywall.enums.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {

    @Schema(description = "Nombre de usuario")
    @NotBlank(message = "El nombre no puede estar vacío")
    private String firstName;

    @Schema(description = "Apellido del usuario")
    @NotBlank(message = "El apellido no puede estar vacío")
    private String lastName;

    @Schema(description = "Fecha de nacimiento del usuario")
    @NotNull(message = "La fecha de nacimiento no puede ser nula")
    @Past(message = "La fecha de nacimiento no puede ser en el futuro")
    private LocalDate birthDate;

    @Schema(description = "Género del usuario")
    @NotNull(message = "El género no puede ser nulo")
    private UserGender gender;

    @Schema(description = "Tipo de documento del usuario")
    @NotNull(message = "El tipo de documento no puede ser nulo")
    private DocumentType documentType;

    @Schema(description = "Número de documento del usuario")
    @NotBlank(message = "El número de documento no puede estar vacío")
    private String documentNumber;

    @Schema(description = "Correo electrónico del usuario")
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El correo electrónico debe ser válido")
    private String email;

    @Schema(description = "Contraseña del usuario")
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[@#$%^&+=])(?=.*\\d).{8,}$", message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un símbolo")
    private String password;
}
