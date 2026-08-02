package com.opportunityboard.service.organization;

import com.opportunityboard.dto.request.organization.UpdateOrganizationProfileRequest;
import com.opportunityboard.dto.response.organization.OrganizationProfileResponse;
import com.opportunityboard.security.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface OrganizationService {
    OrganizationProfileResponse getMyProfile(CustomUserDetails currentUser);

    OrganizationProfileResponse updateMyProfile(CustomUserDetails currentUser, UpdateOrganizationProfileRequest request);

    OrganizationProfileResponse uploadLogo(CustomUserDetails currentUser, MultipartFile file);

    OrganizationProfileResponse requestVerification(CustomUserDetails currentUser);
}
