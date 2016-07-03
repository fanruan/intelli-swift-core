package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.sql.criteria.DataSourceType;
import com.finebi.datasource.sql.criteria.AttributeType;

/**
 * This class created on 2016/7/2.
 *
 * @author Connery
 * @since 4.0
 */
public class AttributePropertyImpl implements AttributeProperty {
    private String name;
    private boolean optional;
    private AttributeType attributeType;
    private DataSourceType dataSourceType;

    public AttributePropertyImpl(String name, boolean optional, AttributeType attributeType) {
        this.name = name;
        this.optional = optional;
        this.attributeType = attributeType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    public AttributeType getAttributeType() {
        return attributeType;
    }
}
