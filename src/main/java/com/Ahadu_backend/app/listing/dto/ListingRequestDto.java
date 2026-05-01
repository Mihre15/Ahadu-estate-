package com.Ahadu_backend.app.listing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingRequestDto {
    private String title;
    private String description;
    private String address;
    private String city;
    private Double price;
    private Integer bedrooms;
    private Integer bathrooms;
    private Double area;
    private Long agentId;
}
