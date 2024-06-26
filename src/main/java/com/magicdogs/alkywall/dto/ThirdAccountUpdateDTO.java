package com.magicdogs.alkywall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThirdAccountUpdateDTO {

    @NotNull(message = "ID de la cuenta es requerido")
    private Long idThirdAccount;

    @NotBlank(message = "El nuevo nickname es requerido")
    private String nickname;
}
