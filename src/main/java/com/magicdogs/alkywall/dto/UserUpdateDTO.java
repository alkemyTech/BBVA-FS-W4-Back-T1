package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.enums.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {
    @Schema(description = "Nombre de usuario")
    private String firstName;

    @Schema(description = "Apellido del usuario")
    private String lastName;

    @Schema(description = "Fecha de nacimiento del usuario")
    @Past(message = "La fecha de nacimiento no puede ser en el futuro")
    private LocalDate birthDate;

    @Schema(description = "Género del usuario")
    private UserGender gender;

    @Schema(description = "Número de documento del usuario")
    private String documentNumber;

    @Schema(description = "Contraseña del usuario")
    private String password;
}
