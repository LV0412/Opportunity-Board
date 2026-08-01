package com.opportunityboard.infrastructure.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Service
public class CloudinaryStorageService implements StorageService {
    private final Cloudinary cloudinary;
    private final boolean configured;

    public CloudinaryStorageService(
            @Value("${app.cloudinary.cloud-name:}") String cloudName,
            @Value("${app.cloudinary.api-key:}") String apiKey,
            @Value("${app.cloudinary.api-secret:}") String apiSecret
    ) {
        this.configured = !cloudName.isBlank() && !apiKey.isBlank() && !apiSecret.isBlank();
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    @Override
    public String uploadResume(MultipartFile file) {
        return upload(file, "opportunity-board/resumes", "raw");
    }

    @Override
    public String uploadLogo(MultipartFile file) {
        return upload(file, "opportunity-board/logos", "image");
    }

    private String upload(MultipartFile file, String folder, String resourceType) {
        if (!configured) {
            throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Cloudinary storage is not configured");
        }

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", resourceType,
                    "use_filename", true,
                    "unique_filename", true
            ));
            Object secureUrl = result.get("secure_url");
            if (secureUrl == null) {
                throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Cloudinary upload did not return a URL");
            }
            return secureUrl.toString();
        } catch (IOException exception) {
            throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Could not read uploaded file", exception);
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(SERVICE_UNAVAILABLE, "Could not upload file to Cloudinary", exception);
        }
    }
}
