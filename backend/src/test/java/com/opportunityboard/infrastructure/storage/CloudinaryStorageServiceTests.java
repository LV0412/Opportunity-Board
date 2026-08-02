package com.opportunityboard.infrastructure.storage;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CloudinaryStorageServiceTests {

    @Test
    void pdfUploadOptionsCreateBrowserRenderableCloudinaryAsset() {
        Map<String, Object> options = CloudinaryStorageService.uploadOptions(
                "opportunity-board/resumes",
                "image",
                "pdf"
        );

        assertThat(options)
                .containsEntry("folder", "opportunity-board/resumes")
                .containsEntry("resource_type", "image")
                .containsEntry("format", "pdf")
                .containsEntry("use_filename", true)
                .containsEntry("unique_filename", true);
    }

    @Test
    void logoUploadOptionsDoNotForceAFormat() {
        Map<String, Object> options = CloudinaryStorageService.uploadOptions(
                "opportunity-board/logos",
                "image",
                null
        );

        assertThat(options).doesNotContainKey("format");
    }
}
