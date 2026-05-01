package com.Ahadu_backend.app.buyer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyerRequestDto {
    private String name;

    private String phone;

    private String preferredCity;

    private Double budgetMin;

    private Double budgetMax;

    private Long userId;
}
