package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.entities.TypeTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionBalanceDTO {

    private Long idTransaction;
    private Double amount;
    private TypeTransaction type;
    private String description;
    private LocalDateTime transactionDate;
}
