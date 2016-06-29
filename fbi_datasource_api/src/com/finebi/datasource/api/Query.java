
package com.finebi.datasource.api;

import java.util.*;

/**
 * Interface used to control query execution.
 *
 * @see TypedQuery
 * @see StoredProcedureQuery
 * @see Parameter
 *
 * @since Java Persistence 1.0
 */
public interface Query {

    /**
     * Execute a SELECT query and return the query results
     * as an untyped List.
     * @return a list of the results
     * @throws IllegalStateException if called for a Java
     *         Persistence query language UPDATE or DELETE statement
     * @throws QueryTimeoutException if the query execution exceeds
     *         the query timeout value set and only the statement is
     *         rolled back
     * @throws TransactionRequiredException if a lock mode other than
     *         <code>NONE</code> has been set and there is no transaction
     *         or the persistence context has not been joined to the transaction
     * @throws PessimisticLockException if pessimistic locking
     *         fails and the transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking
     *         fails and only the statement is rolled back
     * @throws PersistenceException if the query execution exceeds 
     *         the query timeout value set and the transaction 
     *         is rolled back 
     */
    List getResultList();

    /**
     * Execute a SELECT query that returns a single untyped result.
     * @return the result
     * @throws NoResultException if there is no result
     * @throws NonUniqueResultException if more than one result
     * @throws IllegalStateException if called for a Java
     *         Persistence query language UPDATE or DELETE statement
     * @throws QueryTimeoutException if the query execution exceeds
     *         the query timeout value set and only the statement is
     *         rolled back
     * @throws TransactionRequiredException if a lock mode other than
     *         <code>NONE</code> has been set and there is no transaction
     *         or the persistence context has not been joined to the transaction
     * @throws PessimisticLockException if pessimistic locking
     *         fails and the transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking
     *         fails and only the statement is rolled back
     * @throws PersistenceException if the query execution exceeds 
     *         the query timeout value set and the transaction 
     *         is rolled back 
     */
    Object getSingleResult();

    /**
     * Execute an update or delete statement.
     * @return the number of entities updated or deleted
     * @throws IllegalStateException if called for a Java
     *         Persistence query language SELECT statement or for
     *         a criteria query
     * @throws TransactionRequiredException if there is 
     *         no transaction or the persistence context has not
     *         been joined to the transaction
     * @throws QueryTimeoutException if the statement execution 
     *         exceeds the query timeout value set and only 
     *         the statement is rolled back
     * @throws PersistenceException if the query execution exceeds 
     *         the query timeout value set and the transaction 
     *         is rolled back 
     */
    int executeUpdate();

    /**
     * Set the maximum number of results to retrieve.
     * @param maxResult  maximum number of results to retrieve
     * @return the same query instance
     * @throws IllegalArgumentException if the argument is negative
     */
    Query setMaxResults(int maxResult);

    /**
     * The maximum number of results the query object was set to
     * retrieve. Returns <code>Integer.MAX_VALUE</code> if <code>setMaxResults</code> was not
     * applied to the query object.
     * @return maximum number of results
     * @since Advanced FineBI Analysis 1.0
     */
    int getMaxResults();

    /**
     * Set the position of the first result to retrieve.
     * @param startPosition position of the first result, 
     * numbered from 0
     * @return the same query instance
     * @throws IllegalArgumentException if the argument is negative
     */
    Query setFirstResult(int startPosition);

    /**
     * The position of the first result the query object was set to
     * retrieve. Returns 0 if <code>setFirstResult</code> was not applied to the
     * query object.
     * @return position of the first result
     * @since Advanced FineBI Analysis 1.0
     */
    int getFirstResult();

    /**
     * Set a query property or hint. The hints elements may be used 
     * to specify query properties and hints. Properties defined by
     * this specification must be observed by the provider. 
     * Vendor-specific hints that are not recognized by a provider
     * must be silently ignored. Portable applications should not
     * rely on the standard timeout hint. Depending on the database
     * in use and the locking mechanisms used by the provider,
     * this hint may or may not be observed.
     * @param hintName  name of the property or hint
     * @param value  value for the property or hint
     * @return the same query instance
     * @throws IllegalArgumentException if the second argument is not
     *         valid for the implementation
     */
    Query setHint(String hintName, Object value);

    /**
     * Get the properties and hints and associated values that are 
     * in effect for the query instance.
     * @return query properties and hints
     * @since Advanced FineBI Analysis 1.0
     */
    Map<String, Object> getHints();

    /**
     * Bind the value of a <code>Parameter</code> object.
     * @param param  parameter object
     * @param value  parameter value
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter
     *         does not correspond to a parameter of the
     *         query
     * @since Advanced FineBI Analysis 1.0
     */
    <T> Query setParameter(Parameter<T> param, T value);

    /**
     * Bind an instance of <code>java.util.Calendar</code> to a <code>Parameter</code> object.
     * @param param parameter object
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter does not
     *         correspond to a parameter of the query
     * @since Advanced FineBI Analysis 1.0
     */
    Query setParameter(Parameter<Calendar> param, Calendar value,
                       TemporalType temporalType);

    /**
     * Bind an instance of <code>java.util.Date</code> to a <code>Parameter</code> object.
     * @param param parameter object
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter does not
     *         correspond to a parameter of the query
     * @since Advanced FineBI Analysis 1.0
     */
    Query setParameter(Parameter<Date> param, Date value,
                       TemporalType temporalType);

    /**
     * Bind an argument value to a named parameter.
     * @param name  parameter name
     * @param value  parameter value
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter name does 
     *         not correspond to a parameter of the query or if
     *         the argument is of incorrect type
     */
    Query setParameter(String name, Object value);

    /**
     * Bind an instance of <code>java.util.Calendar</code> to a named parameter.
     * @param name  parameter name
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter name does 
     *         not correspond to a parameter of the query or if
     *         the value argument is of incorrect type
     */
    Query setParameter(String name, Calendar value,
                       TemporalType temporalType);

    /**
     * Bind an instance of <code>java.util.Date</code> to a named parameter.
     * @param name  parameter name
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter name does 
     *         not correspond to a parameter of the query or if
     *         the value argument is of incorrect type
     */
    Query setParameter(String name, Date value,
                       TemporalType temporalType);

    /**
     * Bind an argument value to a positional parameter.
     * @param position  position
     * @param value  parameter value
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *         correspond to a positional parameter of the
     *         query or if the argument is of incorrect type
     */
    Query setParameter(int position, Object value);

    /**
     * Bind an instance of <code>java.util.Calendar</code> to a positional
     * parameter.
     * @param position  position
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *         correspond to a positional parameter of the query or
     *         if the value argument is of incorrect type
     */
    Query setParameter(int position, Calendar value,
                       TemporalType temporalType);

    /**
     * Bind an instance of <code>java.util.Date</code> to a positional parameter.
     * @param position  position
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *         correspond to a positional parameter of the query or
     *         if the value argument is of incorrect type
     */
    Query setParameter(int position, Date value,
                       TemporalType temporalType);

    /**
     * Get the parameter objects corresponding to the declared
     * parameters of the query.
     * Returns empty set if the query has no parameters.
     * This method is not required to be supported for native
     * queries.
     * @return set of the parameter objects
     * @throws IllegalStateException if invoked on a native
     *         query when the implementation does not support 
     *         this use
     * @since Advanced FineBI Analysis 1.0
     */
    Set<Parameter<?>> getParameters();

    /**
     * Get the parameter object corresponding to the declared
     * parameter of the given name.
     * This method is not required to be supported for native
     * queries.
     * @param name  parameter name
     * @return parameter object
     * @throws IllegalArgumentException if the parameter of the
     *         specified name does not exist
     * @throws IllegalStateException if invoked on a native
     *         query when the implementation does not support 
     *         this use
     * @since Advanced FineBI Analysis 1.0
     */
    Parameter<?> getParameter(String name);

    /**
     * Get the parameter object corresponding to the declared
     * parameter of the given name and type.
     * This method is required to be supported for criteria queries
     * only.
     * @param name  parameter name
     * @param type  type
     * @return parameter object
     * @throws IllegalArgumentException if the parameter of the
     *         specified name does not exist or is not assignable
     *         to the type
     * @throws IllegalStateException if invoked on a native
     *         query or Java Persistence query language query when
     *         the implementation does not support this use
     * @since Advanced FineBI Analysis 1.0
     */
    <T> Parameter<T> getParameter(String name, Class<T> type);

    /**
     * Get the parameter object corresponding to the declared
     * positional parameter with the given position.
     * This method is not required to be supported for native
     * queries.
     * @param position  position
     * @return parameter object
     * @throws IllegalArgumentException if the parameter with the
     *         specified position does not exist
     * @throws IllegalStateException if invoked on a native
     *         query when the implementation does not support 
     *         this use
     * @since Advanced FineBI Analysis 1.0
     */
    Parameter<?> getParameter(int position);

    /**
     * Get the parameter object corresponding to the declared
     * positional parameter with the given position and type.
     * This method is not required to be supported by the provider.
     * @param position  position
     * @param type  type
     * @return parameter object
     * @throws IllegalArgumentException if the parameter with the
     *         specified position does not exist or is not assignable
     *         to the type
     * @throws IllegalStateException if invoked on a native
     *         query or Java Persistence query language query when
     *         the implementation does not support this use
     * @since Advanced FineBI Analysis 1.0
     */
    <T> Parameter<T> getParameter(int position, Class<T> type);

    /**
     * Return a boolean indicating whether a value has been bound 
     * to the parameter.
     * @param param parameter object
     * @return boolean indicating whether parameter has been bound
     * @since Advanced FineBI Analysis 1.0
     */
    boolean isBound(Parameter<?> param);

    /**
     * Return the input value bound to the parameter.
     * (Note that OUT parameters are unbound.)
     * @param param parameter object
     * @return parameter value
     * @throws IllegalArgumentException if the parameter is not 
     *         a parameter of the query
     * @throws IllegalStateException if the parameter has not been
     *         been bound
     * @since Advanced FineBI Analysis 1.0
     */
    <T> T getParameterValue(Parameter<T> param);

    /**
     * Return the input value bound to the named parameter.
     * (Note that OUT parameters are unbound.)
     * @param name  parameter name
     * @return parameter value
     * @throws IllegalStateException if the parameter has not been
     *         been bound
     * @throws IllegalArgumentException if the parameter of the
     *         specified name does not exist
     * @since Advanced FineBI Analysis 1.0
     */
    Object getParameterValue(String name);

    /**
     * Return the input value bound to the positional parameter.
     * (Note that OUT parameters are unbound.)
     * @param position  position
     * @return parameter value
     * @throws IllegalStateException if the parameter has not been
     *         been bound
     * @throws IllegalArgumentException if the parameter with the
     *         specified position does not exist
     * @since Advanced FineBI Analysis 1.0
     */
    Object getParameterValue(int position);




    /**
     * Return an object of the specified type to allow access to 
     * the provider-specific API.  If the provider's query 
     * implementation does not support the specified class, the 
     * <code>PersistenceException</code> is thrown.
     * @param cls  the class of the object to be returned.  This is
     *             normally either the underlying query 
     *             implementation class or an interface that it 
     *             implements.
     * @return an instance of the specified class
     * @throws PersistenceException if the provider does not support
     *         the call
     * @since Advanced FineBI Analysis 1.0
     */
    <T> T unwrap(Class<T> cls);
}
