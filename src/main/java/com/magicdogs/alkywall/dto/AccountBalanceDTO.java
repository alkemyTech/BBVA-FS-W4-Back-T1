package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.entities.FixedTermDeposit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDTO {

    @Schema(description = "Saldo de la cuenta en ARS")
    private Double accountArs;

    @Schema(description = "Saldo de la cuenta en USD")
    private Double accountUsd;

    @Schema(description = "Historial de transacciones de la cuenta")
    private List<TransactionBalanceDTO> history;

    @Schema(description = "Depósitos a plazo fijo de la cuenta")
    private List<FixedTermsBalanceDTO> fixedTerms;
}
