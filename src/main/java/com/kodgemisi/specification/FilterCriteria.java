package com.kodgemisi.specification;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.JoinType;

/**
 * Created on October, 2018
 *
 * @author Destan Sarpkaya
 * @author Ersan Ceylan
 * @author Gökhan Birinci
 */

/**
 * Class that holds key value pair for given filter parameters
 * @param <T>
 */
@Getter
@Setter
class FilterCriteria<T> {

	@NonNull
	private final String key;

	private final T value;

	@NonNull
	private final CriteriaOperation operation;

	private final JoinType joinType;

	private final Class<T> clazz;

	private final RelationType relationType;

	FilterCriteria(String key, T value, CriteriaOperation operation, Class<T> clazz) {
		this.key = key;
		this.value = value;
		this.operation = operation;
		this.joinType = null;
		this.clazz = clazz;
		this.relationType = RelationType.NO_RELATION;
	}

	FilterCriteria(String key, T value, CriteriaOperation operation, Class<T> clazz, RelationType relationType) {
		this.key = key;
		this.value = value;
		this.operation = operation;
		this.joinType = null;
		this.clazz = clazz;
		this.relationType = relationType;
	}

	FilterCriteria(String key, CriteriaOperation operation, JoinType joinType, Class<T> clazz) {
		this.key = key;
		this.operation = operation;
		this.joinType = joinType;
		this.clazz = clazz;
		this.value = null;
		this.relationType = RelationType.NO_RELATION;
	}
}