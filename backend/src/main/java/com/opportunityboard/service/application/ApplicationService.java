package com.opportunityboard.service.application;

import com.opportunityboard.dto.request.application.CreateApplicationRequest;
import com.opportunityboard.dto.request.application.UpdateApplicationStatusRequest;
import com.opportunityboard.dto.response.application.ApplicationResponse;
import com.opportunityboard.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ApplicationService {
    ApplicationResponse apply(CustomUserDetails currentUser, UUID opportunityId, CreateApplicationRequest request);

    Page<ApplicationResponse> listMine(CustomUserDetails currentUser, Pageable pageable);

    ApplicationResponse getById(CustomUserDetails currentUser, UUID applicationId);

    ApplicationResponse updateStatus(CustomUserDetails currentUser, UUID applicationId, UpdateApplicationStatusRequest request);

    Page<ApplicationResponse> listOrganizationApplications(CustomUserDetails currentUser, Pageable pageable);
}
