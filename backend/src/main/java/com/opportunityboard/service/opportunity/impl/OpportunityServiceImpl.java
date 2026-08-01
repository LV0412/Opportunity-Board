package com.opportunityboard.service.opportunity.impl;

import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.dto.request.opportunity.CreateOpportunityRequest;
import com.opportunityboard.dto.request.opportunity.UpdateOpportunityRequest;
import com.opportunityboard.dto.response.opportunity.OpportunityResponse;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.OpportunityCategory;
import com.opportunityboard.entity.OrganizationProfile;
import com.opportunityboard.entity.Tag;
import com.opportunityboard.repository.OpportunityCategoryRepository;
import com.opportunityboard.repository.OpportunityRepository;
import com.opportunityboard.repository.OrganizationProfileRepository;
import com.opportunityboard.repository.TagRepository;
import com.opportunityboard.security.CustomUserDetails;
import com.opportunityboard.service.opportunity.OpportunityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class OpportunityServiceImpl implements OpportunityService {
    private final OpportunityRepository opportunityRepository;
    private final OrganizationProfileRepository organizationProfileRepository;
    private final OpportunityCategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final OpportunityMapper opportunityMapper;

    public OpportunityServiceImpl(
            OpportunityRepository opportunityRepository,
            OrganizationProfileRepository organizationProfileRepository,
            OpportunityCategoryRepository categoryRepository,
            TagRepository tagRepository,
            OpportunityMapper opportunityMapper
    ) {
        this.opportunityRepository = opportunityRepository;
        this.organizationProfileRepository = organizationProfileRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.opportunityMapper = opportunityMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OpportunityResponse> listApproved(Pageable pageable) {
        return opportunityRepository.findByStatusAndDeadlineAtAfterOrStatusAndDeadlineAtIsNull(
                OpportunityStatus.APPROVED,
                Instant.now(),
                OpportunityStatus.APPROVED,
                pageable
        ).map(opportunityMapper::toResponse);
    }

    @Override
    @Transactional
    public OpportunityResponse getPublicOpportunity(UUID id) {
        Opportunity opportunity = findOpportunity(id);
        if (opportunity.getStatus() != OpportunityStatus.APPROVED || isExpired(opportunity)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found");
        }
        opportunity.setViewCount(opportunity.getViewCount() + 1);
        opportunityRepository.save(opportunity);
        return opportunityMapper.toResponse(opportunity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OpportunityResponse> listMyOpportunities(CustomUserDetails currentUser, Pageable pageable) {
        OrganizationProfile organization = findMyOrganization(currentUser);
        return opportunityRepository.findByOrganizationId(organization.getId(), pageable)
                .map(opportunityMapper::toResponse);
    }

    @Override
    @Transactional
    public OpportunityResponse create(CustomUserDetails currentUser, CreateOpportunityRequest request) {
        OrganizationProfile organization = findMyOrganization(currentUser);
        Opportunity opportunity = new Opportunity();
        opportunity.setOrganization(organization);
        applyRequest(opportunity, request.title(), request.description(), request.requirements(), request.location(),
                request.remote(), request.applyUrl(), request.deadlineAt(), request.categorySlug(), request.tags());
        opportunity.setStatus(OpportunityStatus.PENDING);
        return opportunityMapper.toResponse(opportunityRepository.save(opportunity));
    }

    @Override
    @Transactional
    public OpportunityResponse update(CustomUserDetails currentUser, UUID id, UpdateOpportunityRequest request) {
        Opportunity opportunity = findOpportunity(id);
        ensureOwner(currentUser, opportunity);
        if (opportunity.getStatus() == OpportunityStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Closed opportunity cannot be edited");
        }

        applyRequest(opportunity, request.title(), request.description(), request.requirements(), request.location(),
                request.remote(), request.applyUrl(), request.deadlineAt(), request.categorySlug(), request.tags());
        opportunity.setStatus(OpportunityStatus.PENDING);
        return opportunityMapper.toResponse(opportunityRepository.save(opportunity));
    }

    @Override
    @Transactional
    public void close(CustomUserDetails currentUser, UUID id) {
        Opportunity opportunity = findOpportunity(id);
        if (extractRole(currentUser) != UserRole.ADMIN) {
            ensureOwner(currentUser, opportunity);
        }
        opportunity.setStatus(OpportunityStatus.CLOSED);
        opportunityRepository.save(opportunity);
    }

    private void applyRequest(
            Opportunity opportunity,
            String title,
            String description,
            String requirements,
            String location,
            boolean remote,
            String applyUrl,
            Instant deadlineAt,
            String categorySlug,
            List<String> tags
    ) {
        opportunity.setTitle(title.trim());
        opportunity.setDescription(description.trim());
        opportunity.setRequirements(trimToNull(requirements));
        opportunity.setLocation(trimToNull(location));
        opportunity.setRemote(remote);
        opportunity.setApplyUrl(trimToNull(applyUrl));
        opportunity.setDeadlineAt(deadlineAt);
        opportunity.setCategory(findCategory(categorySlug));
        opportunity.setTags(resolveTags(tags));
    }

    private Opportunity findOpportunity(UUID id) {
        return opportunityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found"));
    }

    private OrganizationProfile findMyOrganization(CustomUserDetails currentUser) {
        return organizationProfileRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization profile not found"));
    }

    private OpportunityCategory findCategory(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid opportunity category"));
    }

    private Set<Tag> resolveTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        if (tagNames == null) {
            return tags;
        }

        for (String rawName : tagNames) {
            String name = trimToNull(rawName);
            if (name == null) {
                continue;
            }
            String slug = slugify(name);
            Tag tag = tagRepository.findBySlug(slug).orElseGet(() -> {
                Tag created = new Tag();
                created.setName(name);
                created.setSlug(slug);
                return tagRepository.save(created);
            });
            tags.add(tag);
        }
        return tags;
    }

    private void ensureOwner(CustomUserDetails currentUser, Opportunity opportunity) {
        if (!opportunity.getOrganization().getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this opportunity");
        }
    }

    private boolean isExpired(Opportunity opportunity) {
        return opportunity.getDeadlineAt() != null && opportunity.getDeadlineAt().isBefore(Instant.now());
    }

    private UserRole extractRole(CustomUserDetails currentUser) {
        String authority = currentUser.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority().replace("ROLE_", ""))
                .orElse(UserRole.STUDENT.name());
        return UserRole.valueOf(authority);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String slugify(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized.isBlank() ? value.toLowerCase(Locale.ROOT).replaceAll("\\s+", "-") : normalized;
    }
}
