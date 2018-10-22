package com.kodgemisi.specification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

/**
 * Created on October, 2018
 *
 * @author Destan Sarpkaya
 * @author Ersan Ceylan
 */

/**
 * Class that holds key value pair for given filter parameters
 * @param <T>
 */
@Getter
@Setter
@AllArgsConstructor
class FilterCriteria<T> {

	@NonNull
	private final String key;

	private T value;

	@NonNull
	private final CriteriaOperation operation;

	private final Class<T> clazz;

	FilterCriteria(String key, CriteriaOperation operation, Class<T> clazz) {
		this.key = key;
		this.operation = operation;
		this.clazz = clazz;
	}
}