package com.Ahadu_backend.app.listing.search;

import com.Ahadu_backend.app.listing.model.Listing;
import com.Ahadu_backend.app.repository.ListingRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListingSearchServiceTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private ListingRepository listingRepository;

    private ListingSearchService listingSearchService;

    @BeforeEach
    void setUp() {
        listingSearchService = new ListingSearchService(elasticsearchOperations, listingRepository);
    }

    @Test
    void searchListingsFallsBackToDatabaseWhenElasticsearchIsUnavailable() {
        Listing matching = listing(2L, "Bole Apartment", "Addis Ababa", "Apartment");
        Listing wrongCity = listing(1L, "Summit Villa", "Addis Ababa", "Villa");
        when(elasticsearchOperations.search(any(Query.class), eq(com.Ahadu_backend.app.listing.model.ListingDocument.class))).thenThrow(new RuntimeException("Elasticsearch unavailable"));
        when(listingRepository.findAll()).thenReturn(List.of(wrongCity, matching));

        List<Listing> results = listingSearchService.searchListings("Bole", "Apartment");

        assertThat(results).containsExactly(matching);
    }

    @Test
    void searchListingsReturnsAllListingsWhenSearchIsEmpty() {
        Listing listing = listing(1L, "Family Home", "Adama", "Residential");
        when(listingRepository.findAll()).thenReturn(List.of(listing));

        List<Listing> results = listingSearchService.searchListings(" ", "Property Type");

        assertThat(results).containsExactly(listing);
    }

    private Listing listing(Long id, String title, String city, String propertyType) {
        Listing listing = new Listing();
        listing.setId(id);
        listing.setTitle(title);
        listing.setDescription(title + " description");
        listing.setAddress("Main Road");
        listing.setCity(city);
        listing.setPropertyType(propertyType);
        return listing;
    }
}


