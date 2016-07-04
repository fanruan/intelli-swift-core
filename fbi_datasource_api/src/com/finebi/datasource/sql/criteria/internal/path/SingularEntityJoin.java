
package com.finebi.datasource.sql.criteria.internal.path;

import com.finebi.datasource.api.criteria.JoinType;
import com.finebi.datasource.api.metamodel.Bindable;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.ManagedType;
import com.finebi.datasource.api.metamodel.SingularAttribute;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.CriteriaSubqueryImpl;
import com.finebi.datasource.sql.criteria.internal.FromImplementor;
import com.finebi.datasource.sql.criteria.internal.PathSource;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

/**
 * Models a join based on a singular attribute
 *
 * @param <O> Represents the parameterized type of the attribute owner
 * @param <X> Represents the parameterized type of the attribute
 * @author Steve Ebersole
 */
public class SingularEntityJoin<O, X> extends AbstractJoinImpl<O, X> {
    private final Bindable<X> model;

    @SuppressWarnings({"unchecked"})
    public SingularEntityJoin(
            CriteriaBuilderImpl criteriaBuilder,
            Class<X> javaType,
            PathSource<O> pathSource,
            EntityType<X> entityType,
            JoinType joinType) {
        super(criteriaBuilder, javaType, pathSource, entityType, joinType);
        this.model = entityType;
    }

    @Override
    public SingularAttribute<? super O, ?> getAttribute() {
        return (SingularAttribute<? super O, ?>) super.getAttribute();
    }

    @Override
    public SingularEntityJoin<O, X> correlateTo(CriteriaSubqueryImpl subquery) {
        return (SingularEntityJoin<O, X>) super.correlateTo(subquery);
    }

    @Override
    protected FromImplementor<O, X> createCorrelationDelegate() {
//        return new SingularEntityJoin<O, X>(
//                criteriaBuilder(),
//                getJavaType(),
//                getPathSource(),
//                getAttribute(),
//                getJoinType()
//        );
        return null;
    }

    @Override
    protected boolean canBeJoinSource() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ManagedType<? super X> locateManagedType() {
        if (getModel().getBindableType() == Bindable.BindableType.ENTITY_TYPE) {
            return (ManagedType<? super X>) getModel();
        } else if (getModel().getBindableType() == Bindable.BindableType.SINGULAR_ATTRIBUTE) {
//			final Type joinedAttributeType = ( (SingularAttribute) getAttribute() ).getType();
//			if ( !ManagedType.class.isInstance( joinedAttributeType ) ) {
//				throw new UnsupportedOperationException(
//						"Cannot further dereference attribute join [" + getPathIdentifier() + "] as its type is not a ManagedType"
//				);
//			}
//			return (ManagedType<? super X>) joinedAttributeType;
        } else if (getModel().getBindableType() == Bindable.BindableType.PLURAL_ATTRIBUTE) {
            throw new UnsupportedOperationException();
        }

        return super.locateManagedType();
    }

    public Bindable<X> getModel() {
        return model;
    }

    @Override
    public <T extends X> SingularEntityJoin<O, T> treatAs(Class<T> treatAsType) {
        return new TreatedSingularAttributeJoin<O, T>(this, treatAsType);
    }

    public static class TreatedSingularAttributeJoin<O, T> extends SingularEntityJoin<O, T> {
        private final SingularEntityJoin<O, ? super T> original;
        private final Class<T> treatAsType;

        public TreatedSingularAttributeJoin(SingularEntityJoin<O, ? super T> original, Class<T> treatAsType) {
            super(
                    original.criteriaBuilder(),
                    treatAsType,
                    original.getPathSource(),
                    null,
                    original.getJoinType()
            );
            this.original = original;
            this.treatAsType = treatAsType;
        }

        @Override
        public String getAlias() {
            return original.getAlias();
        }

        @Override
        public void prepareAlias(RenderingContext renderingContext) {
            // do nothing...
        }

        @Override
        public String render(RenderingContext renderingContext) {
            return "treat(" + original.render(renderingContext) + " as " + treatAsType.getName() + ")";
        }
    }
}
