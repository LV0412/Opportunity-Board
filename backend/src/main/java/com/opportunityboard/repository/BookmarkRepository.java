package com.opportunityboard.repository;

import com.opportunityboard.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    Page<Bookmark> findByStudentId(UUID studentId, Pageable pageable);

    long countByStudentId(UUID studentId);

    List<Bookmark> findAllByOpportunityId(UUID opportunityId);

    Optional<Bookmark> findByStudentIdAndOpportunityId(UUID studentId, UUID opportunityId);

    boolean existsByStudentIdAndOpportunityId(UUID studentId, UUID opportunityId);

    long countByOpportunityId(UUID opportunityId);

    @Query("select count(b) from Bookmark b where b.opportunity.organization.id = :organizationId")
    long countByOpportunityOrganizationId(UUID organizationId);

    @Query(
            value = """
                    select b from Bookmark b
                    join b.opportunity o
                    where b.student.id = :studentId
                    order by o.deadlineAt asc nulls last, b.createdAt desc
                    """,
            countQuery = "select count(b) from Bookmark b where b.student.id = :studentId"
    )
    Page<Bookmark> findByStudentIdOrderByOpportunityDeadline(UUID studentId, Pageable pageable);
}
