package com.bobocode.services.filter_and_search;

import com.bobocode.entities.filtering.SearchCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.util.Collection;

@AllArgsConstructor
public class GenericSpecification<T> implements Specification<T> {

    private final SearchCriteria criteria;

    @Nullable
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Predicate toPredicate(Root<T> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Path<?> path = getPath(root, criteria.getKey());

        return switch (criteria.getSearchOperation()) {
            case EQUAL -> criteriaBuilder.equal(path, criteria.getValue());
            case IN -> {
                if (criteria.getValue() instanceof Collection<?> collection) {
                    yield path.in(collection);
                }
                yield path.in(criteria.getValue());
            }
            case GREATER_THAN -> criteriaBuilder.greaterThan((Expression) path, (Comparable) criteria.getValue());
            case LESS_THAN -> criteriaBuilder.lessThan((Expression) path, (Comparable) criteria.getValue());
            case LIKE -> criteriaBuilder.like(
                    criteriaBuilder.lower((Expression<String>) path),
                    "%" + criteria.getValue().toString().toLowerCase() + "%"
            );
        };
    }

    private Path<?> getPath(Root<T> root, String key) {
        if (key.contains(".")) {
            String[] parts = key.split("\\.");
            Path<?> current = root;
            for (String part : parts) {
                current = current.get(part);
            }
            return current;
        }
        return root.get(key);
    }
}
