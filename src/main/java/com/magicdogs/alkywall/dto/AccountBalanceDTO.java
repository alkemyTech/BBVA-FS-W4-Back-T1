package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.entities.Account;
import com.magicdogs.alkywall.entities.FixedTermDeposit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDTO {

    @Schema(description = "Saldo de la cuenta en ARS")
    private List<AccountDTO> accountArs = new ArrayList<>();

    @Schema(description = "Saldo de la cuenta en USD")
    private AccountDTO accountUsd;

    @Schema(description = "Historial de transacciones de la cuenta")
    private List<TransactionDTO> history = new ArrayList<>();

    @Schema(description = "Depósitos a plazo fijo de la cuenta")
    private List<FixedTermsBalanceDTO> fixedTerms = new ArrayList<>();

    public void addHistory(List<TransactionDTO> transactionDTO) {
        history.addAll(transactionDTO);
    }

    public void addFixedTerms(List<FixedTermsBalanceDTO> fixedTermsBalanceDTO) {
        fixedTerms.addAll(fixedTermsBalanceDTO);
    }
}
