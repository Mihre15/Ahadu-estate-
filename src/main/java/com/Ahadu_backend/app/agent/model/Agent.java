package com.Ahadu_backend.app.agent.model;

import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.listing.model.Listing;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "agents")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String phone;

    private String agencyName;

    private String licenseNumber;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "agent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Listing> listings = new ArrayList<>();
}