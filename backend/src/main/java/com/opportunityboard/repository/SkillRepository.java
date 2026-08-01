package com.opportunityboard.repository;

import com.opportunityboard.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {
    Optional<Skill> findBySlug(String slug);

    List<Skill> findBySlugIn(Collection<String> slugs);
}
