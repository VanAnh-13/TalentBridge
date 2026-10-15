package com.example.talentbridge.shared.infrastructure.persistence;

import com.example.talentbridge.shared.domain.exception.InvalidRequestException;
import com.example.talentbridge.shared.domain.query.FilterExpression;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/** Translates the API filter language into a JPA {@link Specification} (spring-filter). */
@Component
@RequiredArgsConstructor
public class FilterSpecifications {

    private final FilterSpecificationConverter converter;

    public <T> Specification<T> toSpecification(FilterExpression filter) {
        if (filter == null || filter.isEmpty())
            return Specification.where(null);
        Specification<T> parsed;
        try {
            parsed = converter.convert(filter.value());
        } catch (RuntimeException ex) {
            throw new InvalidRequestException("Bộ lọc không hợp lệ: " + ex.getMessage());
        }
        // A well-formed filter can still name a field the entity does not have; that only shows when the
        // query is built. Report it as a bad request instead of letting it surface as a 500 (or, for an
        // anonymous caller, as a 401 from the error dispatch).
        return (root, query, builder) -> {
            try {
                return parsed.toPredicate(root, query, builder);
            } catch (IllegalArgumentException ex) {
                throw new InvalidRequestException("Bộ lọc không hợp lệ: " + ex.getMessage());
            }
        };
    }
}
