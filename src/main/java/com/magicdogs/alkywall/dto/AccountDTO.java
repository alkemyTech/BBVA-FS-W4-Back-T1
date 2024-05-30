package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    @Schema(description = "ID de la cuenta")
    private Long id;

    @Schema(description = "CBU de la cuenta")
    private String cbu;
}
