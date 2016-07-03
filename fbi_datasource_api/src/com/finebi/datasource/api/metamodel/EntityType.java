
package com.finebi.datasource.api.metamodel;

/**
 * 代表一个数据源实体类型
 *
 * @param <X> The represented entity type.
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface EntityType<X>
        extends IdentifiableType<X>, Bindable<X> {

    /**
     * Return the entity name.
     *
     * @return entity name
     */
    String getName();
}
