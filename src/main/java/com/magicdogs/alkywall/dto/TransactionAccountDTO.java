package com.magicdogs.alkywall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionAccountDTO {

    @Schema(description = "Objeto transacción")
    private TransactionDTO transaction;
    @Schema(description = "Objeto cuenta")
    private AccountDTO account;
}
