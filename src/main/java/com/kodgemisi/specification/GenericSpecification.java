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
 * @author Gökhan Birinci
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
		case JOIN: {
			final JoinType joinType = filterCriteria.getJoinType();
			root.join(key, joinType);
			return null;
		}

		case JOIN_FETCH: {
			final Class clazz = query.getResultType();
			final JoinType joinType = filterCriteria.getJoinType();
			if (clazz.equals(Long.class) || clazz.equals(long.class)) {
				// If clazz is long then it's a count query for pageable
				root.join(key, joinType);
				return null;
			}
			else {
				root.fetch(key, joinType);
				return null;
			}
		}
		case EQUAL: {
			final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
			return criteriaBuilder.equal(path, filterCriteria.getValue());
		}

		case LIKE: {
			final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
			return criteriaBuilder.like(path.as(String.class), "%" + filterCriteria.getValue() + "%");
		}

		case IN: {
			final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
			return path.in(filterCriteria.getValue());
		}

		case GREATER_THAN: {
			final ComparableFilterCriteria<C> comparableFilterCriteria = getComparableFilterCriteria();
			final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
			return criteriaBuilder.greaterThan(path.as(comparableFilterCriteria.getClazz()), comparableFilterCriteria.getValue());
		}

		case GREATER_THAN_OR_EQUAL_TO: {
			final ComparableFilterCriteria<C> comparableFilterCriteria = getComparableFilterCriteria();
			final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
			return criteriaBuilder.greaterThanOrEqualTo(path.as(comparableFilterCriteria.getClazz()), comparableFilterCriteria.getValue());
		}

		case LESS_THAN: {
			final ComparableFilterCriteria<C> comparableFilterCriteria = getComparableFilterCriteria();
			final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
			return criteriaBuilder.lessThan(path.as(comparableFilterCriteria.getClazz()), comparableFilterCriteria.getValue());
		}

		case LESS_THAN_OR_EQUAL_TO: {
			final ComparableFilterCriteria<C> comparableFilterCriteria = getComparableFilterCriteria();
			final Path<?> path = resolvePath(root, filterCriteria.getKey(), filterCriteria.getRelationType());
			return criteriaBuilder.lessThanOrEqualTo(path.as(comparableFilterCriteria.getClazz()), comparableFilterCriteria.getValue());
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

	private Path<?> resolvePath(Root<E> root, String key, RelationType relationType) {
		if (relationType.equals(RelationType.NO_RELATION)) {
			return root.get(key);
		}
		else if (relationType.equals(RelationType.TO_ONE)) {
			final String columns[] = key.split("\\.");
			Path<?> path = root.get(columns[0]);
			for (int i = 1; i < columns.length; i++) {
				path = path.get(columns[i]);
			}
			return path;
		}
		else {
			final String columns[] = key.split("\\.");
			final Join<T, ?> joinedTable = root.join(columns[0]);
			return joinedTable.get(columns[1]);
		}
	}

}