
package com.finebi.datasource.sql.criteria.internal;

import com.finebi.datasource.api.criteria.Path;
import com.finebi.datasource.api.metamodel.Attribute;

/**
 * Implementation contract for the JPA {@link Path} interface.
 *
 * @author Steve Ebersole
 */
public interface PathImplementor<X> extends ExpressionImplementor<X>, Path<X>, PathSource<X>, Renderable {
	/**
	 * Retrieve reference to the attribute this path represents.
	 *
	 * @return The metamodel attribute.
	 */
	public Attribute<?, ?> getAttribute();

}
