package com.opportunityboard.service.organization.impl;

import com.opportunityboard.common.enums.VerificationStatus;
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
import java.time.Instant;
import java.util.Objects;

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
        String previousWebsiteUrl = profile.getWebsiteUrl();
        if (request.organizationName() != null && !request.organizationName().isBlank()) {
            profile.setOrganizationName(request.organizationName().trim());
        }
        profile.setIndustry(trimToNull(request.industry()));
        profile.setWebsiteUrl(trimToNull(request.websiteUrl()));
        profile.setDescription(trimToNull(request.description()));

        if (profile.getVerificationStatus() == VerificationStatus.VERIFIED
                && !Objects.equals(previousWebsiteUrl, profile.getWebsiteUrl())) {
            profile.setVerificationStatus(VerificationStatus.PENDING);
            profile.setVerificationNote(null);
            profile.setVerificationRequestedAt(Instant.now());
            profile.setVerifiedAt(null);
            profile.setVerifiedBy(null);
        }

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

    @Override
    @Transactional
    public OrganizationProfileResponse requestVerification(CustomUserDetails currentUser) {
        OrganizationProfile profile = findMyProfile(currentUser);
        if (profile.getVerificationStatus() == VerificationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Verification request is already pending");
        }
        if (profile.getVerificationStatus() == VerificationStatus.VERIFIED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Organization is already verified");
        }
        validateVerificationProfile(profile);
        profile.setVerificationStatus(VerificationStatus.PENDING);
        profile.setVerificationNote(null);
        profile.setVerificationRequestedAt(Instant.now());
        profile.setVerifiedAt(null);
        profile.setVerifiedBy(null);
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
                profile.getVerificationStatus(),
                profile.getVerificationNote(),
                profile.getVerificationRequestedAt(),
                profile.getVerifiedAt()
        );
    }

    private void validateVerificationProfile(OrganizationProfile profile) {
        if (profile.getUser().getEmailVerifiedAt() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account email must be verified first");
        }
        if (isBlank(profile.getOrganizationName()) || isBlank(profile.getIndustry())
                || isBlank(profile.getWebsiteUrl()) || isBlank(profile.getLogoUrl())
                || isBlank(profile.getDescription())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Organization name, industry, website, logo, and description are required"
            );
        }
        try {
            java.net.URI website = java.net.URI.create(profile.getWebsiteUrl());
            if (website.getHost() == null || !("http".equalsIgnoreCase(website.getScheme())
                    || "https".equalsIgnoreCase(website.getScheme()))) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organization website is invalid");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
