package com.kodgemisi.specification;

class ComparableFilterCriteria<T extends Comparable<? super T>> extends FilterCriteria<T> {

	ComparableFilterCriteria(String key, T value, CriteriaOperation operation, Class<T> clazz, ConditionType conditionType) {
		super(key, value, operation, clazz, conditionType);
	}

	ComparableFilterCriteria(String key, T value, CriteriaOperation operation, Class<T> clazz, RelationType relationType, ConditionType conditionType) {
		super(key, value, operation, clazz, relationType, conditionType);
	}
}