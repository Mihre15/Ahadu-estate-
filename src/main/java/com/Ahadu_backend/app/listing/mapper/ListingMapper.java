package com.Ahadu_backend.app.listing.mapper;

import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.listing.dto.ListingRequestDto;
import com.Ahadu_backend.app.listing.dto.ListingResponseDto;
import com.Ahadu_backend.app.listing.model.Listing;
import org.springframework.stereotype.Component;

@Component
public class ListingMapper {
    public Listing toEntity(ListingRequestDto dto, Agent agent) {
        Listing listing = new Listing();
        updateEntity(listing, dto, agent);
        return listing;
    }

    public ListingResponseDto toResponseDto(Listing listing) {
        ListingResponseDto dto = new ListingResponseDto();

        dto.setId(listing.getId());
        dto.setTitle(listing.getTitle());
        dto.setDescription(listing.getDescription());
        dto.setAddress(listing.getAddress());
        dto.setCity(listing.getCity());
        dto.setPrice(listing.getPrice());
        dto.setBedrooms(listing.getBedrooms());
        dto.setBathrooms(listing.getBathrooms());
        dto.setArea(listing.getArea());
        dto.setImage(listing.getImage());
        dto.setPropertyType(listing.getPropertyType());
        dto.setListingStatus(listing.getListingStatus());

        if (listing.getAgent() != null) {
            dto.setAgentId(listing.getAgent().getId());
            dto.setAgentName(listing.getAgent().getName());
            dto.setAgentPhone(listing.getAgent().getPhone());
        }

        return dto;
    }

    public void updateEntity(Listing listing, ListingRequestDto dto, Agent agent) {
        listing.setTitle(dto.getTitle());
        listing.setDescription(dto.getDescription());
        listing.setAddress(dto.getAddress());
        listing.setCity(dto.getCity());
        listing.setPrice(dto.getPrice());
        listing.setBedrooms(dto.getBedrooms());
        listing.setBathrooms(dto.getBathrooms());
        listing.setArea(dto.getArea());
        listing.setImage(dto.getImage());
        listing.setPropertyType(dto.getPropertyType());
        listing.setListingStatus(dto.getListingStatus());
        listing.setAgent(agent);
    }
}
