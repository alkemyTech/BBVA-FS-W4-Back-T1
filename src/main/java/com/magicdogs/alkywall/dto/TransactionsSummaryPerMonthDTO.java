package com.magicdogs.alkywall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsSummaryPerMonthDTO {
    String month;
    Double deposit;
    Double payment;
    Double income;

}
