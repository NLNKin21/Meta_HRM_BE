package com.metahrms.employee_management.mapper;

import com.metahrms.employee_management.dto.response.PageResponseDto;
import org.springframework.data.domain.Page;

public class PageMapper {

    private PageMapper() {
    }

    public static <T> PageResponseDto<T> toPageResponse(Page<T> page) {
        return PageResponseDto.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .numberOfElements(page.getNumberOfElements())
                .empty(page.isEmpty())
                .build();
    }
}