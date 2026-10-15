package com.example.talentbridge.shared.domain.query;

import java.util.List;
import java.util.function.Function;

/** Framework-free page of results. {@code page} is zero-based. */
public record PageResult<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public PageResult {
        content = content == null ? List.of() : List.copyOf(content);
    }

    public <R> PageResult<R> map(Function<? super T, ? extends R> mapper) {
        return new PageResult<>(content.stream().<R>map(mapper).toList(), page, size, totalElements, totalPages);
    }
}
