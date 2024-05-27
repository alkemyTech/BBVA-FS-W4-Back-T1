package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.entities.FixedTermDeposit;
import com.magicdogs.alkywall.entities.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDTO {

    private Double accountArs;
    private Double accountUsd;
    List<Transaction> history;
    List<FixedTermDeposit> fixedTerms;
}
