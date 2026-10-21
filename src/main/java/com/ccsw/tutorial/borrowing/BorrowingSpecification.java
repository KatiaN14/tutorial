package com.ccsw.tutorial.borrowing;

import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.borrowing.model.Borrowing;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class BorrowingSpecification implements Specification<Borrowing> {
    private static final long serialVersionUID = 1L;
    private final SearchCriteria criteria;

    public BorrowingSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Borrowing> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if(criteria.getValue() == null) {
            return null;
        }
        if(criteria.getOperation().equalsIgnoreCase(":")) {
            Path<String> path = getPath(root);
            return builder.equal(path, criteria.getValue());
        } else if (criteria.getOperation().equalsIgnoreCase("<=")) {
            return builder.lessThanOrEqualTo(root.<LocalDate>get(criteria.getKey()), (LocalDate) criteria.getValue());
        } else if (criteria.getOperation().equalsIgnoreCase(">=")) {
            return builder.greaterThanOrEqualTo(root.<LocalDate>get(criteria.getKey()), (LocalDate) criteria.getValue());
        }
        return null;
    }

    private Path<String> getPath(Root<Borrowing> root) {
        String key = criteria.getKey();
        String[] split = key.split("[.]",0);

        Path<String> expression = root.get(split[0]);
        for (int i = 1; i < split.length; i++) {
            expression = expression.get(split[i]);
        }

        return expression;
    }
}
