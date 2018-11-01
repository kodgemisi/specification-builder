package com.kodgemisi.specification;

/**
 * Created on October, 2018
 *
 * @author Destan Sarpkaya
 * @author Ersan Ceylan
 * @author Sedat Gokcen
 */
enum CriteriaOperation implements Operation {

	EQUAL,
	EQUAL_TO_MANY,
	EQUAL_TO_ONE,
	LIKE,
	IN,
	LESS_THAN,
	LESS_THAN_OR_EQUAL_TO,
	GREATER_THAN,
	GREATER_THAN_OR_EQUAL_TO

}