package com.Ahadu_backend.app.listing.controller;

import com.Ahadu_backend.app.listing.dto.ListingRequestDto;
import com.Ahadu_backend.app.listing.dto.ListingResponseDto;
import com.Ahadu_backend.app.listing.service.ListingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListingControllerTest {

    @Mock
    private ListingService listingService;

    private ListingController controller;

    @BeforeEach
    void setUp() {
        controller = new ListingController(listingService);
    }

    @Test
    void createListingReturnsCreatedListingWithCreatedStatus() {
        ListingRequestDto request = new ListingRequestDto();
        ListingResponseDto responseDto = response(1L, "New listing");
        when(listingService.createLising(request)).thenReturn(responseDto);

        var response = controller.createListing(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(responseDto);
    }

    @Test
    void getAllListingsReturnsListingsFromService() {
        ListingResponseDto listing = response(1L, "Listing");
        when(listingService.getAllListings()).thenReturn(List.of(listing));

        var response = controller.getAllListings();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(listing);
    }

    @Test
    void updateListingReturnsUpdatedListing() {
        ListingRequestDto request = new ListingRequestDto();
        ListingResponseDto updated = response(4L, "Updated listing");
        when(listingService.updateListing(4L, request)).thenReturn(updated);

        var response = controller.updateListing(4L, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(updated);
    }

    @Test
    void deleteListingReturnsNoContent() {
        var response = controller.deleteListing(5L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(listingService).deleteListing(5L);
    }

    @Test
    void getListingsByAgentReturnsServiceResults() {
        ListingResponseDto listing = response(7L, "Agent listing");
        when(listingService.getListingsByAgent(3L)).thenReturn(List.of(listing));

        var response = controller.getListingsByAgent(3L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(listing);
    }

    private ListingResponseDto response(Long id, String title) {
        ListingResponseDto dto = new ListingResponseDto();
        dto.setId(id);
        dto.setTitle(title);
        return dto;
    }
}
