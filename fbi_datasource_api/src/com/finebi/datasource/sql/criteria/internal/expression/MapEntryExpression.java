/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.expression;

import java.io.Serializable;
import java.util.Map;
import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.api.metamodel.MapAttribute;

import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.PathImplementor;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class MapEntryExpression<K,V>
		extends ExpressionImpl<Map.Entry<K,V>>
		implements Expression<Map.Entry<K,V>>, Serializable {

	private final PathImplementor origin;
	private final MapAttribute<?, K, V> attribute;

	public MapEntryExpression(
			CriteriaBuilderImpl criteriaBuilder,
			Class<Map.Entry<K, V>> javaType,
			PathImplementor origin,
			MapAttribute<?, K, V> attribute) {
		super( criteriaBuilder, javaType);
		this.origin = origin;
		this.attribute = attribute;
	}

	public MapAttribute<?, K, V> getAttribute() {
		return attribute;
	}

	public void registerParameters(ParameterRegistry registry) {
		// none to register
	}

	public String render(RenderingContext renderingContext) {
		// don't think this is valid outside of select clause...
		throw new IllegalStateException( "illegal reference to map entry outside of select clause." );
	}

	public String renderProjection(RenderingContext renderingContext) {
		return "entry(" + path( renderingContext ) + ")";
	}

	private String path(RenderingContext renderingContext) {
		return origin.getPathIdentifier()
				+ '.'
				+ ( (Renderable) getAttribute() ).renderProjection( renderingContext );
	}
}
