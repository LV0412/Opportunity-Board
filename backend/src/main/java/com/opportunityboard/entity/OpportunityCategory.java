package com.opportunityboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "opportunity_categories")
public class OpportunityCategory extends BaseEntity {
    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(nullable = false, unique = true, length = 90)
    private String slug;

    @Column(columnDefinition = "text")
    private String description;

    @OneToMany(mappedBy = "category")
    private Set<Opportunity> opportunities = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Opportunity> getOpportunities() {
        return opportunities;
    }
}
