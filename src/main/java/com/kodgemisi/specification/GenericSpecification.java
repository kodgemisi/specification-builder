package com.kodgemisi.specification;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * <p>
 * Class which implements {@link org.springframework.data.jpa.domain.Specification} interface
 * to generate {@link javax.persistence.criteria.Predicate} object
 * by checking given {@link com.kodgemisi.specification.FilterCriteria} object
 * </p>
 *
 * @param <T>
 * @param <C>
 *
 * Created on October, 2018
 *
 * @author Destan Sarpkaya
 * @author Ersan Ceylan
 */

@SuppressWarnings({"rawtypes", "unchecked"})
@AllArgsConstructor
class GenericSpecification<E, T, C extends Comparable<? super C>> implements Specification<E> {

	private final FilterCriteria<T> filterCriteria;

	@Override
	public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

		final CriteriaOperation operation = filterCriteria.getOperation();
		final String key = filterCriteria.getKey();

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
			return criteriaBuilder.equal(root.get(key), filterCriteria.getValue());

		case EQUAL_TO_ONE: {
			final String columns[] = key.split("\\.");
			Path<?> path = root.get(columns[0]);
			for (int i = 1; i < columns.length; i++) {
				path = path.get(columns[i]);
			}

			return criteriaBuilder.equal(path, filterCriteria.getValue());
		}

		case EQUAL_TO_MANY: {
			final String columns[] = key.split("\\.");
			final Join<T, ?> joinedTable = root.join(columns[0]);
			return criteriaBuilder.equal(joinedTable.get(columns[1]), filterCriteria.getValue());}

		case LIKE:
			return criteriaBuilder.like(root.get(key), "%" + filterCriteria.getValue() + "%");

		case IN:
			return root.get(key).in(filterCriteria.getValue());

		case GREATER_THAN: {
			final ComparableFilterCriteria<C> comparableFilterCriteria = getComparableFilterCriteria();
			return criteriaBuilder.greaterThan(root.get(key).as(comparableFilterCriteria.getClazz()), comparableFilterCriteria.getValue());
		}

		case GREATER_THAN_OR_EQUAL_TO: {
			final ComparableFilterCriteria<C> comparableFilterCriteria = getComparableFilterCriteria();
			return criteriaBuilder.greaterThanOrEqualTo(root.get(key).as(comparableFilterCriteria.getClazz()), comparableFilterCriteria.getValue());
		}

		case LESS_THAN: {
			final ComparableFilterCriteria<C> comparableFilterCriteria = getComparableFilterCriteria();
			return criteriaBuilder.lessThan(root.get(key).as(comparableFilterCriteria.getClazz()), comparableFilterCriteria.getValue());
		}

		case LESS_THAN_OR_EQUAL_TO: {
			final ComparableFilterCriteria<C> comparableFilterCriteria = getComparableFilterCriteria();
			return criteriaBuilder.lessThanOrEqualTo(root.get(key).as(comparableFilterCriteria.getClazz()), comparableFilterCriteria.getValue());
		}

		default:
			return null;
		}
	}

	private ComparableFilterCriteria<C> getComparableFilterCriteria() {
		if(this.filterCriteria instanceof ComparableFilterCriteria) {
			return (ComparableFilterCriteria) filterCriteria;
		}
		throw new ClassCastException("TODO");//TODO
	}
}