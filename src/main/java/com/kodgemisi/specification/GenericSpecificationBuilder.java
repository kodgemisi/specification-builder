package com.kodgemisi.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on October, 2018
 *
 * @author Destan Sarpkaya
 * @author Ersan Ceylan
 */

/**
 * <p>
 * {@link Specification} builder class that helps building complex queries
 *  * by chaining methods which provided in this class. For example:
 * </p>
 *
 * <blockquote><pre>
 *		GenericSpecificationBuilder.of(Person.class)
 *			.like("name", name)
 *			.greaterThan("age", age)
 *		.build();
 * </pre></blockquote>
 *
 *
 * @param <E>
 */
public class GenericSpecificationBuilder<E> {

	private final List<FilterCriteria<?>> filterCriteriaList;

	private final List<Specification<E>> specifications;

	private GenericSpecificationBuilder() {
		filterCriteriaList = new ArrayList<>();
		specifications = new ArrayList<>();
	}

	/**
	 * returns an instance of GenericSpecificationBuilder
	 * @param clazz
	 * @param <E>
	 * @throws IllegalArgumentException in case the given clazz is {@link java.lang.Number}.
	 * @return this
	 */
	public static <E> GenericSpecificationBuilder<E> of(Class<E> clazz) {
		if(Number.class.isAssignableFrom(clazz)) {
			// This is to assure long type of clazz is for count queries
			// See com.kodgemisi.suite.common.specification.GenericSpecification.toPredicate
			throw new IllegalArgumentException("Only entities allowed.");
		}
		return new GenericSpecificationBuilder<>();
	}

	@SuppressWarnings("unchecked")
	private <C> GenericSpecificationBuilder<E>  addCriteria(String key, C value, CriteriaOperation operation) {
		if (value != null) {
			filterCriteriaList.add(new FilterCriteria<>(key, value, operation, (Class<C>) value.getClass()));
		}
		return this;
	}

	private GenericSpecificationBuilder<E> addCriteria(String key, CriteriaOperation operation) {
		filterCriteriaList.add(new FilterCriteria<Void>(key, operation, Void.class));
		return this;
	}

	/**
	 * Adds a new join criteria to the filterCriteriaList
	 * @param key field name of relation
	 * @return
	 */
	public GenericSpecificationBuilder<E> join(String key) {
		return addCriteria(key, CriteriaOperation.JOIN);
	}

	/**
	 * Adds a new join fetch criteria to the filterCriteriaList
	 * @param key field name of relation
	 * @return
	 */
	public GenericSpecificationBuilder<E> joinFetch(String key) {
		return addCriteria(key, CriteriaOperation.JOIN_FETCH);
	}

	/**
	 * Adds a new "equals" criteria to the filterCriteriaList
	 * @param key field name
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> equals(String key, Object value) {
		return addCriteria(key, value, CriteriaOperation.EQUAL);
	}

	/**
	 * Adds a new "like" criteria to the filterCriteriaList
	 * @param key field name
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> like(String key, Object value) {
		return addCriteria(key, value, CriteriaOperation.LIKE);
	}

	/**
	 * Adds a new "in" criteria to the filterCriteriaList
	 * @param key field name
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> in(String key, Object value) {
		return addCriteria(key, value, CriteriaOperation.IN);
	}

	/**
	 * Adds a new "lessThan" criteria to the filterCriteriaList
	 * @param key field name
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> lessThan(String key, Object value) {
		return addCriteria(key, value, CriteriaOperation.LESS_THAN);
	}

	/**
	 * Adds a new "lessThanOrEqualTo" criteria to the filterCriteriaList
	 * @param key field name
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> lessThanOrEqualTo(String key, Object value) {
		return addCriteria(key, value, CriteriaOperation.LESS_THAN_OR_EQUAL_TO);
	}

	/**
	 * Adds a new "greaterThan" criteria to the filterCriteriaList
	 * @param key field name
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> greaterThan(String key, Object value) {
		return addCriteria(key, value, CriteriaOperation.GREATER_THAN);
	}

	/**
	 * Adds a new "greaterThanOrEqualTo" criteria to the filterCriteriaList
	 * @param key field name
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> greaterThanOrEqualTo(String key, Object value) {
		return addCriteria(key, value, CriteriaOperation.GREATER_THAN_OR_EQUAL_TO);
	}

	/**
	 * Adds a new "equals" criteria through given toMany relation key to the filterCriteriaList
	 * @param key field name of relation
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> equalsWithToManyRelation(String key, Object value) {
		return addCriteria(key, value, CriteriaOperation.EQUAL_TO_MANY);
	}

	/**
	 * Adds a new "equals" criteria through given toOne relation key to the filterCriteriaList
	 * @param key field name of relation
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> equalsWithToOneRelation(String key, Object value) {
		return addCriteria(key, value, CriteriaOperation.EQUAL_TO_ONE);
	}

	/**
	 * Adds a new {@link org.springframework.data.jpa.domain.Specification} to the specifications list <b>directly</b>
	 * @param specification
	 * @return
	 */
	public GenericSpecificationBuilder<E> custom(Specification<E> specification) {
		specifications.add(specification);
		return this;
	}

	/**
	 * <p>
	 * 	Generates a {@link com.kodgemisi.specification.GenericSpecification} object for each given filter criteria paramater
	 * 	by iterating filterCriteriaList then combines them with AND clause
	 * </p>
	 *
	 * @return {@link org.springframework.data.jpa.domain.Specification}
	 */
	@SuppressWarnings("unchecked")
	public Specification<E> build() {
		if (filterCriteriaList.size() == 0) {
			return null;
		}

		for (FilterCriteria<?> filterCriteria : filterCriteriaList) {
			this.specifications.add(new GenericSpecification(filterCriteria));
		}

		// this is required to initiate where clause
		Specification<E> specificationResult = this.specifications.get(0);
		for (int i = 1; i < this.specifications.size(); i++) {
			specificationResult = Specification.where(specificationResult).and(specifications.get(i));
		}

		return specificationResult;
	}

}