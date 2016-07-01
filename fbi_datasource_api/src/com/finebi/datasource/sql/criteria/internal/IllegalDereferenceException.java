
package com.finebi.datasource.sql.criteria.internal;


/**
 * Represents an illegal attempt to dereference from a {@link #getPathSource() path source} which
 * cannot be dereferenced.
 *
 * @author Steve Ebersole
 */
public class IllegalDereferenceException extends RuntimeException {
	private final PathSource pathSource;

	public IllegalDereferenceException(PathSource pathSource) {
		super( "Illegal attempt to dereference path source [" + pathSource.getPathIdentifier() + "]" );
		this.pathSource = pathSource;
	}

	public PathSource getPathSource() {
		return pathSource;
	}
}
