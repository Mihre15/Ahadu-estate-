package com.Ahadu_backend.app.buyer.mapper;

import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.buyer.dto.BuyerRequestDto;
import com.Ahadu_backend.app.buyer.dto.BuyerResponseDto;
import com.Ahadu_backend.app.buyer.model.Buyer;
import org.springframework.stereotype.Component;

@Component
public class BuyerMapper {
    public Buyer toEntity(BuyerRequestDto dto, User user) {
        Buyer buyer = new Buyer();

        buyer.setName(dto.getName());
        buyer.setPhone(dto.getPhone());
        buyer.setPreferredCity(dto.getPreferredCity());
        buyer.setBudgetMin(dto.getBudgetMin());
        buyer.setBudgetMax(dto.getBudgetMax());
        buyer.setUser(user);

        return buyer;
    }

    public BuyerResponseDto toResponseDto(Buyer buyer) {
        BuyerResponseDto dto = new BuyerResponseDto();

        dto.setId(buyer.getId());
        dto.setName(buyer.getName());
        dto.setPhone(buyer.getPhone());
        dto.setPreferredCity(buyer.getPreferredCity());
        dto.setBudgetMin(buyer.getBudgetMin());
        dto.setBudgetMax(buyer.getBudgetMax());

        if (buyer.getUser() != null) {
            dto.setUserId(buyer.getUser().getId());
            dto.setEmail(buyer.getUser().getEmail());
        }

        return dto;
    }

    public void updateEntity(Buyer buyer, BuyerRequestDto dto, User user) {
        buyer.setName(dto.getName());
        buyer.setPhone(dto.getPhone());
        buyer.setPreferredCity(dto.getPreferredCity());
        buyer.setBudgetMin(dto.getBudgetMin());
        buyer.setBudgetMax(dto.getBudgetMax());
        buyer.setUser(user);
    }
}
