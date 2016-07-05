
package com.finebi.datasource.sql.criteria.internal.path;

import com.finebi.datasource.api.criteria.Root;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.CriteriaSubqueryImpl;
import com.finebi.datasource.sql.criteria.internal.FromImplementor;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;

/**
 * Hibernate implementation of the JPA {@link Root} contract
 *
 * @author Steve Ebersole
 */
public class RootImpl<X> extends AbstractFromImpl<X, X> implements Root<X>, Serializable {
    private final boolean allowJoins;

    public RootImpl(CriteriaBuilderImpl criteriaBuilder, EntityType<X> entityType) {
        this(criteriaBuilder, entityType, true);
    }


    public RootImpl(CriteriaBuilderImpl criteriaBuilder, EntityType<X> entityType, boolean allowJoins) {
        super(criteriaBuilder, entityType);
        this.allowJoins = allowJoins;
    }


    public EntityType<X> getModel() {
        return getEntityType();
    }

    @Override
    protected FromImplementor<X, X> createCorrelationDelegate() {
        return new RootImpl<X>(criteriaBuilder(), getEntityType());
    }

    @Override
    public RootImpl<X> correlateTo(CriteriaSubqueryImpl subquery) {
        return (RootImpl<X>) super.correlateTo(subquery);
    }

    @Override
    protected boolean canBeJoinSource() {
        return allowJoins;
    }

    @Override
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    protected RuntimeException illegalJoin() {
        return allowJoins ? super.illegalJoin() : new IllegalArgumentException("UPDATE/DELETE criteria queries cannot define joins");
    }

    @Override
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    protected RuntimeException illegalFetch() {
        return allowJoins ? super.illegalFetch() : new IllegalArgumentException("UPDATE/DELETE criteria queries cannot define fetches");
    }

    public String renderTableExpression(RenderingContext renderingContext) {
        prepareAlias(renderingContext);
        return getModel().getName() + " as " + getAlias();
    }

    @Override
    public String getPathIdentifier() {
        return getAlias();
    }

    @Override
    public Object render(RenderingContext renderingContext) {
        return delegateRender(renderingContext);
    }

    @Override
    protected RenderExtended choseRender(RenderingContext renderingContext) {
        return (RenderExtended)renderingContext.getRenderFactory().getRootRender(this, "d");
    }

    @Override
    public Object renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }


}
