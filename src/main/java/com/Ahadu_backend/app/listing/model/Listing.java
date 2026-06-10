package com.Ahadu_backend.app.listing.model;

import com.Ahadu_backend.app.agent.model.Agent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "listing")
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(length = 2000)
    private String description;
    private String address;
    private String city;
    private Double price;
    private Integer bedrooms;
    private String image;
    private Integer bathrooms;
    private Double area;
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;
}
