package com.kodgemisi.specification;

class ComparableFilterCriteria<T extends Comparable<? super T>> extends FilterCriteria<T> {

	ComparableFilterCriteria(String key, T value, CriteriaOperation operation, Class<T> clazz) {
		super(key, value, operation, clazz);
	}

	ComparableFilterCriteria(String key, T value, CriteriaOperation operation, Class<T> clazz, RelationType relationType) {
		super(key, value, operation, clazz, relationType);
	}
}