package com.opportunityboard.controller;

import com.opportunityboard.common.dto.PageResponse;
import com.opportunityboard.dto.response.bookmark.BookmarkResponse;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.bookmark.BookmarkService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping("/opportunities/{id}/bookmark")
    @PreAuthorize("hasRole('STUDENT')")
    public OpportunityResponse save(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id
    ) {
        return bookmarkService.save(currentUser, id);
    }

    @DeleteMapping("/opportunities/{id}/bookmark")
    @PreAuthorize("hasRole('STUDENT')")
    public OpportunityResponse unsave(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id
    ) {
        return bookmarkService.unsave(currentUser, id);
    }

    @GetMapping("/bookmarks/me")
    @PreAuthorize("hasRole('STUDENT')")
    public PageResponse<BookmarkResponse> listMine(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam(defaultValue = "deadline") String sort,
            Pageable pageable
    ) {
        return PageResponse.from(bookmarkService.listMine(currentUser, sort, pageable));
    }
}
