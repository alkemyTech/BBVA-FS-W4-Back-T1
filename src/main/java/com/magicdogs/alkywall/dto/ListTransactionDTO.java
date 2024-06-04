package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.enums.TransactionConcept;
import com.magicdogs.alkywall.enums.TypeTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListTransactionDTO {

    private Long idTransaction;
    private Double amount;
    private TypeTransaction type;
    private TransactionConcept concept;
    private String description;
    private Long accountIdAccount;
    private LocalDateTime transactionDate;
}
