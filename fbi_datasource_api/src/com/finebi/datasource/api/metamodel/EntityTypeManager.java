package com.finebi.datasource.api.metamodel;

import com.finebi.datasource.api.criteria.CriteriaBuilder;

import java.util.Map;

/**
 * Interface used to interact with the persistence context.
 * <p/>
 * <p> An <code>EntityManager</code> instance is associated with
 * a persistence context. A persistence context is a set of entity
 * instances in which for any persistent entity identity there is
 * a unique entity instance. Within the persistence context, the
 * entity instances and their lifecycle are managed.
 * The <code>EntityManager</code> API is used
 * to create and remove persistent entity instances, to find entities
 * by their primary key, and to query over entities.
 * <p/>
 * <p> The set of entities that can be managed by a given
 * <code>EntityManager</code> instance is defined by a persistence
 * unit. A persistence unit defines the set of all classes that are
 * related or grouped by the application, and which must be
 * colocated in their mapping to a single database.
 *
 * @see CriteriaQuery
 * @since Advanced FineBI Analysis 1.0
 */
public interface EntityTypeManager {


    /**
     * 从数据源里面更新一遍当前注册的全部的元数据
     */
    void flush();


    void refresh(EntityType entityType);


    /**
     * 清楚所有的数据
     */
    void clear();


    /**
     * Get the properties and hints and associated values that are in effect
     * for the entity manager. Changing the contents of the map does
     * not change the configuration in effect.
     *
     * @return map of properties and hints in effect for entity manager
     * @since Java Persistence 2.0
     */
    public Map<String, Object> getProperties();


    /**
     * @param entityType
     * @return
     */
    public boolean contains(EntityType entityType);


    /**
     * Close an application-managed entity manager.
     * After the close method has been invoked, all methods
     * on the <code>EntityManager</code> instance and any
     * <code>Query</code> and <code>TypedQuery</code>
     * objects obtained from it will throw the <code>IllegalStateException</code>
     * except for <code>getProperties</code>,
     * <code>getTransaction</code>, and <code>isOpen</code> (which will return false).
     * If this method is called when the entity manager is
     * associated with an active transaction, the persistence
     * context remains managed until the transaction completes.
     *
     * @throws IllegalStateException if the entity manager
     *                               is container-managed
     */
    public void close();

    /**
     * Determine whether the entity manager is open.
     *
     * @return true until the entity manager has been closed
     */
    public boolean isOpen();

    /**
     * Return the entity manager factory for the entity manager.
     *
     * @return EntityManagerFactory instance
     * @throws IllegalStateException if the entity manager has
     *                               been closed
     * @since Java Persistence 2.0
     */
    public EntityManagerFactory getEntityManagerFactory();

    /**
     * Return an instance of <code>CriteriaBuilder</code> for the creation of
     * <code>CriteriaQuery</code> objects.
     *
     * @return CriteriaBuilder instance
     * @throws IllegalStateException if the entity manager has
     *                               been closed
     * @since Java Persistence 2.0
     */
    public CriteriaBuilder getCriteriaBuilder();

    /**
     * Return an instance of <code>Metamodel</code> interface for access to the
     * metamodel of the persistence unit.
     *
     * @return Metamodel instance
     * @throws IllegalStateException if the entity manager has
     *                               been closed
     * @since Java Persistence 2.0
     */
    public Metamodel getMetamodel();


}