package com.Ahadu_backend.app.listing.controller;

import com.Ahadu_backend.app.listing.dto.ListingRequestDto;
import com.Ahadu_backend.app.listing.dto.ListingResponseDto;
import com.Ahadu_backend.app.listing.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {
    private final ListingService listingService;

    @PostMapping
    public ResponseEntity<ListingResponseDto> createListing(
            @RequestBody ListingRequestDto dto
    ) {
        ListingResponseDto createdListing = listingService.createLising(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdListing);
    }
    @GetMapping
    public ResponseEntity<List<ListingResponseDto>> getAllListings() {
        return ResponseEntity.ok(listingService.getAllListings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponseDto> getListingById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(listingService.getListingById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListingResponseDto> updateListing(
            @PathVariable Long id,
            @RequestBody ListingRequestDto dto
    ) {
        return ResponseEntity.ok(listingService.updateListing(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long id
    ) {
        listingService.deleteListing(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<ListingResponseDto>> getListingsByCity(
            @PathVariable String city
    ) {
        return ResponseEntity.ok(listingService.getListingsByCity(city));
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<ListingResponseDto>> getListingsByAgent(
            @PathVariable Long agentId
    ) {
        return ResponseEntity.ok(listingService.getListingsByAgent(agentId));
    }
}
