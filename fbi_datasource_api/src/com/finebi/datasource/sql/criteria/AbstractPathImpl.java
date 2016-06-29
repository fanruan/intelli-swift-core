
package com.finebi.datasource.sql.criteria;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.api.criteria.Path;
import com.finebi.datasource.api.metamodel.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Convenience base class for various {@link Path} implementations.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractPathImpl<X>
        extends ExpressionImpl<X>
        implements Path<X>, Serializable {
    public AbstractPathImpl(CriteriaBuilderImpl criteriaBuilder,PlainTable plainTable) {
        super(criteriaBuilder, plainTable);
    }

    @Override
    public Bindable<X> getModel() {
        return null;
    }

    @Override
    public Path<?> getParentPath() {
        return null;
    }

    @Override
    public <Y> Path<Y> get(SingularAttribute<? super X, Y> attribute) {
        return null;
    }

    @Override
    public <E, C extends Collection<E>> Expression<C> get(PluralAttribute<X, C, E> collection) {
        return null;
    }

    @Override
    public <K, V, M extends Map<K, V>> Expression<M> get(MapAttribute<X, K, V> map) {
        return null;
    }

    @Override
    public Expression<Class<? extends X>> type() {
        return null;
    }

    @Override
    public <Y> Path<Y> get(String attributeName) {
        return null;
    }
}
