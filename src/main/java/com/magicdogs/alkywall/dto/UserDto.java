package com.magicdogs.alkywall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    // private Long id;
    private String firstName;
    private String lastName;
    private Integer softDelete;
    private String email;
}