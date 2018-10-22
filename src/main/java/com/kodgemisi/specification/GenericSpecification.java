package com.kodgemisi.specification;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * Created on October, 2018
 *
 * @author Destan Sarpkaya
 * @author Ersan Ceylan
 */

/**
 * <p>
 * Class which implements {@link org.springframework.data.jpa.domain.Specification} interface
 * to generate {@link javax.persistence.criteria.Predicate} object
 * by checking given {@link com.kodgemisi.specification.FilterCriteria} object
 * </p>
 *
 * @param <T>
 * @param <C>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@AllArgsConstructor
public class GenericSpecification<T, C extends Comparable> implements Specification<T> {

	private final FilterCriteria<C> filterCriteria;

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

		final CriteriaOperation operation = filterCriteria.getOperation();
		final String key = filterCriteria.getKey();
		final C value = filterCriteria.getValue();

		switch (operation) {
		case JOIN:
			root.join(key);
			return null;

		case JOIN_FETCH:
			final Class clazz = query.getResultType();
			if (clazz.equals(Long.class) || clazz.equals(long.class)) {
				// If clazz is long then it's a count query for pageable
				root.join(key);
				return null;
			}
			else {
				root.fetch(key);
				return null;
			}
		case EQUAL:
			return criteriaBuilder.equal(root.get(key), value);

		case EQUAL_TO_ONE: {
			final String columns[] = key.split("\\.");
			Path<?> path = root.get(columns[0]);
			for (int i = 1; i < columns.length; i++) {
				path = path.get(columns[i]);
			}

			return criteriaBuilder.equal(path, value);
		}

		case EQUAL_TO_MANY: {
			final String columns[] = key.split("\\.");
			final Join<T, ?> joinedTable = root.join(columns[0]);
			return criteriaBuilder.equal(joinedTable.get(columns[1]), value);}

		case LIKE:
			return criteriaBuilder.like(root.get(key), "%" + value + "%");

		case GREATER_THAN:
			return criteriaBuilder.greaterThan(root.get(key).as(filterCriteria.getClazz()), value);

		case GREATER_THAN_OR_EQUAL_TO:
			return criteriaBuilder.greaterThanOrEqualTo(root.get(key).as(filterCriteria.getClazz()), value);

		case LESS_THAN:
			return criteriaBuilder.lessThan(root.get(key).as(filterCriteria.getClazz()), value);

		case LESS_THAN_OR_EQUAL_TO:
			return criteriaBuilder.lessThanOrEqualTo(root.get(key).as(filterCriteria.getClazz()), value);

		default:
			return null;
		}
	}
}