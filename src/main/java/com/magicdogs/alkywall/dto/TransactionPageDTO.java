package com.magicdogs.alkywall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionPageDTO {

    private List<TransactionDTO> transactios;
    private String nextPage;
    private String prevPage;
    private int countPages;
    private Double maxAmmout;
}
