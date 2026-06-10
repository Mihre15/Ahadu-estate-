package com.Ahadu_backend.app.buyer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.buyer.dto.BuyerRequestDto;
import com.Ahadu_backend.app.buyer.dto.BuyerResponseDto;
import com.Ahadu_backend.app.buyer.mapper.BuyerMapper;
import com.Ahadu_backend.app.buyer.model.Buyer;
import com.Ahadu_backend.app.repository.BuyerRepository;
import com.Ahadu_backend.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuyerService implements IBuyerService {
    private final BuyerRepository buyerRepository;
    private final UserRepository userRepository;
    private final BuyerMapper buyerMapper;

    @Override
    public BuyerResponseDto createBuyer(BuyerRequestDto dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Buyer buyer = buyerMapper.toEntity(dto, user);

        buyer.setUser(user);

        return buyerMapper.toResponseDto(
                buyerRepository.save(buyer));
    }

    @Override
    public List<BuyerResponseDto> getAllBuyers() {

        return buyerRepository.findAll()
                .stream()
                .map(buyerMapper::toResponseDto)
                .toList();
    }

    @Override
    public BuyerResponseDto getBuyerById(Long id) {

        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        return buyerMapper.toResponseDto(buyer);
    }

    @Override
    public BuyerResponseDto getBuyerByUserId(Long userId) {

        Buyer buyer = buyerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        return buyerMapper.toResponseDto(buyer);
    }

    @Override
    public BuyerResponseDto updateBuyer(Long id, BuyerRequestDto dto) {

        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        buyer.setName(dto.getName());
        buyer.setPhone(dto.getPhone());
        buyer.setPreferredCity(dto.getPreferredCity());
        buyer.setBudgetMin(dto.getBudgetMin());
        buyer.setBudgetMax(dto.getBudgetMax());

        return buyerMapper.toResponseDto(
                buyerRepository.save(buyer));
    }

    @Override
    public void deleteBuyer(Long id) {

        buyerRepository.deleteById(id);
    }
}
