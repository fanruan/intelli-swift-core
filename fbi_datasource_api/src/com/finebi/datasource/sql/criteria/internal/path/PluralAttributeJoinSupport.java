
package com.finebi.datasource.sql.criteria.internal.path;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.api.criteria.JoinType;
import com.finebi.datasource.api.criteria.PluralJoin;
import com.finebi.datasource.api.criteria.Predicate;
import com.finebi.datasource.api.metamodel.Attribute;
import com.finebi.datasource.api.metamodel.ManagedType;
import com.finebi.datasource.api.metamodel.PluralAttribute;
import com.finebi.datasource.api.metamodel.Type;

import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.JoinImplementor;
import com.finebi.datasource.sql.criteria.internal.PathSource;

/**
 * Support for defining joins to plural attributes (JPA requires typing based on
 * the specific collection type so we cannot really implement all support in a
 * single class)
 *
 * @author Steve Ebersole
 */
public abstract class PluralAttributeJoinSupport<O,C,E>
		extends AbstractJoinImpl<O,E>
		implements PluralJoin<O,C,E> {

	public PluralAttributeJoinSupport(
			CriteriaBuilderImpl criteriaBuilder,
			Class<E> javaType,
			PathSource<O> pathSource,
			Attribute<? super O,?> joinAttribute,
			JoinType joinType) {
		super( criteriaBuilder, javaType, pathSource, joinAttribute, joinType );
	}

	@Override
	public PluralAttribute<? super O, C, E> getAttribute() {
		return (PluralAttribute<? super O, C, E>) super.getAttribute();
	}

	public PluralAttribute<? super O, C, E> getModel() {
		return getAttribute();
	}

	@Override
	protected ManagedType<E> locateManagedType() {
		return isBasicCollection()
				? null
				: (ManagedType<E>) getAttribute().getElementType();
	}

	public boolean isBasicCollection() {
		return Type.PersistenceType.BASIC.equals( getAttribute().getElementType().getPersistenceType() );
	}

	@Override
	protected boolean canBeDereferenced() {
		return !isBasicCollection();
	}

	@Override
	protected boolean canBeJoinSource() {
		return !isBasicCollection();
	}

	@Override
	public JoinImplementor<O, E> on(Predicate... restrictions) {
		return super.on( restrictions );
	}

	@Override
	public JoinImplementor<O, E> on(Expression<Boolean> restriction) {
		return super.on( restriction );
	}
}
