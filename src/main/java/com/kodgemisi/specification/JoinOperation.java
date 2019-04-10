package com.kodgemisi.specification;

import javax.persistence.criteria.JoinType;

/**
 * Created on November, 2018
 *
 * @author Sedat Gokcen
 */
public enum JoinOperation implements Operation {
    JOIN,
    JOIN_FETCH;

    private JoinType type = JoinType.INNER;

    public JoinOperation type(JoinType type) {
        this.type = type;
        return this;
    }

    public JoinType getType() {
        return type;
    }
}