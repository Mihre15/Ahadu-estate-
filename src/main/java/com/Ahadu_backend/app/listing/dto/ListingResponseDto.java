package com.Ahadu_backend.app.listing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingResponseDto {
    private Long id;
    private String title;
    private String description;
    private String address;
    private String city;
    private Double price;
    private Integer bedrooms;
    private Integer bathrooms;
    private Double area;
    private String image;
    private String propertyType;
    private String listingStatus;

    private Long agentId;
    private String agentName;
    private String agentPhone;
}
