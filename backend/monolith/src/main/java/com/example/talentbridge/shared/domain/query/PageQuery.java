package com.example.talentbridge.shared.domain.query;

import java.util.List;

/** Framework-free paging request. {@code page} is zero-based. */
public record PageQuery(int page, int size, List<SortOrder> sort) {

    public PageQuery {
        sort = sort == null ? List.of() : List.copyOf(sort);
    }

    public static PageQuery of(int page, int size) {
        return new PageQuery(page, size, List.of());
    }
}
