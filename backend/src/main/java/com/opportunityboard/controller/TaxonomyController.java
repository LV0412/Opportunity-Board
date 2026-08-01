package com.opportunityboard.controller;

import com.opportunityboard.dto.response.admin.CategoryResponse;
import com.opportunityboard.dto.response.admin.TagResponse;
import com.opportunityboard.service.admin.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/taxonomy")
public class TaxonomyController {
    private final AdminService adminService;

    public TaxonomyController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/categories")
    public List<CategoryResponse> listCategories() {
        return adminService.listCategories();
    }

    @GetMapping("/tags")
    public List<TagResponse> listTags() {
        return adminService.listTags();
    }
}
