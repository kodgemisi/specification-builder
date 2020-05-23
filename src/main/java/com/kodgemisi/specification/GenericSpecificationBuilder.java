package com.kodgemisi.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * <p>
 * {@link Specification} builder class that helps building complex queries
 * by chaining methods which are provided in this class. For example:
 * </p>
 *
 * <blockquote><pre>
 * 		GenericSpecificationBuilder.of(Person.class)
 * 			.like("name", name)
 * 			.greaterThan("age", age)
 * 		.build();
 * </pre></blockquote>
 *
 * @param <E> Created on October, 2018
 * @author Destan Sarpkaya
 * @author Ersan Ceylan
 * @author Gökhan Birinci
 */
public class GenericSpecificationBuilder<E> {

	private final List<FilterCriteria<?>> filterCriteriaList;

	private final List<Specification<E>> specifications;

	private final Map<Specification<E>, ConditionType> customSpecifications;

	private final List<Map<String, String>> parameters;

	// defaults to AND specification
	private ConditionType activeConditionType;

	private GenericSpecificationBuilder() {
		filterCriteriaList = new ArrayList<>();
		specifications = new ArrayList<>();
		parameters = new ArrayList();
		activeConditionType = ConditionType.AND;
		customSpecifications = new HashMap<>();
	}

	/**
	 * returns an instance of GenericSpecificationBuilder
	 *
	 * @param clazz
	 * @param <E>
	 * @return this
	 * @throws IllegalArgumentException in case the given clazz is {@link java.lang.Number}.
	 */
	public static <E> GenericSpecificationBuilder<E> of(Class<E> clazz) {
		if (Number.class.isAssignableFrom(clazz)) {
			// This is to assure long type of clazz is for count queries
			// See com.kodgemisi.suite.common.specification.GenericSpecification.toPredicate
			throw new IllegalArgumentException("Only entities allowed.");
		}
		return new GenericSpecificationBuilder<>();
	}

	private GenericSpecificationBuilder<E> addCriteria(String key, CriteriaOperation operation) {
		filterCriteriaList.add(new FilterCriteria<Void>(key, operation, null, Void.class, activeConditionType));
		return this;
	}

	private GenericSpecificationBuilder<E> addCriteria(String key, CriteriaOperation operation, JoinType joinType) {
		filterCriteriaList.add(new FilterCriteria<Void>(key, operation, joinType, Void.class, activeConditionType));
		return this;
	}

	@SuppressWarnings("unchecked")
	private <C> GenericSpecificationBuilder<E> addCriteria(String key, C value, CriteriaOperation operation, RelationType relationType) {
		if (value != null) {
			filterCriteriaList.add(new FilterCriteria<>(key, value, operation, (Class<C>) value.getClass(), relationType, activeConditionType));
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	private <C extends Comparable<? super C>> GenericSpecificationBuilder<E> addComparableCriteria(String key, C value, CriteriaOperation operation,
			RelationType relationType) {
		if (value != null) {
			filterCriteriaList.add(new ComparableFilterCriteria<C>(key, value, operation, (Class<C>) value.getClass(), relationType, activeConditionType));
		}
		return this;
	}

	public GenericSpecificationBuilder<E> or() {
		activeConditionType = ConditionType.OR;
		return this;
	}

	public GenericSpecificationBuilder<E> and() {
		activeConditionType = ConditionType.AND;
		return this;
	}

	/**
	 * Adds a new inner join criteria to the filterCriteriaList
	 *
	 * @param key field name of relation
	 * @return
	 */
	public GenericSpecificationBuilder<E> join(String key) {
		return addCriteria(key, CriteriaOperation.JOIN, JoinType.INNER);
	}

	/**
	 * Adds a new join with given join type to the filterCriteriaList.
	 *
	 * @param key      field name of relation
	 * @param joinType {@link javax.persistence.criteria.JoinType}
	 * @return
	 */
	public GenericSpecificationBuilder<E> join(String key, JoinType joinType) {
		return addCriteria(key, CriteriaOperation.JOIN, joinType);
	}

	/**
	 * Adds a new inner join fetch criteria to the filterCriteriaList
	 *
	 * @param key field name of relation
	 * @return
	 */
	public GenericSpecificationBuilder<E> joinFetch(String key) {
		return addCriteria(key, CriteriaOperation.JOIN_FETCH, JoinType.INNER);
	}

	/**
	 * Adds a new join with given join type to the filterCriteriaList.
	 *
	 * @param key      field name of relation
	 * @param joinType {@link javax.persistence.criteria.JoinType }
	 * @return
	 */
	public GenericSpecificationBuilder<E> joinFetch(String key, JoinType joinType) {
		return addCriteria(key, CriteriaOperation.JOIN_FETCH, joinType);
	}

	/**
	 * Adds a new "equals" criteria to the filterCriteriaList
	 * For example:
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.equals("gender", Gender.FEMALE)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> equals(String key, Object value) {
		return addCriteria(key, value, CriteriaOperation.EQUAL, RelationType.NO_RELATION);
	}

	/**
	 * Adds a new "equals" criteria to the filterCriteriaList by joining to given relation
	 * In order to define a relation, you must use "." delimiter after relation name
	 * For example:
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.equals("addresses.city", cityName, {@link com.kodgemisi.specification.RelationType} RelationType.TO_MANY)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key          field name
	 * @param value
	 * @param relationType
	 * @return
	 */
	public GenericSpecificationBuilder<E> equals(String key, Object value, RelationType relationType) {
		return addCriteria(key, value, CriteriaOperation.EQUAL, relationType);
	}

	/**
	 * Adds a new "like" criteria to the filterCriteriaList
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.like("bio", keyword)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> like(String key, Object value) {
		return addCriteria(key, value, CriteriaOperation.LIKE, RelationType.NO_RELATION);
	}

	/**
	 * Adds a new "like" criteria to the filterCriteriaList by joining to given relation
	 * In order to define a relation, you must use "." delimiter after relation name
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.like("department.name", departmentName, {@link com.kodgemisi.specification.RelationType} RelationType.TO_ONE)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> like(String key, Object value, RelationType relationType) {
		return addCriteria(key, value, CriteriaOperation.LIKE, relationType);
	}

	/**
	 * Adds a new ignore-case "like" criteria to the filterCriteriaList
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.likeIgnoreCase("bio", keyword)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> likeIgnoreCase(String key, Object value) {
		return addCriteria(key, value, CriteriaOperation.LIKE_IGNORE_CASE, RelationType.NO_RELATION);
	}

	/**
	 * Adds a new ignore-case "like" criteria to the filterCriteriaList by joining to given relation
	 * In order to define a relation, you must use "." delimiter after relation name
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.likeIgnoreCase("department.name", departmentName, {@link com.kodgemisi.specification.RelationType} RelationType.TO_ONE)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> likeIgnoreCase(String key, Object value, RelationType relationType) {
		return addCriteria(key, value, CriteriaOperation.LIKE_IGNORE_CASE, relationType);
	}

	public GenericSpecificationBuilder<E> isNull(String key) {
		return addCriteria(key, CriteriaOperation.IS_NULL);
	}

	public GenericSpecificationBuilder<E> isNotNull(String key) {
		return addCriteria(key, CriteriaOperation.IS_NOT_NULL);
	}

	/**
	 * Adds a new "in" criteria to the filterCriteriaList
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.in("name", name)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public GenericSpecificationBuilder<E> in(String key, Object value) {
		return addCriteria(key, value, CriteriaOperation.IN, RelationType.NO_RELATION);
	}

	/**
	 * Adds a new "in" criteria to the filterCriteriaList by joining to given relation
	 * In order to define a relation, you must use "." delimiter after relation name
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.in("skills.name", Arrays.asList("JAVA", "RUBY"), {@link com.kodgemisi.specification.RelationType} RelationType.TO_MANY)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key
	 * @param value
	 * @param relationType
	 * @return
	 */
	public GenericSpecificationBuilder<E> in(String key, Object value, RelationType relationType) {
		return addCriteria(key, value, CriteriaOperation.IN, relationType);
	}

	/**
	 * Adds a new "lessThan" criteria to the filterCriteriaList
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.lessThan("birthDate", thirtyYearsAgo)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public <C extends Comparable<? super C>> GenericSpecificationBuilder<E> lessThan(String key, C value) {
		return addComparableCriteria(key, value, CriteriaOperation.LESS_THAN, RelationType.NO_RELATION);
	}

	/**
	 * Adds a new "lessThan" criteria to the filterCriteriaList by joining to given relation
	 * In order to define a relation, you must use "." delimiter after relation name
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.lessThan("wage.amount", maxWageAmount, {@link com.kodgemisi.specification.RelationType} RelationType.TO_ONE)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public <C extends Comparable<? super C>> GenericSpecificationBuilder<E> lessThan(String key, C value, RelationType relationType) {
		return addComparableCriteria(key, value, CriteriaOperation.LESS_THAN, relationType);
	}

	/**
	 * Adds a new "lessThanOrEqualTo" criteria to the filterCriteriaList
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.lessThanOrEqualTo("birthDate", thirtyYearsAgo)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public <C extends Comparable<? super C>> GenericSpecificationBuilder<E> lessThanOrEqualTo(String key, C value) {
		return addComparableCriteria(key, value, CriteriaOperation.LESS_THAN_OR_EQUAL_TO, RelationType.NO_RELATION);
	}

	/**
	 * Adds a new "lessThanOrEqualTo" criteria to the filterCriteriaList by joining to given relation
	 * In order to define a relation, you must use "." delimiter after relation name
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.lessThanOrEqualTo("wage.amount", maxWageAmount, {@link com.kodgemisi.specification.RelationType} RelationType.TO_ONE)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public <C extends Comparable<? super C>> GenericSpecificationBuilder<E> lessThanOrEqualTo(String key, C value, RelationType relationType) {
		return addComparableCriteria(key, value, CriteriaOperation.LESS_THAN_OR_EQUAL_TO, relationType);
	}

	/**
	 * Adds a new "greaterThan" criteria to the filterCriteriaList
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.greaterThan("birthDate", thirtyYearsAgo)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public <C extends Comparable<? super C>> GenericSpecificationBuilder<E> greaterThan(String key, C value) {
		return addComparableCriteria(key, value, CriteriaOperation.GREATER_THAN, RelationType.NO_RELATION);
	}

	/**
	 * Adds a new "greaterThan" criteria to the filterCriteriaList by joining to given relation
	 * In order to define a relation, you must use "." delimiter after relation name
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.greaterThan("wage.amount", minWageAmount, {@link com.kodgemisi.specification.RelationType} RelationType.TO_ONE)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public <C extends Comparable<? super C>> GenericSpecificationBuilder<E> greaterThan(String key, C value, RelationType relationType) {
		return addComparableCriteria(key, value, CriteriaOperation.GREATER_THAN, relationType);
	}

	/**
	 * Adds a new "greaterThanOrEqualTo" criteria to the filterCriteriaList
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.greaterThanOrEqualTo("birthDate", thirtyYearsAgo)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public <C extends Comparable<? super C>> GenericSpecificationBuilder<E> greaterThanOrEqualTo(String key, C value) {
		return addComparableCriteria(key, value, CriteriaOperation.GREATER_THAN_OR_EQUAL_TO, RelationType.NO_RELATION);
	}

	/**
	 * Adds a new "greaterThanOrEqualTo" criteria to the filterCriteriaList by joining to given relation
	 * In order to define a relation, you must use "." delimiter after relation name
	 * <blockquote><pre>
	 *     GenericSpecificationBuilder.of(Person.class)
	 *     	.greaterThanOrEqualTo("wage.amount", minWageAmount, {@link com.kodgemisi.specification.RelationType} RelationType.TO_ONE)
	 *     	.build();
	 * </pre></blockquote>
	 *
	 * @param key   field name
	 * @param value
	 * @return
	 */
	public <C extends Comparable<? super C>> GenericSpecificationBuilder<E> greaterThanOrEqualTo(String key, C value, RelationType relationType) {
		return addComparableCriteria(key, value, CriteriaOperation.GREATER_THAN_OR_EQUAL_TO, relationType);
	}

	/**
	 * Adds a new {@link org.springframework.data.jpa.domain.Specification} to the specifications list <b>directly</b>
	 *
	 * @param specification
	 * @return
	 */
	public GenericSpecificationBuilder<E> custom(Specification<E> specification) {
		this.customSpecifications.put(specification, activeConditionType);
		return this;
	}

	public GenericSpecificationBuilder<E> customFunction(String functionName, String[] fieldNames, String... params) {

		final int functionIndex = parameters.size();

		final HashMap<String, String> parameterMap = new HashMap<>();
		parameters.add(parameterMap);

		List<Specification<E>> specifications = new ArrayList<>();
		for (String fieldName : fieldNames) {

			final Specification<E> fieldSpecification = (Specification<E>) (root, query, criteriaBuilder) -> {

				int parameterIndex = 0;
				final List<Expression<String>> parameterExpressions = new ArrayList<>();

				if (fieldName.contains(".")) {
					final String columns[] = fieldName.split("\\.");
					final Join<E, ?> joinedTable = root.join(columns[0], JoinType.LEFT);
					Path<String> path = joinedTable.get(columns[1]);
					for (int i = 2; i < columns.length; i++) {
						path = path.get(columns[i]);
					}
					parameterExpressions.add(path);
				}
				else {
					parameterExpressions.add(root.get(fieldName));
				}

				for (String param : params) {
					final String paramsName = "function" + functionIndex + "_param" + parameterIndex + "_" +
							StringUtils.deleteAny(UUID.randomUUID().toString(), "-");
					parameterMap.put(paramsName, param);
					final ParameterExpression<String> parameterExpression = criteriaBuilder.parameter(String.class, paramsName);
					++parameterIndex;
					parameterExpressions.add(parameterExpression);
				}

				final Expression<?>[] parameters = parameterExpressions.toArray(new Expression<?>[] {});
				final Expression<Boolean> sqlFunction = criteriaBuilder.function(functionName, Boolean.class, parameters);

				return criteriaBuilder.equal(sqlFunction, "");
			};

			specifications.add(fieldSpecification);
		}

		Specification<E> specificationResult = specifications.get(0);
		for (int i = 1; i < specifications.size(); i++) {
			specificationResult = Specification.where(specificationResult).or(specifications.get(i));
		}

		this.custom(specificationResult);
		return this;
	}

	/**
	 * <p>
	 * Generates a {@link com.kodgemisi.specification.GenericSpecification} object for each given filter criteria paramater
	 * by iterating filterCriteriaList then combines them with AND clause
	 * </p>
	 *
	 * @return {@link org.springframework.data.jpa.domain.Specification}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Specification<E> build() {
		if (filterCriteriaList.size() == 0 && customSpecifications.isEmpty()) {
			return null;
		}

		Specification<E> andSpecs = null;
		Specification<E> orSpecs = null;
		for (FilterCriteria<?> filterCriteria : filterCriteriaList) {

			final Specification<E> specification = new GenericSpecification(filterCriteria);
			if (filterCriteria.getConditionType().equals(ConditionType.AND)) {
				andSpecs = Specification.where(andSpecs).and(specification);
			}
			else {
				orSpecs = Specification.where(orSpecs).or(specification);
			}
		}

		// iterate over custom specification list
		for (Map.Entry<Specification<E>, ConditionType> specificationConditionTypeEntry : customSpecifications.entrySet()) {
			if (specificationConditionTypeEntry.getValue().equals(ConditionType.AND)) {
				andSpecs = Specification.where(andSpecs).and(specificationConditionTypeEntry.getKey());
			}
			else {
				orSpecs = Specification.where(orSpecs).or(specificationConditionTypeEntry.getKey());
			}
		}

		final Specification<E> specificationResult = Specification.where(andSpecs).and(orSpecs);

		return new GenericSpecificationContainer<E>(specificationResult, parameters);
	}

}