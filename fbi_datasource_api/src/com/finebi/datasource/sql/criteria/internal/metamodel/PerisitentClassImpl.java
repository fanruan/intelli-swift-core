package com.finebi.datasource.sql.criteria.internal.metamodel;

/**
 * This class created on 2016/7/2.
 *
 * @author Connery
 * @since 4.0
 */
public class PerisitentClassImpl implements PersistentClass {
    @Override
    public String getEntityName() {
        return "entity";
    }

    @Override
    public Object getDeclaredIdentifierMapper() {
        return null;
    }

    @Override
    public boolean hasIdentifierProperty() {
        return false;
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @Override
    public String getJpaEntityName() {
        return "jpaEntity";
    }
}
