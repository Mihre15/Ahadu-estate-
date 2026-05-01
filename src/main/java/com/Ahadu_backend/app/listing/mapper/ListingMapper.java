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

        listing.setTitle(dto.getTitle());
        listing.setDescription(dto.getDescription());
        listing.setAddress(dto.getAddress());
        listing.setCity(dto.getCity());
        listing.setPrice(dto.getPrice());
        listing.setBedrooms(dto.getBedrooms());
        listing.setBathrooms(dto.getBathrooms());
        listing.setArea(dto.getArea());
        listing.setAgent(agent);

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
        listing.setAgent(agent);
    }
}
