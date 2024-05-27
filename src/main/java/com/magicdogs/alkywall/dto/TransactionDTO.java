package com.magicdogs.alkywall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    @NotNull
    @NotBlank
    @NotEmpty
    private int destinationIdAccount;

    @NotNull
    @NotBlank
    @Positive
    @NotEmpty
    private double amount;

    private String description;

}
