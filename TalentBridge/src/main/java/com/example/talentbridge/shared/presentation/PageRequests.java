package com.example.talentbridge.shared.presentation;

import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.SortOrder;
import org.springframework.data.domain.Pageable;

/** Maps the web layer's {@link Pageable} to the framework-free {@link PageQuery}. */
public final class PageRequests {

    private PageRequests() {
    }

    public static PageQuery from(Pageable pageable) {
        return new PageQuery(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().stream()
                        .map(o -> new SortOrder(o.getProperty(),
                                o.isDescending() ? SortOrder.Direction.DESC : SortOrder.Direction.ASC))
                        .toList());
    }
}
