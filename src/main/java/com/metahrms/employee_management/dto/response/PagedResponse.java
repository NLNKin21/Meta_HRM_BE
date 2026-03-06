package com.metahrms.employee_management.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Generic paginated response wrapper for list endpoints")
public class PagedResponse<T> {
    @Schema(description = "List of items in the current page")
    List<T> content;

    @Schema(description = "Current page number (zero-based)", example = "0")
    Integer currentPage;

    @Schema(description = "Number of items per page", example = "10")
    Integer pageSize;

    @Schema(description = "Total number of items across all pages", example = "100")
    Long totalElements;

    @Schema(description = "Total number of pages", example = "10")
    Integer totalPages;

    @Schema(description = "Indicates if there is a next page", example = "true")
    Boolean hasNext;

    @Schema(description = "Indicates if there is a previous page", example = "false")
    Boolean hasPrevious;

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private List<T> content;
        private Integer currentPage;
        private Integer pageSize;
        private Long totalElements;
        private Integer totalPages;
        private Boolean hasNext;
        private Boolean hasPrevious;

        public Builder<T> content(List<T> content) {
            this.content = content;
            return this;
        }

        public Builder<T> currentPage(Integer currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public Builder<T> pageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder<T> totalElements(Long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public Builder<T> totalPages(Integer totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder<T> hasNext(Boolean hasNext) {
            this.hasNext = hasNext;
            return this;
        }

        public Builder<T> hasPrevious(Boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
            return this;
        }

        public PagedResponse<T> build() {
            PagedResponse<T> response = new PagedResponse<>();
            response.content = this.content;
            response.currentPage = this.currentPage;
            response.pageSize = this.pageSize;
            response.totalElements = this.totalElements;
            response.totalPages = this.totalPages;
            response.hasNext = this.hasNext;
            response.hasPrevious = this.hasPrevious;
            return response;
        }
    }
}
