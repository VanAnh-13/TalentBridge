package com.example.talentbridge.shared.infrastructure.persistence;

import com.example.talentbridge.shared.domain.query.PageQuery;
import com.example.talentbridge.shared.domain.query.PageResult;
import com.example.talentbridge.shared.domain.query.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.function.Function;

/** Converts between the framework-free paging types and Spring Data's. */
public final class PagingMapper {

    private PagingMapper() {
    }

    public static Pageable toPageable(PageQuery query) {
        if (query.sort().isEmpty())
            return PageRequest.of(query.page(), query.size());
        Sort sort = Sort.by(query.sort().stream()
                .map(o -> new Sort.Order(
                        o.direction() == SortOrder.Direction.DESC ? Sort.Direction.DESC : Sort.Direction.ASC,
                        o.property()))
                .toList());
        return PageRequest.of(query.page(), query.size(), sort);
    }

    public static <E, D> PageResult<D> toPageResult(Page<E> page, Function<E, D> mapper) {
        return new PageResult<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
