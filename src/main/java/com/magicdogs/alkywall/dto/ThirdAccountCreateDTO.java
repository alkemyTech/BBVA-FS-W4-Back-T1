package com.magicdogs.alkywall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThirdAccountCreateDTO {
    private String nickname;

    @NotNull(message = "El ID de la cuenta destino no puede estar vacío")
    private Long idDestinationAccount;
}
