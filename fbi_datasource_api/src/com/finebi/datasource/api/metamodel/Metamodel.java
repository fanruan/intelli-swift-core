
package com.finebi.datasource.api.metamodel;

import com.finebi.datasource.sql.criteria.internal.metamodel.MetamodelImpl;

import java.util.Set;

/**
 * Provides access to the metamodel of persistent
 * entities in the persistence unit.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface Metamodel {


    MetamodelImpl.Builder build();

    /**
     * 当前的model不再可变
     */
    void close();

    /**
     * 判断当前的Model是否不可变
     *
     * @return
     */
    boolean isImmutable();

    /**
     * 返回保存的全部实体类型
     *
     * @return 保存的全部实体类型
     */
    Set<EntityType> getEntities();


    /**
     * Access to an entity supporting Hibernate's entity-name feature
     *
     * @param entityName The entity-name
     * @return The entity descriptor
     */
    EntityType entity(String entityName);


    boolean contain(EntityType entityType);

    boolean contain(String entityTypeName);

    /**
     * Get the names of all entities known to this Metamodel
     *
     * @return All of the entity names
     */
    String[] getAllEntityNames();


}
