package com.opportunityboard.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadResume(MultipartFile file);

    String uploadLogo(MultipartFile file);
}
