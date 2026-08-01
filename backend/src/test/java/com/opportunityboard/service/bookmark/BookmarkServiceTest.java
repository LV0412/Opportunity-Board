package com.opportunityboard.service.bookmark;

import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.common.enums.UserStatus;
import com.opportunityboard.dto.response.bookmark.BookmarkResponse;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.entity.Bookmark;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.StudentProfile;
import com.opportunityboard.entity.User;
import com.opportunityboard.repository.BookmarkRepository;
import com.opportunityboard.repository.OpportunityRepository;
import com.opportunityboard.repository.StudentProfileRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.bookmark.impl.BookmarkServiceImpl;
import com.opportunityboard.service.opportunity.impl.OpportunityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {
    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private OpportunityMapper opportunityMapper;

    @InjectMocks
    private BookmarkServiceImpl bookmarkService;

    private CustomUserDetails studentUser;
    private StudentProfile studentProfile;
    private Opportunity opportunity;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("student@example.com");
        user.setPasswordHash("encoded");
        user.setFullName("Student");
        user.setRole(UserRole.STUDENT);
        user.setStatus(UserStatus.ACTIVE);
        studentUser = new CustomUserDetails(user);

        studentProfile = new StudentProfile();
        studentProfile.setId(UUID.randomUUID());
        studentProfile.setUser(user);

        opportunity = new Opportunity();
        opportunity.setId(UUID.randomUUID());
        opportunity.setTitle("Internship");
        opportunity.setStatus(OpportunityStatus.APPROVED);
        opportunity.setDeadlineAt(Instant.now().plusSeconds(86_400));
    }

    @Test
    void saveCreatesBookmarkOnlyOnce() {
        when(studentProfileRepository.findByUserId(studentUser.getId())).thenReturn(Optional.of(studentProfile));
        when(opportunityRepository.findById(opportunity.getId())).thenReturn(Optional.of(opportunity));
        when(bookmarkRepository.findByStudentIdAndOpportunityId(studentProfile.getId(), opportunity.getId()))
                .thenReturn(Optional.empty());
        when(bookmarkRepository.save(any(Bookmark.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(opportunityMapper.toResponse(opportunity)).thenReturn(dummyOpportunityResponse());

        bookmarkService.save(studentUser, opportunity.getId());

        ArgumentCaptor<Bookmark> captor = ArgumentCaptor.forClass(Bookmark.class);
        verify(bookmarkRepository).save(captor.capture());
        assertThat(captor.getValue().getStudent()).isEqualTo(studentProfile);
        assertThat(captor.getValue().getOpportunity()).isEqualTo(opportunity);
    }

    @Test
    void saveRejectsExpiredOpportunity() {
        opportunity.setDeadlineAt(Instant.now().minusSeconds(60));
        when(studentProfileRepository.findByUserId(studentUser.getId())).thenReturn(Optional.of(studentProfile));
        when(opportunityRepository.findById(opportunity.getId())).thenReturn(Optional.of(opportunity));

        assertThatThrownBy(() -> bookmarkService.save(studentUser, opportunity.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseStatusException = (ResponseStatusException) exception;
                    assertThat(responseStatusException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(responseStatusException.getReason()).isEqualTo("Opportunity not found");
                });
    }

    @Test
    void listMineUsesDeadlineSortingAndCapsPageSize() {
        when(studentProfileRepository.findByUserId(studentUser.getId())).thenReturn(Optional.of(studentProfile));
        Bookmark bookmark = new Bookmark();
        bookmark.setId(UUID.randomUUID());
        bookmark.setOpportunity(opportunity);
        bookmark.setCreatedAt(Instant.now());
        when(bookmarkRepository.findByStudentIdOrderByOpportunityDeadline(any(UUID.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(bookmark)));
        when(opportunityMapper.toResponse(opportunity)).thenReturn(dummyOpportunityResponse());

        var page = bookmarkService.listMine(studentUser, "deadline", PageRequest.of(0, 200));

        assertThat(page.getContent()).hasSize(1).extracting(BookmarkResponse::id).containsExactly(bookmark.getId());
        verify(bookmarkRepository).findByStudentIdOrderByOpportunityDeadline(any(UUID.class), any(PageRequest.class));
        verify(bookmarkRepository, never()).findByStudentId(any(UUID.class), any(PageRequest.class));
    }

    private OpportunityResponse dummyOpportunityResponse() {
        return new OpportunityResponse(
                opportunity.getId(),
                opportunity.getTitle(),
                "Description",
                null,
                null,
                false,
                null,
                opportunity.getDeadlineAt(),
                OpportunityStatus.APPROVED,
                "Internship",
                "internship",
                List.of(),
                UUID.randomUUID(),
                "Org",
                null,
                0,
                0,
                null,
                Instant.now(),
                Instant.now()
        );
    }
}
