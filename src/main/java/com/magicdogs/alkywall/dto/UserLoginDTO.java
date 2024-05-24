package com.magicdogs.alkywall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {


    private String fistName;
    private String lastName;
    private String email;
    private String password;
    private Boolean autenticado;
}
