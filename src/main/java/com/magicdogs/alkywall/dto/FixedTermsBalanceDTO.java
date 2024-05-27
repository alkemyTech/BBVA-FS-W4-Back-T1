package com.magicdogs.alkywall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedTermsBalanceDTO {

    private Long idDeposit;
    private Double amount;
    private Double interest;
    private LocalDateTime creationDate;
}
