package com.magicdogs.alkywall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThirdAccountDTO {

    @NotEmpty(message = "El nickname no puede estar vacío")
    @NotNull(message = "El nickname no puede ser nulo")
    @NotBlank(message = "El nickname no puede estar en blanco")
    private String nickname;

    @NotEmpty(message = "El cbu no puede estar vacío")
    @NotNull(message = "El cbu no puede ser nulo")
    @NotBlank(message = "El cbu no puede estar en blanco")
    private String cbu;

}
