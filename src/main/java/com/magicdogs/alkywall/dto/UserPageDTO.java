package com.magicdogs.alkywall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPageDTO {

    private List<UserDto> users;
    private String nextPage;
    private String prevPage;
    private Integer totalPages;
}
