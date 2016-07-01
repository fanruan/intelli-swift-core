/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.expression;

import com.finebi.datasource.api.metamodel.PlainTable;
import com.finebi.datasource.sql.criteria.internal.AbstractNode;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.TupleElementImplementor;
import com.finebi.datasource.sql.criteria.internal.ValueHandlerFactory;

import java.io.Serializable;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public abstract class AbstractTupleElement<X>
		extends AbstractNode
		implements TupleElementImplementor<X>, Serializable {
	private final Class originalJavaType;
	private Class<X> javaType;
	private String alias;
	private ValueHandlerFactory.ValueHandler<X> valueHandler;

	protected AbstractTupleElement(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType) {
		super( criteriaBuilder );
		this.originalJavaType = javaType;
		this.javaType = javaType;
	}

	@Override
	public Class<X> getJavaType() {
		return javaType;
	}

    @Override
    public PlainTable getPlainTable() {
        return null;
    }

    @SuppressWarnings({ "unchecked" })
	protected void resetJavaType(Class targetType) {
		this.javaType = targetType;
//		this.valueHandler = javaType.equals( originalJavaType )
//				? null
//				: ValueHandlerFactory.determineAppropriateHandler( javaType );
		this.valueHandler = ValueHandlerFactory.determineAppropriateHandler( javaType );
	}

	protected void forceConversion(ValueHandlerFactory.ValueHandler<X> valueHandler) {
		this.valueHandler = valueHandler;
	}

	@Override
	public ValueHandlerFactory.ValueHandler<X> getValueHandler() {
		return valueHandler;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	/**
	 * Protected access to define the alias.
	 *
	 * @param alias The alias to use.
	 */
	protected void setAlias(String alias) {
		this.alias = alias;
	}
}
