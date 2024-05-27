package com.magicdogs.alkywall.dto;

import com.magicdogs.alkywall.entities.CurrencyType;
import com.magicdogs.alkywall.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateDTO {

    @NotNull
    @NotBlank
    @NotEmpty
    private CurrencyType currency;

    @NotNull
    @NotBlank
    @NotEmpty
    private User user;
}
