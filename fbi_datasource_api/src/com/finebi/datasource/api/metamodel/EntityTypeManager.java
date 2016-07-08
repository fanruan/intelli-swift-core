package com.finebi.datasource.api.metamodel;

import com.finebi.datasource.api.criteria.CriteriaBuilder;

/**
 * Interface used to interact with the persistence context.
 * 局部上下文交互的接口。
 * 在实际的计算中，用来获得所需要任意信息。
 *
 * @since Advanced FineBI Analysis 1.0
 */
public interface EntityTypeManager {

    /**
     * 获得属性
     *
     * @param name 属性名
     * @return 相应的属性
     */
    Object getProperties(String name);


    /**
     * 当前的Manager是否包含entityType
     *
     * @param entityType 目标
     * @return 是否包含
     */
    boolean contains(EntityType entityType);


    /**
     * Return the entity manager factory for the entity manager.
     *
     * @return EntityManagerFactory instance
     * @throws IllegalStateException if the entity manager has
     *                               been closed
     * @since Java Persistence 2.0
     */
    EntityTypeManagerFactory getEntityManagerFactory();

    /**
     * Return an instance of <code>CriteriaBuilder</code> for the creation of
     * <code>CriteriaQuery</code> objects.
     *
     * @return CriteriaBuilder instance
     * @throws IllegalStateException if the entity manager has
     *                               been closed
     * @since Java Persistence 2.0
     */
    CriteriaBuilder getCriteriaBuilder();

    /**
     * Return an instance of <code>Metamodel</code> interface for access to the
     * metamodel of the persistence unit.
     *
     * @return Metamodel instance
     * @throws IllegalStateException if the entity manager has
     *                               been closed
     * @since Java Persistence 2.0
     */
    Metamodel getMetamodel();


}