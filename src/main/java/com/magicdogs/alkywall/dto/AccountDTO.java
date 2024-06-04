package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.enums.AccountType;
import com.magicdogs.alkywall.enums.CurrencyType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    @Schema(description = "ID de la cuenta")
    private Long idAccount;

    @Schema(description = "Tipo de cuenta")
    private AccountType accountType;
  
    @Schema(description = "Tipo de moneda de la cuenta")
    private CurrencyType currency;

    @Schema(description = "CBU de la cuenta")
    private String cbu;

    @Schema(description = "Alias de la cuenta")
    private String alias;

    @Schema(description = "Límite de transacción de la cuenta")
    private Double transactionLimit;

    @Schema(description = "Balance de la cuenta")
    private Double balance;
}