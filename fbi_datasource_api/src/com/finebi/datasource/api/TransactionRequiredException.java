
package com.finebi.datasource.api;

/**
 * Thrown by the persistence provider when a transaction is required but is not
 * active.
 *
 * @since Advanced FineBI Analysis 1.0
 */
public class TransactionRequiredException extends RuntimeException {

	/**
	 * Constructs a new <code>TransactionRequiredException</code> exception with
	 * <code>null</code> as its detail message.
	 */
	public TransactionRequiredException() {
		super();
	}

	/**
	 * Constructs a new <code>TransactionRequiredException</code> exception with
	 * the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public TransactionRequiredException(String message) {
		super(message);
	}
}
