package com.finebi.datasource.api.metamodel;

import com.finebi.datasource.api.criteria.CriteriaBuilder;

import java.util.Map;

/**
 * Interface used to interact with the persistence context.
 *
 * <p> An <code>EntityManager</code> instance is associated with
 * a persistence context. A persistence context is a set of entity
 * instances in which for any persistent entity identity there is
 * a unique entity instance. Within the persistence context, the
 * entity instances and their lifecycle are managed.
 * The <code>EntityManager</code> API is used
 * to create and remove persistent entity instances, to find entities
 * by their primary key, and to query over entities.
 *
 * <p> The set of entities that can be managed by a given
 * <code>EntityManager</code> instance is defined by a persistence
 * unit. A persistence unit defines the set of all classes that are
 * related or grouped by the application, and which must be
 * colocated in their mapping to a single database.
 *
 * @see CriteriaQuery
 *
 * @since Advanced FineBI Analysis 1.0
 */
public interface EntityManager {

    /**
     * Make an instance managed and persistent.
     * @param entity  entity instance
     * @throws EntityExistsException if the entity already exists.
     * (If the entity already exists, the <code>EntityExistsException</code> may
     * be thrown when the persist operation is invoked, or the
     * <code>EntityExistsException</code> or another <code>PersistenceException</code> may be
     * thrown at flush or commit time.)
     * @throws IllegalArgumentException if the instance is not an
     *         entity
     * @throws TransactionRequiredException if invoked on a
     *         container-managed entity manager of type
     *         <code>PersistenceContextType.TRANSACTION</code> and there is
     *         no transaction
     */
    public void persist(Object entity);

    /**
     * Merge the state of the given entity into the
     * current persistence context.
     * @param entity  entity instance
     * @return the managed instance that the state was merged to
     * @throws IllegalArgumentException if instance is not an
     *         entity or is a removed entity
     * @throws TransactionRequiredException if invoked on a
     *         container-managed entity manager of type
     *         <code>PersistenceContextType.TRANSACTION</code> and there is
     *         no transaction
     */
    public <T> T merge(T entity);

    /**
     * Remove the entity instance.
     * @param entity  entity instance
     * @throws IllegalArgumentException if the instance is not an
     *         entity or is a detached entity
     * @throws TransactionRequiredException if invoked on a
     *         container-managed entity manager of type
     *         <code>PersistenceContextType.TRANSACTION</code> and there is
     *         no transaction
     */
    public void remove(Object entity);

    /**
     * Find by primary key.
     * Search for an entity of the specified class and primary key.
     * If the entity instance is contained in the persistence context,
     * it is returned from there.
     * @param entityClass  entity class
     * @param primaryKey  primary key
     * @return the found entity instance or null if the entity does
     *         not exist
     * @throws IllegalArgumentException if the first argument does
     *         not denote an entity type or the second argument is
     *         is not a valid type for that entitys primary key or
     *         is null
     */
    public <T> T find(Class<T> entityClass, Object primaryKey);

    /**
     * Find by primary key, using the specified properties.
     * Search for an entity of the specified class and primary key.
     * If the entity instance is contained in the persistence
     * context, it is returned from there.
     * If a vendor-specific property or hint is not recognized,
     * it is silently ignored.
     * @param entityClass  entity class
     * @param primaryKey   primary key
     * @param properties  standard and vendor-specific properties
     *        and hints
     * @return the found entity instance or null if the entity does
     *         not exist
     * @throws IllegalArgumentException if the first argument does
     *         not denote an entity type or the second argument is
     *         is not a valid type for that entitys primary key or
     *         is null
     * @since Java Persistence 2.0
     */
    public <T> T find(Class<T> entityClass, Object primaryKey,
                      Map<String, Object> properties);


    /**
     * Get an instance, whose state may be lazily fetched.
     * If the requested instance does not exist in the database,
     * the <code>EntityNotFoundException</code> is thrown when the instance
     * state is first accessed. (The persistence provider runtime is
     * permitted to throw the <code>EntityNotFoundException</code> when
     * <code>getReference</code> is called.)
     * The application should not expect that the instance state will
     * be available upon detachment, unless it was accessed by the
     * application while the entity manager was open.
     * @param entityClass  entity class
     * @param primaryKey  primary key
     * @return the found entity instance
     * @throws IllegalArgumentException if the first argument does
     *         not denote an entity type or the second argument is
     *         not a valid type for that entitys primary key or
     *         is null
     * @throws EntityNotFoundException if the entity state
     *         cannot be accessed
     */
    public <T> T getReference(Class<T> entityClass,
                              Object primaryKey);

    /**
     * Synchronize the persistence context to the
     * underlying database.
     * @throws TransactionRequiredException if there is
     *         no transaction
     * @throws PersistenceException if the flush fails
     */
    public void flush();


    /**
     * Refresh the state of the instance from the database,
     * overwriting changes made to the entity, if any.
     * @param entity  entity instance
     * @throws IllegalArgumentException if the instance is not
     *         an entity or the entity is not managed
     * @throws TransactionRequiredException if invoked on a
     *         container-managed entity manager of type
     *         <code>PersistenceContextType.TRANSACTION</code> and there is
     *         no transaction
     * @throws EntityNotFoundException if the entity no longer
     *         exists in the database
     */
    public void refresh(Object entity);

    /**
     * Refresh the state of the instance from the database, using
     * the specified properties, and overwriting changes made to
     * the entity, if any.
     * <p> If a vendor-specific property or hint is not recognized,
     * it is silently ignored.
     * @param entity  entity instance
     * @param properties  standard and vendor-specific properties
     *        and hints
     * @throws IllegalArgumentException if the instance is not
     *         an entity or the entity is not managed
     * @throws TransactionRequiredException if invoked on a
     *         container-managed entity manager of type
     *         <code>PersistenceContextType.TRANSACTION</code> and there is
     *         no transaction
     * @throws EntityNotFoundException if the entity no longer
     *         exists in the database
     * @since Java Persistence 2.0
     */
    public void refresh(Object entity,
                        Map<String, Object> properties);



    /**
     * Clear the persistence context, causing all managed
     * entities to become detached. Changes made to entities that
     * have not been flushed to the database will not be
     * persisted.
     */
    public void clear();

    /**
     * Remove the given entity from the persistence context, causing
     * a managed entity to become detached.  Unflushed changes made
     * to the entity if any (including removal of the entity),
     * will not be synchronized to the database.  Entities which
     * previously referenced the detached entity will continue to
     * reference it.
     * @param entity  entity instance
     * @throws IllegalArgumentException if the instance is not an
     *         entity
     * @since Java Persistence 2.0
     */
    public void detach(Object entity);

    /**
     * Check if the instance is a managed entity instance belonging
     * to the current persistence context.
     * @param entity  entity instance
     * @return boolean indicating if entity is in persistence context
     * @throws IllegalArgumentException if not an entity
     */
    public boolean contains(Object entity);

    /**
     * Set an entity manager property or hint.
     * If a vendor-specific property or hint is not recognized, it is
     * silently ignored.
     * @param propertyName name of property or hint
     * @param value  value for property or hint
     * @throws IllegalArgumentException if the second argument is
     *         not valid for the implementation
     * @since Java Persistence 2.0
     */
    public void setProperty(String propertyName, Object value);

    /**
     * Get the properties and hints and associated values that are in effect
     * for the entity manager. Changing the contents of the map does
     * not change the configuration in effect.
     * @return map of properties and hints in effect for entity manager
     * @since Java Persistence 2.0
     */
    public Map<String, Object> getProperties();


    /**
     * Indicate to the entity manager that a JTA transaction is
     * active. This method should be called on a JTA application
     * managed entity manager that was created outside the scope
     * of the active transaction to associate it with the current
     * JTA transaction.
     * @throws TransactionRequiredException if there is
     *         no transaction
     */
    public void joinTransaction();

    /**
     * Determine whether the entity manager is joined to the
     * current transaction. Returns false if the entity manager
     * is not joined to the current transaction or if no
     * transaction is active
     * @return boolean
     */
    public boolean isJoinedToTransaction();

    /**
     * Return an object of the specified type to allow access to the
     * provider-specific API.   If the provider's <code>EntityManager</code>
     * implementation does not support the specified class, the
     * <code>PersistenceException</code> is thrown.
     * @param cls  the class of the object to be returned.  This is
     * normally either the underlying <code>EntityManager</code> implementation
     * class or an interface that it implements.
     * @return an instance of the specified class
     * @throws PersistenceException if the provider does not
     *         support the call
     * @since Java Persistence 2.0
     */
    public <T> T unwrap(Class<T> cls);

    /**
     * Return the underlying provider object for the <code>EntityManager</code>,
     * if available. The result of this method is implementation
     * specific. The <code>unwrap</code> method is to be preferred for new
     * applications.
     * @return underlying provider object for EntityManager
     */
    public Object getDelegate();

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
     * @throws IllegalStateException if the entity manager
     *         is container-managed
     */
    public void close();

    /**
     * Determine whether the entity manager is open.
     * @return true until the entity manager has been closed
     */
    public boolean isOpen();

    /**
     * Return the entity manager factory for the entity manager.
     * @return EntityManagerFactory instance
     * @throws IllegalStateException if the entity manager has
     *         been closed
     * @since Java Persistence 2.0
     */
    public EntityManagerFactory getEntityManagerFactory();

    /**
     * Return an instance of <code>CriteriaBuilder</code> for the creation of
     * <code>CriteriaQuery</code> objects.
     * @return CriteriaBuilder instance
     * @throws IllegalStateException if the entity manager has
     *         been closed
     * @since Java Persistence 2.0
     */
    public CriteriaBuilder getCriteriaBuilder();

    /**
     * Return an instance of <code>Metamodel</code> interface for access to the
     * metamodel of the persistence unit.
     * @return Metamodel instance
     * @throws IllegalStateException if the entity manager has
     *         been closed
     * @since Java Persistence 2.0
     */
    public Metamodel getMetamodel();




}