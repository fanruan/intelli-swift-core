package com.finebi.datasource.sql.criteria.internal.metamodel;

/**
 * This class created on 2016/7/2.
 *
 * @author Connery
 * @since 4.0
 */
public class PropertyImpl implements Property {
    private String name;
    private boolean optional;

    public PropertyImpl(String name, boolean optional) {
        this.name = name;
        this.optional = optional;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }
}
