package com.Ahadu_backend.app.listing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "listings")
public class ListingDocument {

    @Id
    private Long id;

    private String title;
    private String description;
    private String address;
    private String city;

    private Double price;
    private Integer bedrooms;
    private String image;
    private Integer bathrooms;
    private Double area;

    private Long agentId;
}
