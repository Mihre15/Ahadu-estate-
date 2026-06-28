package com.Ahadu_backend.app.listing.search;

import com.Ahadu_backend.app.listing.model.Listing;
import com.Ahadu_backend.app.listing.model.ListingDocument;
import com.Ahadu_backend.app.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingSearchService {
    private final ElasticsearchOperations elasticsearchOperations;
    private final ListingRepository listingRepository;

    public List<Listing> searchListings(String query, String propertyType) {
        String cleanQuery = normalize(query);
        String cleanPropertyType = normalize(propertyType);

        if (cleanQuery == null && cleanPropertyType == null) {
            return listingRepository.findAll();
        }

        try {
            List<ListingDocument> documents = searchDocuments(cleanQuery);
            List<Long> orderedIds = documents.stream()
                    .filter(document -> matchesPropertyType(document.getPropertyType(), cleanPropertyType))
                    .map(ListingDocument::getId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            if (orderedIds.isEmpty()) {
                return List.of();
            }

            Map<Long, Listing> listingsById = listingRepository.findAllById(orderedIds)
                    .stream()
                    .collect(Collectors.toMap(Listing::getId, Function.identity()));

            return orderedIds.stream()
                    .map(listingsById::get)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (RuntimeException ex) {
            return searchListingsFromDatabase(cleanQuery, cleanPropertyType);
        }
    }

    public void indexListing(Listing listing) {
        if (listing == null || listing.getId() == null) {
            return;
        }

        try {
            elasticsearchOperations.save(toDocument(listing));
        } catch (RuntimeException ignored) {
            // PostgreSQL remains the source of truth if Elasticsearch is unavailable.
        }
    }

    public void deleteListing(Long listingId) {
        if (listingId == null) {
            return;
        }

        try {
            elasticsearchOperations.delete(String.valueOf(listingId), ListingDocument.class);
        } catch (RuntimeException ignored) {
            // Search index cleanup can be retried when Elasticsearch is available.
        }
    }

    public void reindexAllListings() {
        try {
            List<ListingDocument> documents = listingRepository.findAll()
                    .stream()
                    .map(this::toDocument)
                    .toList();
            elasticsearchOperations.save(documents);
        } catch (RuntimeException ignored) {
            // App startup should not fail just because the search service is down.
        }
    }

    private List<ListingDocument> searchDocuments(String cleanQuery) {
        Criteria criteria;
        if (cleanQuery == null) {
            criteria = new Criteria();
        } else {
            criteria = new Criteria("title").contains(cleanQuery)
                    .or(new Criteria("description").contains(cleanQuery))
                    .or(new Criteria("address").contains(cleanQuery))
                    .or(new Criteria("city").contains(cleanQuery))
                    .or(new Criteria("propertyType").contains(cleanQuery))
                    .or(new Criteria("listingStatus").contains(cleanQuery));
        }

        return elasticsearchOperations.search(new CriteriaQuery(criteria), ListingDocument.class)
                .getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }

    private List<Listing> searchListingsFromDatabase(String cleanQuery, String cleanPropertyType) {
        return listingRepository.findAll()
                .stream()
                .filter(listing -> matchesQuery(listing, cleanQuery))
                .filter(listing -> matchesPropertyType(listing.getPropertyType(), cleanPropertyType))
                .sorted(Comparator.comparing(Listing::getId, Comparator.nullsLast(Long::compareTo)).reversed())
                .toList();
    }

    private boolean matchesQuery(Listing listing, String cleanQuery) {
        if (cleanQuery == null) {
            return true;
        }

        Set<String> searchableFields = new HashSet<>();
        searchableFields.add(listing.getTitle());
        searchableFields.add(listing.getDescription());
        searchableFields.add(listing.getAddress());
        searchableFields.add(listing.getCity());
        searchableFields.add(listing.getPropertyType());
        searchableFields.add(listing.getListingStatus());

        return searchableFields.stream()
                .filter(Objects::nonNull)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.contains(cleanQuery));
    }

    private boolean matchesPropertyType(String value, String cleanPropertyType) {
        if (cleanPropertyType == null) {
            return true;
        }
        return value != null && value.toLowerCase(Locale.ROOT).contains(cleanPropertyType);
    }

    private ListingDocument toDocument(Listing listing) {
        return ListingDocument.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .address(listing.getAddress())
                .city(listing.getCity())
                .price(listing.getPrice())
                .bedrooms(listing.getBedrooms())
                .image(listing.getImage())
                .bathrooms(listing.getBathrooms())
                .area(listing.getArea())
                .propertyType(listing.getPropertyType())
                .listingStatus(listing.getListingStatus())
                .agentId(listing.getAgent() != null ? listing.getAgent().getId() : null)
                .build();
    }

    private String normalize(String value) {
        if (value == null || value.isBlank() || "Property Type".equalsIgnoreCase(value.trim())) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
