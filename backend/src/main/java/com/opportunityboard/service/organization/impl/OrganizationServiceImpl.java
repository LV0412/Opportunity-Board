package com.opportunityboard.service.organization.impl;

import com.opportunityboard.dto.request.organization.UpdateOrganizationProfileRequest;
import com.opportunityboard.dto.response.organization.OrganizationProfileResponse;
import com.opportunityboard.entity.OrganizationProfile;
import com.opportunityboard.infrastructure.storage.StorageService;
import com.opportunityboard.repository.OrganizationProfileRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.organization.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private static final long MAX_LOGO_SIZE_BYTES = 2 * 1024 * 1024;
    private static final Set<String> ALLOWED_LOGO_TYPES = Set.of("image/png", "image/jpeg", "image/webp");

    private final OrganizationProfileRepository organizationProfileRepository;
    private final StorageService storageService;

    public OrganizationServiceImpl(
            OrganizationProfileRepository organizationProfileRepository,
            StorageService storageService
    ) {
        this.organizationProfileRepository = organizationProfileRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationProfileResponse getMyProfile(CustomUserDetails currentUser) {
        return toResponse(findMyProfile(currentUser));
    }

    @Override
    @Transactional
    public OrganizationProfileResponse updateMyProfile(CustomUserDetails currentUser, UpdateOrganizationProfileRequest request) {
        OrganizationProfile profile = findMyProfile(currentUser);
        if (request.organizationName() != null && !request.organizationName().isBlank()) {
            profile.setOrganizationName(request.organizationName().trim());
        }
        profile.setIndustry(trimToNull(request.industry()));
        profile.setWebsiteUrl(trimToNull(request.websiteUrl()));
        profile.setDescription(trimToNull(request.description()));

        return toResponse(organizationProfileRepository.save(profile));
    }

    @Override
    @Transactional
    public OrganizationProfileResponse uploadLogo(CustomUserDetails currentUser, MultipartFile file) {
        validateLogo(file);
        OrganizationProfile profile = findMyProfile(currentUser);
        profile.setLogoUrl(storageService.uploadLogo(file));
        return toResponse(organizationProfileRepository.save(profile));
    }

    private OrganizationProfile findMyProfile(CustomUserDetails currentUser) {
        return organizationProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization profile not found"));
    }

    private void validateLogo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Logo file is required");
        }
        if (file.getSize() > MAX_LOGO_SIZE_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Logo file must be 2MB or smaller");
        }
        if (!ALLOWED_LOGO_TYPES.contains(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Logo must be PNG, JPG, or WebP");
        }
    }

    private OrganizationProfileResponse toResponse(OrganizationProfile profile) {
        return new OrganizationProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getUser().getEmail(),
                profile.getUser().getFullName(),
                profile.getOrganizationName(),
                profile.getIndustry(),
                profile.getWebsiteUrl(),
                profile.getLogoUrl(),
                profile.getDescription(),
                profile.isVerified()
        );
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
