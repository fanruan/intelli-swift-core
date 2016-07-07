package com.finebi.datasource.api.metamodel;

import java.util.Map;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.criteria.CriteriaBuilder;

/**
 * This class created on 2016/7/1.
 *
 * @author Connery
 * @since 4.0
 */
public interface EntityManagerFactory {

    /**
     * Create a new application-managed <code>EntityManager</code>.
     * This method returns a new <code>EntityManager</code> instance each time
     * it is invoked.
     * The <code>isOpen</code> method will return true on the returned instance.
     *
     * @return entity manager instance
     * @throws IllegalStateException if the entity manager factory
     *                               has been closed
     */
    EntityTypeManager createEntityManager();

    /**
     * Create a new application-managed <code>EntityManager</code> with the
     * specified Map of properties.
     * This method returns a new <code>EntityManager</code> instance each time
     * it is invoked.
     * The <code>isOpen</code> method will return true on the returned instance.
     *
     * @param map properties for entity manager
     * @return entity manager instance
     * @throws IllegalStateException if the entity manager factory
     *                               has been closed
     */
    EntityTypeManager createEntityManager(Map map);


    /**
     * Return an instance of <code>CriteriaBuilder</code> for the creation of
     * <code>CriteriaQuery</code> objects.
     *
     * @return CriteriaBuilder instance
     * @throws IllegalStateException if the entity manager factory
     *                               has been closed
     * @since Java Persistence 2.0
     */
    public CriteriaBuilder getCriteriaBuilder();

    /**
     * Return an instance of <code>Metamodel</code> interface for access to the
     * metamodel of the persistence unit.
     *
     * @return Metamodel instance
     * @throws IllegalStateException if the entity manager factory
     *                               has been closed
     * @since Java Persistence 2.0
     */
    public Metamodel getMetamodel();

    /**
     * Indicates whether the factory is open. Returns true
     * until the factory has been closed.
     *
     * @return boolean indicating whether the factory is open
     */
    public boolean isOpen();

    /**
     * Close the factory, releasing any resources that it holds.
     * After a factory instance has been closed, all methods invoked
     * on it will throw the <code>IllegalStateException</code>, except
     * for <code>isOpen</code>, which will return false. Once an
     * <code>EntityManagerFactory</code> has been closed, all its
     * entity managers are considered to be in the closed state.
     *
     * @throws IllegalStateException if the entity manager factory
     *                               has been closed
     */
    public void close();

    /**
     * Get the properties and associated values that are in effect
     * for the entity manager factory. Changing the contents of the
     * map does not change the configuration in effect.
     *
     * @return properties
     * @throws IllegalStateException if the entity manager factory
     *                               has been closed
     * @since Java Persistence 2.0
     */
    public Map<String, Object> getProperties();


    /**
     * Return an object of the specified type to allow access to the
     * provider-specific API. If the provider's EntityManagerFactory
     * implementation does not support the specified class, the
     * PersistenceException is thrown.
     *
     * @param cls the class of the object to be returned. This is
     *            normally either the underlying EntityManagerFactory
     *            implementation class or an interface that it implements.
     * @return an instance of the specified class
     * @throws PersistenceException if the provider does not
     *                              support the call
     * @since Java Persistence 2.1
     */
    public <T> T unwrap(Class<T> cls);


}