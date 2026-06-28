package com.Ahadu_backend.app.core.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private final Path uploadRoot = Paths.get("uploads").toAbsolutePath().normalize();

    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new RuntimeException("Please upload a valid image file: JPG, PNG, WEBP, or GIF.");
        }

        String extension = getExtension(file.getOriginalFilename(), contentType);
        String fileName = UUID.randomUUID() + extension;
        Path destination = uploadRoot.resolve(fileName).normalize();

        if (!destination.startsWith(uploadRoot)) {
            throw new RuntimeException("Invalid upload path");
        }

        try {
            Files.createDirectories(uploadRoot);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not save uploaded image", ex);
        }
    }

    private String getExtension(String originalFilename, String contentType) {
        if (originalFilename != null) {
            String cleanName = Paths.get(originalFilename).getFileName().toString();
            int dotIndex = cleanName.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < cleanName.length() - 1) {
                String extension = cleanName.substring(dotIndex).toLowerCase(Locale.ROOT);
                if (Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif").contains(extension)) {
                    return extension;
                }
            }
        }

        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> ".jpg";
        };
    }
}
