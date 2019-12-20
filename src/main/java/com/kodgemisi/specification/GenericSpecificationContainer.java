package com.kodgemisi.specification;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter

@SuppressWarnings({"rawtypes", "unchecked"})
public class GenericSpecificationContainer<E> implements Specification<E> {

	private final Specification specification;

	private final List<Map<String, String>> parameters;

	GenericSpecificationContainer(Specification specification, List<Map<String, String>> parameters) {
		this.specification = specification;
		this.parameters = Collections.unmodifiableList(parameters);
	}

	GenericSpecificationContainer(Specification specification) {
		this.specification = specification;
		this.parameters = Collections.emptyList();
	}

	public static GenericSpecificationContainer where(Specification specification) {
		return new GenericSpecificationContainer(specification);
	}

//	@Override
//	public Specification<E> and(Specification<E> other) {
//		return null;
//	}

	public GenericSpecificationContainer<E> or(GenericSpecificationContainer<E> spec) {
		final ArrayList<Map<String, String>> parameterList = new ArrayList<>(this.parameters);
		parameterList.addAll(spec.parameters);
		return new GenericSpecificationContainer<>(Specification.where(this.specification).or(spec), parameterList);
	}

	@Override
	public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
		return this.specification.toPredicate(root, query, criteriaBuilder);
	}

	public boolean hasParameters() {
		return !parameters.isEmpty();
	}

	public List<Map<String, String>> getParameters() {
		return parameters;
	}
}