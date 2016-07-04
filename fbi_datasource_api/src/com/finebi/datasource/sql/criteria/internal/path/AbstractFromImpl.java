
package com.finebi.datasource.sql.criteria.internal.path;

import com.finebi.datasource.api.criteria.From;
import com.finebi.datasource.api.criteria.Join;
import com.finebi.datasource.api.criteria.JoinType;
import com.finebi.datasource.api.metamodel.Attribute;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.ManagedType;
import com.finebi.datasource.sql.criteria.internal.*;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Convenience base class for various {@link com.finebi.datasource.api.criteria.From} implementations.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractFromImpl<Z, X>
        extends AbstractPathImpl<X>
        implements From<Z, X>, FromImplementor<Z, X>, Serializable {

    public static final JoinType DEFAULT_JOIN_TYPE = JoinType.INNER;

    private Set<Join<X, ?>> joins;

    private EntityType<X> entityType;

    public AbstractFromImpl(CriteriaBuilderImpl criteriaBuilder, EntityType<X> entityType) {
        this(criteriaBuilder, entityType, null);
    }

    public AbstractFromImpl(CriteriaBuilderImpl criteriaBuilder, EntityType<X> entityType, PathSource pathSource) {
        super(criteriaBuilder, entityType.getJavaType(), pathSource);
        this.entityType = entityType;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public PathSource<Z> getPathSource() {
        return super.getPathSource();
    }

    @Override
    public String getPathIdentifier() {
        return getAlias();
    }

    @Override
    protected boolean canBeDereferenced() {
        return true;
    }

    @Override
    public void prepareAlias(RenderingContext renderingContext) {
        if (getAlias() == null) {
            if (isCorrelated()) {
                setAlias(getCorrelationParent().getAlias());
            } else {
                setAlias(renderingContext.generateAlias());
            }
        }
    }

    @Override
    public String renderProjection(RenderingContext renderingContext) {
        prepareAlias(renderingContext);
        return getAlias();
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return renderProjection(renderingContext);
    }

    @Override
    public Attribute<?, ?> getAttribute() {
        return null;
    }

    public From<?, Z> getParent() {
        return null;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    protected Attribute<X, ?> locateAttributeInternal(String name) {
        return (Attribute<X, ?>) locateManagedType().getAttribute(name);
    }

    @SuppressWarnings({"unchecked"})
    protected ManagedType<? super X> locateManagedType() {
        // by default, this should be the model
        return (ManagedType<? super X>) getModel();
    }


    // CORRELATION ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    // IMPL NOTE : another means from handling correlations is to create a series of
    //		specialized From implementations that represent the correlation roots.  While
    //		that may be cleaner code-wise, it is certainly means creating a lot of "extra"
    //		classes since we'd need one for each Subquery#correlate method

    private FromImplementor<Z, X> correlationParent;

    private JoinScope<X> joinScope = new BasicJoinScope();

    /**
     * Helper contract used to define who/what keeps track of joins and fetches made from this <tt>FROM</tt>.
     */
    public static interface JoinScope<X> extends Serializable {
        public void addJoin(Join<X, ?> join);

    }

    protected class BasicJoinScope implements JoinScope<X> {
        @Override
        public void addJoin(Join<X, ?> join) {
            if (joins == null) {
                joins = new LinkedHashSet<Join<X, ?>>();
            }
            joins.add(join);
        }


    }

    protected class CorrelationJoinScope implements JoinScope<X> {
        @Override
        public void addJoin(Join<X, ?> join) {
            if (joins == null) {
                joins = new LinkedHashSet<Join<X, ?>>();
            }
            joins.add(join);
        }

    }

    @Override
    public boolean isCorrelated() {
        return correlationParent != null;
    }

    @Override
    public FromImplementor<Z, X> getCorrelationParent() {
        if (correlationParent == null) {
            throw new IllegalStateException(
                    String.format(
                            "Criteria query From node [%s] is not part of a subquery correlation",
                            getPathIdentifier()
                    )
            );
        }
        return correlationParent;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public FromImplementor<Z, X> correlateTo(CriteriaSubqueryImpl subquery) {
        final FromImplementor<Z, X> correlationDelegate = createCorrelationDelegate();
        correlationDelegate.prepareCorrelationDelegate(this);
        return correlationDelegate;
    }

    protected abstract FromImplementor<Z, X> createCorrelationDelegate();

    @Override
    public void prepareCorrelationDelegate(FromImplementor<Z, X> parent) {
        this.joinScope = new CorrelationJoinScope();
        this.correlationParent = parent;
    }

    @Override
    public String getAlias() {
        return isCorrelated() ? getCorrelationParent().getAlias() : super.getAlias();
    }

    // JOINS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    protected abstract boolean canBeJoinSource();

    protected RuntimeException illegalJoin() {
        return new IllegalArgumentException(
                "Collection of values [" + getPathIdentifier() + "] cannot be source of a join"
        );
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Set<Join<X, ?>> getJoins() {
        return joins == null
                ? Collections.EMPTY_SET
                : joins;
    }


    @Override
    public Join join(EntityType entityType) {
        return join(entityType, DEFAULT_JOIN_TYPE);
    }


    @Override
    public <Y> Join<X, Y> join(EntityType<Y> entityType, JoinType jt) {
        if (!canBeJoinSource()) {
            throw illegalJoin();
        }

        Join<X, Y> join = constructJoin(entityType, jt);
        joinScope.addJoin(join);
        return join;
    }



    private <Y> JoinImplementor<X, Y> constructJoin(EntityType<Y> entityType, JoinType jt) {
        if (jt.equals(JoinType.RIGHT)) {
            throw new UnsupportedOperationException("RIGHT JOIN not supported");
        }

        return new SingularEntityJoin<X, Y>(
                criteriaBuilder(),
                entityType.getJavaType(),
                this,
                entityType,
                jt
        );
    }


    public EntityType<X> getEntityType() {
        return entityType;
    }


    protected boolean canBeFetchSource() {
        // the conditions should be the same...
        return canBeJoinSource();
    }

    protected RuntimeException illegalFetch() {
        return new IllegalArgumentException(
                "Collection of values [" + getPathIdentifier() + "] cannot be source of a fetch"
        );
    }


}
