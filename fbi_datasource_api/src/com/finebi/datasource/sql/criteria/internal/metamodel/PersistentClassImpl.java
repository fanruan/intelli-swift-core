package com.finebi.datasource.sql.criteria.internal.metamodel;

/**
 * This class created on 2016/7/2.
 *
 * @author Connery
 * @since 4.0
 */
public class PersistentClassImpl implements EntityTypeProperty {
    private String entityName;
    private String sourceName;

    public PersistentClassImpl(String entityName, String sourceName) {
        this.entityName = entityName;
        this.sourceName = sourceName;
    }

    @Override
    public String getEntityName() {
        return entityName;
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
        return sourceName;
    }
}
