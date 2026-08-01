package com.opportunityboard.service.bookmark.impl;

import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.dto.response.bookmark.BookmarkResponse;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.entity.Bookmark;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.StudentProfile;
import com.opportunityboard.repository.BookmarkRepository;
import com.opportunityboard.repository.OpportunityRepository;
import com.opportunityboard.repository.StudentProfileRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.bookmark.BookmarkService;
import com.opportunityboard.service.opportunity.impl.OpportunityMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
public class BookmarkServiceImpl implements BookmarkService {
    private static final int MAX_PAGE_SIZE = 50;

    private final BookmarkRepository bookmarkRepository;
    private final OpportunityRepository opportunityRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final OpportunityMapper opportunityMapper;

    public BookmarkServiceImpl(
            BookmarkRepository bookmarkRepository,
            OpportunityRepository opportunityRepository,
            StudentProfileRepository studentProfileRepository,
            OpportunityMapper opportunityMapper
    ) {
        this.bookmarkRepository = bookmarkRepository;
        this.opportunityRepository = opportunityRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.opportunityMapper = opportunityMapper;
    }

    @Override
    @Transactional
    public OpportunityResponse save(CustomUserDetails currentUser, UUID opportunityId) {
        StudentProfile student = findStudent(currentUser);
        Opportunity opportunity = findBookmarkableOpportunity(opportunityId);
        bookmarkRepository.findByStudentIdAndOpportunityId(student.getId(), opportunity.getId())
                .orElseGet(() -> {
                    Bookmark bookmark = new Bookmark();
                    bookmark.setStudent(student);
                    bookmark.setOpportunity(opportunity);
                    return bookmarkRepository.save(bookmark);
                });
        return opportunityMapper.toResponse(opportunity);
    }

    @Override
    @Transactional
    public OpportunityResponse unsave(CustomUserDetails currentUser, UUID opportunityId) {
        StudentProfile student = findStudent(currentUser);
        Opportunity opportunity = findBookmarkableOpportunity(opportunityId);
        bookmarkRepository.findByStudentIdAndOpportunityId(student.getId(), opportunity.getId())
                .ifPresent(bookmarkRepository::delete);
        return opportunityMapper.toResponse(opportunity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookmarkResponse> listMine(CustomUserDetails currentUser, String sort, Pageable pageable) {
        StudentProfile student = findStudent(currentUser);
        Pageable effectivePageable = normalizePageable(pageable);
        Page<Bookmark> bookmarks = "deadline".equalsIgnoreCase(sort)
                ? bookmarkRepository.findByStudentIdOrderByOpportunityDeadline(student.getId(), effectivePageable)
                : bookmarkRepository.findByStudentId(student.getId(), effectivePageable);

        return bookmarks.map(bookmark -> new BookmarkResponse(
                bookmark.getId(),
                opportunityMapper.toResponse(bookmark.getOpportunity()),
                bookmark.getCreatedAt()
        ));
    }

    private StudentProfile findStudent(CustomUserDetails currentUser) {
        return studentProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student profile not found"));
    }

    private Opportunity findBookmarkableOpportunity(UUID opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found"));
        if (opportunity.getStatus() != OpportunityStatus.APPROVED || isExpired(opportunity)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found");
        }
        return opportunity;
    }

    private boolean isExpired(Opportunity opportunity) {
        return opportunity.getDeadlineAt() != null && opportunity.getDeadlineAt().isBefore(Instant.now());
    }

    private Pageable normalizePageable(Pageable pageable) {
        int page = Math.max(pageable.getPageNumber(), 0);
        int size = pageable.getPageSize() <= 0 ? 12 : Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
        return PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
    }
}
