package com.bobocode.services.filter_and_search;

import com.bobocode.entities.filtering.SearchCriteria;
import com.bobocode.enums.SearchOperations;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generic builder for constructing dynamic JPA {@link Specification} instances
 * from a list of {@link SearchCriteria}.
 *
 * @param <T> the type of the entity
 */
public class GenericSpecificationsBuilder<T> {

    private final List<SearchCriteria> criteriaList;

    public GenericSpecificationsBuilder() {
        this.criteriaList = new ArrayList<>();
    }

    /**
     * Adds a search criterion if the provided value is non-null and non-empty.
     *
     * @param key       the field name / path in the entity
     * @param operation the search operation to apply
     * @param value     the value to compare against
     * @return the builder instance for method chaining
     */
    public GenericSpecificationsBuilder<T> with(String key, SearchOperations operation, Object value) {
        if (value != null) {
            if (value instanceof Collection<?> collection && collection.isEmpty()) {
                return this;
            }
            if (value instanceof String str && str.isBlank()) {
                return this;
            }
            criteriaList.add(new SearchCriteria(key, operation, value));
        }
        return this;
    }

    /**
     * Adds an existing {@link SearchCriteria} if non-null and having a non-null value.
     *
     * @param criteria the search criteria object
     * @return the builder instance for method chaining
     */
    public GenericSpecificationsBuilder<T> with(SearchCriteria criteria) {
        if (criteria != null && criteria.getValue() != null) {
            criteriaList.add(criteria);
        }
        return this;
    }

    /**
     * Builds the final {@link Specification} combining all criteria with logical AND.
     *
     * @return the composite specification, or an empty specification if no criteria exist
     */
    public Specification<T> build() {
        if (criteriaList.isEmpty()) {
            return Specification.where(null);
        }

        Specification<T> result = new GenericSpecification<>(criteriaList.get(0));

        for (int i = 1; i < criteriaList.size(); i++) {
            result = Specification.where(result).and(new GenericSpecification<>(criteriaList.get(i)));
        }

        return result;
    }
}
