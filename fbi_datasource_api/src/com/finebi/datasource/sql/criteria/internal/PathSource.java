
package com.finebi.datasource.sql.criteria.internal;
import com.finebi.datasource.api.criteria.Path;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

/**
 * Implementation contract for things which can be the source (parent, left-hand-side, etc) of a path
 *
 * @author Steve Ebersole
 */
public interface PathSource<X> extends Path<X> {
	public void prepareAlias(RenderingContext renderingContext);

	/**
	 * Get the string representation of this path as a navigation from one of the
	 * queries <tt>identification variables</tt>
	 *
	 * @return The path's identifier.
	 */
	public String getPathIdentifier();
}
