package com.Ahadu_backend.app.buyer.service;

import java.util.List;

import com.Ahadu_backend.app.buyer.dto.BuyerRequestDto;
import com.Ahadu_backend.app.buyer.dto.BuyerResponseDto;

public interface IBuyerService {
    BuyerResponseDto createBuyer(BuyerRequestDto dto);

    List<BuyerResponseDto> getAllBuyers();

    BuyerResponseDto getBuyerById(Long id);

    BuyerResponseDto getBuyerByUserId(Long userId);

    BuyerResponseDto updateBuyer(Long id, BuyerRequestDto dto);

    void deleteBuyer(Long id);
}
