package com.Ahadu_backend.app.listing.service;

import java.util.List;

import com.Ahadu_backend.app.listing.dto.ListingRequestDto;
import com.Ahadu_backend.app.listing.dto.ListingResponseDto;

public interface IListingService {
    ListingResponseDto createLising(ListingRequestDto dto);

    List<ListingResponseDto> getAllListings();

    ListingResponseDto getListingById(Long id);

    ListingResponseDto updateListing(Long id, ListingRequestDto dto);

    void deleteListing(Long id);

    List<ListingResponseDto> getListingsByCity(String city);

    List<ListingResponseDto> getListingsByAgent(Long agentId);
}
