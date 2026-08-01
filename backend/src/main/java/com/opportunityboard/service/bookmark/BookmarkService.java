package com.opportunityboard.service.bookmark;

import com.opportunityboard.dto.response.bookmark.BookmarkResponse;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BookmarkService {
    OpportunityResponse save(CustomUserDetails currentUser, UUID opportunityId);

    OpportunityResponse unsave(CustomUserDetails currentUser, UUID opportunityId);

    Page<BookmarkResponse> listMine(CustomUserDetails currentUser, String sort, Pageable pageable);
}
