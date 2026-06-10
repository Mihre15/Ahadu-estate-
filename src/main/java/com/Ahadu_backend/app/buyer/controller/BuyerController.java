package com.Ahadu_backend.app.buyer.controller;

import com.Ahadu_backend.app.buyer.dto.BuyerRequestDto;
import com.Ahadu_backend.app.buyer.dto.BuyerResponseDto;
import com.Ahadu_backend.app.buyer.service.BuyerService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buyer")
@RequiredArgsConstructor
public class BuyerController {
    private final BuyerService buyerService;

    @PostMapping
    public ResponseEntity<BuyerResponseDto> createBuyer(
            @RequestBody BuyerRequestDto dto) {
        BuyerResponseDto createdBuyer = buyerService.createBuyer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBuyer);
    }

    @GetMapping
    public ResponseEntity<List<BuyerResponseDto>> getAllBuyers() {
        return ResponseEntity.ok(buyerService.getAllBuyers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuyerResponseDto> getBuyerById(
            @PathVariable Long id) {
        return ResponseEntity.ok(buyerService.getBuyerById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BuyerResponseDto> getBuyerByUserId(
            @PathVariable Long userId) {
        return ResponseEntity.ok(buyerService.getBuyerByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuyerResponseDto> updateBuyer(
            @PathVariable Long id,
            @RequestBody BuyerRequestDto dto) {
        return ResponseEntity.ok(buyerService.updateBuyer(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuyer(
            @PathVariable Long id) {
        buyerService.deleteBuyer(id);
        return ResponseEntity.noContent().build();
    }

}
