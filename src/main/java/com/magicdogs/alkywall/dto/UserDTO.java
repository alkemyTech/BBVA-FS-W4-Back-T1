package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.enums.DocumentType;
import com.magicdogs.alkywall.enums.UserGender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Schema(description = "DTO para un usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @Schema(description = "Nombre del usuario")
    private String firstName;

    @Schema(description = "Apellido del usuario")
    private String lastName;

    @Schema(description = "Fecha de nacimiento del usuario")
    private LocalDate birthDate;

    @Schema(description = "Género del usuario")
    private UserGender gender;

    @Schema(description = "Tipo de documento del usuario")
    private DocumentType documentType;

    @Schema(description = "Número de documento del usuario")
    private String documentNumber;

    @Schema(description = "Correo electrónico del usuario")
    private String email;
}