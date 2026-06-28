package com.Ahadu_backend.app.buyer.model;

import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.listing.model.Listing;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "buyer")
public class Buyer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phone;

    private String preferredCity;

    private Double budgetMin;

    private Double budgetMax;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "buyer_requested_listings",
            joinColumns = @JoinColumn(name = "buyer_id"),
            inverseJoinColumns = @JoinColumn(name = "listing_id")
    )
    private Set<Listing> requestedListings = new HashSet<>();
}
