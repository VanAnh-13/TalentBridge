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
        try {
            return converter.convert(filter.value());
        } catch (RuntimeException ex) {
            throw new InvalidRequestException("Bộ lọc không hợp lệ: " + ex.getMessage());
        }
    }
}
