package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.sql.criteria.AttributeType;

/**
 * This class created on 2016/7/1.
 *
 * @author Connery
 * @since 4.0
 */
public interface AttributeProperty<T> {
    String getName();

    boolean isOptional();

    AttributeType<T> getAttributeType();

}
