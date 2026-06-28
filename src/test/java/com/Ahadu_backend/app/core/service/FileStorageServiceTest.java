package com.Ahadu_backend.app.core.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    private final FileStorageService fileStorageService = new FileStorageService();

    @Test
    void storeImageReturnsNullForMissingUpload() {
        assertThat(fileStorageService.storeImage(null)).isNull();
    }

    @Test
    void storeImageReturnsNullForEmptyUpload() {
        MockMultipartFile emptyFile = new MockMultipartFile("imageFile", "", "image/png", new byte[0]);

        assertThat(fileStorageService.storeImage(emptyFile)).isNull();
    }

    @Test
    void storeImageRejectsNonImageUploads() {
        MockMultipartFile textFile = new MockMultipartFile(
                "imageFile",
                "listing.txt",
                "text/plain",
                "https://example.com/home.jpg".getBytes()
        );

        assertThatThrownBy(() -> fileStorageService.storeImage(textFile))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Please upload a valid image file: JPG, PNG, WEBP, or GIF.");
    }
}
