package com.magicdogs.alkywall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FixedTermPageDTO {
    private List<?> fixedTerms;
    private String nextPage;
    private String prevPage;
    private int countPages;
}
