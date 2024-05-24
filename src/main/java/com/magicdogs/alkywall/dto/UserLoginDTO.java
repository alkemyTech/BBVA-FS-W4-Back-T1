package com.magicdogs.alkywall.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {



    @NotEmpty
    @NotNull
    @NotBlank
    @Email
    private String email;
    @NotEmpty
    @NotNull
    @NotBlank
    private String password;

}
