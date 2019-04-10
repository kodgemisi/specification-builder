package com.kodgemisi.specification;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

/**
 * Created on October, 2018
 *
 * @author Destan Sarpkaya
 * @author Ersan Ceylan
 * @author Gökhan Birinci
 * @author Sedat Gokcen
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
	private final Operation operation;

	private final Class<T> clazz;

	private final RelationType relationType;

	FilterCriteria(String key, T value, Operation operation, Class<T> clazz, RelationType relationType) {
		this.key = key;
		this.value = value;
		this.operation = operation;
		this.clazz = clazz;
		this.relationType = relationType;
	}

	FilterCriteria(String key, T value, Operation operation, Class<T> clazz) {
		this(key, value, operation, clazz, RelationType.NO_RELATION);
	}

	FilterCriteria(String key, Operation operation, Class<T> clazz) {
		this(key, null, operation, clazz, RelationType.NO_RELATION);
	}
}