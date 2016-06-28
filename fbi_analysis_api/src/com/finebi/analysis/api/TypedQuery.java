
package com.finebi.analysis.api;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Interface used to control the execution of typed queries.
 * @param <X> query result type
 *
 * @see Query
 * @see Parameter
 *
 * @since Advanced FineBI Analysis 1.0
 */
public interface TypedQuery<X> extends Query {
	
    /**
     * Execute a SELECT query and return the query results
     * as a typed List.
     * @return a list of the results
     * @throws IllegalStateException if called for a Java
     *         Persistence query language UPDATE or DELETE statement
     * @throws QueryTimeoutException if the query execution exceeds
     *         the query timeout value set and only the statement is
     *         rolled back
     * @throws TransactionRequiredException if a lock mode other than
     *         <code>NONE</code> has been set and there is no transaction
     *         or the persistence context has not been joined to the
     *         transaction
     * @throws PessimisticLockException if pessimistic locking
     *         fails and the transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking
     *         fails and only the statement is rolled back
     * @throws PersistenceException if the query execution exceeds 
     *         the query timeout value set and the transaction 
     *         is rolled back 
     */
    List<X> getResultList();

    /**
     * Execute a SELECT query that returns a single result.
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
     *         or the persistence context has not been joined to the
     *         transaction
     * @throws PessimisticLockException if pessimistic locking
     *         fails and the transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking
     *         fails and only the statement is rolled back
     * @throws PersistenceException if the query execution exceeds 
     *         the query timeout value set and the transaction 
     *         is rolled back 
     */
    X getSingleResult();

    /**
     * Set the maximum number of results to retrieve.
     * @param maxResult  maximum number of results to retrieve
     * @return the same query instance
     * @throws IllegalArgumentException if the argument is negative
     */
    TypedQuery<X> setMaxResults(int maxResult);

    /**
     * Set the position of the first result to retrieve.
     * @param startPosition position of the first result, 
     *        numbered from 0
     * @return the same query instance
     * @throws IllegalArgumentException if the argument is negative
     */
    TypedQuery<X> setFirstResult(int startPosition);

    /**
     * Set a query property or hint. The hints elements may be used 
     * to specify query properties and hints. Properties defined by
     * this specification must be observed by the provider. 
     * Vendor-specific hints that are not recognized by a provider
     * must be silently ignored. Portable applications should not
     * rely on the standard timeout hint. Depending on the database
     * in use and the locking mechanisms used by the provider,
     * this hint may or may not be observed.
     * @param hintName  name of property or hint
     * @param value  value for the property or hint
     * @return the same query instance
     * @throws IllegalArgumentException if the second argument is not
     *         valid for the implementation
     */
    TypedQuery<X> setHint(String hintName, Object value);

    /**
     * Bind the value of a <code>Parameter</code> object.
     * @param param  parameter object
     * @param value  parameter value
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter
     *         does not correspond to a parameter of the
     *         query
     */
     <T> TypedQuery<X> setParameter(Parameter<T> param, T value);

    /**
     * Bind an instance of <code>java.util.Calendar</code> to a <code>Parameter</code> object.
     * @param param  parameter object
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter does not
     *         correspond to a parameter of the query
     */
    TypedQuery<X> setParameter(Parameter<Calendar> param,
                               Calendar value,
                               TemporalType temporalType);

    /**
     * Bind an instance of <code>java.util.Date</code> to a <code>Parameter</code> object.
     * @param param  parameter object
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter does not
     *         correspond to a parameter of the query
     */
    TypedQuery<X> setParameter(Parameter<Date> param, Date value,
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
    TypedQuery<X> setParameter(String name, Object value);

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
    TypedQuery<X> setParameter(String name, Calendar value,
                               TemporalType temporalType);

    /**
     * Bind an instance of <code>java.util.Date</code> to a named parameter.
     * @param name   parameter name
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter name does
     *         not correspond to a parameter of the query or if 
     *         the value argument is of incorrect type
     */
    TypedQuery<X> setParameter(String name, Date value,
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
    TypedQuery<X> setParameter(int position, Object value);

    /**
     * Bind an instance of <code>java.util.Calendar</code> to a positional
     * parameter.
     * @param position  position
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *         correspond to a positional parameter of the query
     *         or if the value argument is of incorrect type
     */
    TypedQuery<X> setParameter(int position, Calendar value,
                               TemporalType temporalType);

    /**
     * Bind an instance of <code>java.util.Date</code> to a positional parameter.
     * @param position  position
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *         correspond to a positional parameter of the query
     *         or if the value argument is of incorrect type
     */
    TypedQuery<X> setParameter(int position, Date value,
                               TemporalType temporalType);


}
