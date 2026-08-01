package com.opportunityboard.repository;

import com.opportunityboard.common.enums.ApplicationStatus;
import com.opportunityboard.common.enums.OpportunityStatus;
import com.opportunityboard.common.enums.UserRole;
import com.opportunityboard.entity.Application;
import com.opportunityboard.entity.Bookmark;
import com.opportunityboard.entity.Opportunity;
import com.opportunityboard.entity.OpportunityCategory;
import com.opportunityboard.entity.OrganizationProfile;
import com.opportunityboard.entity.StudentProfile;
import com.opportunityboard.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RepositoryMappingTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private OrganizationProfileRepository organizationProfileRepository;

    @Autowired
    private OpportunityCategoryRepository categoryRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Test
    void seedReferenceDataIsAvailable() {
        assertThat(categoryRepository.findBySlug("internship")).isPresent();
        assertThat(tagRepository.findBySlug("remote")).isPresent();
        assertThat(skillRepository.findBySlug("java")).isPresent();
    }

    @Test
    void savesCoreOpportunityStudentApplicationRelationships() {
        User studentUser = new User();
        studentUser.setEmail("student@example.com");
        studentUser.setPasswordHash("hashed-password");
        studentUser.setFullName("Student User");
        studentUser.setRole(UserRole.STUDENT);
        studentUser = userRepository.save(studentUser);

        StudentProfile student = new StudentProfile();
        student.setUser(studentUser);
        student.setUniversity("FPT University");
        student.setMajor("Software Engineering");
        student = studentProfileRepository.save(student);

        User organizationUser = new User();
        organizationUser.setEmail("org@example.com");
        organizationUser.setPasswordHash("hashed-password");
        organizationUser.setFullName("Organization Owner");
        organizationUser.setRole(UserRole.ORGANIZATION);
        organizationUser = userRepository.save(organizationUser);

        OrganizationProfile organization = new OrganizationProfile();
        organization.setUser(organizationUser);
        organization.setOrganizationName("Opportunity Labs");
        organization = organizationProfileRepository.save(organization);

        OpportunityCategory category = categoryRepository.findBySlug("internship").orElseThrow();
        Opportunity opportunity = new Opportunity();
        opportunity.setOrganization(organization);
        opportunity.setCategory(category);
        opportunity.setTitle("Backend Intern");
        opportunity.setDescription("Build REST APIs for student products.");
        opportunity.setStatus(OpportunityStatus.APPROVED);
        opportunity = opportunityRepository.save(opportunity);

        Bookmark bookmark = new Bookmark();
        bookmark.setStudent(student);
        bookmark.setOpportunity(opportunity);
        bookmarkRepository.save(bookmark);

        Application application = new Application();
        application.setStudent(student);
        application.setOpportunity(opportunity);
        application.setCoverLetter("I would like to apply.");
        application = applicationRepository.save(application);

        assertThat(userRepository.findByEmail("student@example.com")).isPresent();
        assertThat(bookmarkRepository.existsByStudentIdAndOpportunityId(student.getId(), opportunity.getId())).isTrue();
        assertThat(applicationRepository.findByStudentIdAndOpportunityId(student.getId(), opportunity.getId()))
                .hasValueSatisfying(saved -> assertThat(saved.getStatus()).isEqualTo(ApplicationStatus.APPLIED));
        assertThat(opportunityRepository.findByStatus(OpportunityStatus.APPROVED, org.springframework.data.domain.Pageable.unpaged()).getContent())
                .extracting(Opportunity::getId)
                .contains(application.getOpportunity().getId());
    }
}
