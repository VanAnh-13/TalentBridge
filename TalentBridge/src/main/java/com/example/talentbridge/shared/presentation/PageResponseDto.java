package com.example.talentbridge.shared.presentation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageResponseDto<T> {

    private List<T> content;

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;


    /** The API exposes one-based page numbers. */
    public static <T> PageResponseDto<T> from(com.example.talentbridge.shared.domain.query.PageResult<T> result) {
        return new PageResponseDto<>(
                result.content(),
                result.page() + 1,
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
