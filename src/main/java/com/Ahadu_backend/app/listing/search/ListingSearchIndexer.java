package com.Ahadu_backend.app.listing.search;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListingSearchIndexer implements ApplicationRunner {
    private final ListingSearchService listingSearchService;

    @Override
    public void run(ApplicationArguments args) {
        listingSearchService.reindexAllListings();
    }
}
