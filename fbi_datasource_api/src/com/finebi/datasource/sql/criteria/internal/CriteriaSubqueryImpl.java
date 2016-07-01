/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal;

import com.finebi.datasource.api.criteria.*;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.api.metamodel.PlainTable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.DelegatedExpressionImpl;
import com.finebi.datasource.sql.criteria.internal.expression.ExpressionImpl;
import com.finebi.datasource.sql.criteria.internal.path.RootImpl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * The Hibernate implementation of the JPA {@link Subquery} contract.  Mostlty a set of delegation to its internal
 * {@link QueryStructure}.
 *
 * @author Steve Ebersole
 */
public class CriteriaSubqueryImpl<T> extends ExpressionImpl<T> implements Subquery<T>, Serializable {
    private final CommonAbstractCriteria parent;
    private final QueryStructure<T> queryStructure;

    public CriteriaSubqueryImpl(
            CriteriaBuilderImpl criteriaBuilder,
            Class<T> javaType,
            CommonAbstractCriteria parent) {
        super(criteriaBuilder, javaType);
        this.parent = parent;
        this.queryStructure = new QueryStructure<T>(this, criteriaBuilder);
    }

    @Override
    public <X> Root<X> from(PlainTable plainTable) {
        return null;
    }


    @Override
    public AbstractQuery<?> getParent() {
        if (!AbstractQuery.class.isInstance(parent)) {
            throw new IllegalStateException("Cannot call getParent on update/delete criterias");
        }
        return (AbstractQuery<?>) parent;
    }

    @Override
    public CommonAbstractCriteria getContainingQuery() {
        return parent;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        for (ParameterExpression param : queryStructure.getParameters()) {
            registry.registerParameter(param);
        }
    }

    @Override
    public Class<T> getResultType() {
        return null;
    }


    // ROOTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public Set<Root<?>> getRoots() {
        return queryStructure.getRoots();
    }

    @Override
    public <X> Root<X> from(EntityType<X> entityType) {
        return queryStructure.from(entityType);
    }

    @Override
    public <X> Root<X> from(Class<X> entityClass) {
        return queryStructure.from(entityClass);
    }


    // SELECTION ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public Subquery<T> distinct(boolean applyDistinction) {
        queryStructure.setDistinct(applyDistinction);
        return this;
    }

    @Override
    public boolean isDistinct() {
        return queryStructure.isDistinct();
    }

    private Expression<T> wrappedSelection;

    @Override
    public Expression<T> getSelection() {
        if (wrappedSelection == null) {
            if (queryStructure.getSelection() == null) {
                return null;
            }
            wrappedSelection = new SubquerySelection<T>((ExpressionImpl<T>) queryStructure.getSelection(), this);
        }
        return wrappedSelection;
    }

    @Override
    public Subquery<T> select(Expression<T> expression) {
        queryStructure.setSelection(expression);
        return this;
    }


    public static class SubquerySelection<S> extends DelegatedExpressionImpl<S> {
        private final CriteriaSubqueryImpl subQuery;

        public SubquerySelection(ExpressionImpl<S> wrapped, CriteriaSubqueryImpl subQuery) {
            super(wrapped);
            this.subQuery = subQuery;
        }

        @Override
        public String render(RenderingContext renderingContext) {
            return subQuery.render(renderingContext);
        }

        @Override
        public String renderProjection(RenderingContext renderingContext) {
            return render(renderingContext);
        }
    }


    // RESTRICTION ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public Predicate getRestriction() {
        return queryStructure.getRestriction();
    }

    @Override
    public Subquery<T> where(Expression<Boolean> expression) {
        queryStructure.setRestriction(criteriaBuilder().wrap(expression));
        return this;
    }

    @Override
    public Subquery<T> where(Predicate... predicates) {
        // TODO : assuming this should be a conjuntion, but the spec does not say specifically...
        queryStructure.setRestriction(criteriaBuilder().and(predicates));
        return this;
    }


    // GROUPING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public List<Expression<?>> getGroupList() {
        return queryStructure.getGroupings();
    }

    @Override
    public Subquery<T> groupBy(Expression<?>... groupings) {
        queryStructure.setGroupings(groupings);
        return this;
    }

    @Override
    public Subquery<T> groupBy(List<Expression<?>> groupings) {
        queryStructure.setGroupings(groupings);
        return this;
    }

    @Override
    public Predicate getGroupRestriction() {
        return queryStructure.getHaving();
    }

    @Override
    public Subquery<T> having(Expression<Boolean> expression) {
        queryStructure.setHaving(criteriaBuilder().wrap(expression));
        return this;
    }

    @Override
    public Subquery<T> having(Predicate... predicates) {
        queryStructure.setHaving(criteriaBuilder().and(predicates));
        return this;
    }


    // CORRELATIONS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public Set<Join<?, ?>> getCorrelatedJoins() {
        return queryStructure.collectCorrelatedJoins();
    }

    @Override
    public <Y> Root<Y> correlate(Root<Y> source) {
        final RootImpl<Y> correlation = ((RootImpl<Y>) source).correlateTo(this);
        queryStructure.addCorrelationRoot(correlation);
        return correlation;
    }

    @Override
    public <X, Y> Join<X, Y> correlate(Join<X, Y> source) {
        final JoinImplementor<X, Y> correlation = ((JoinImplementor<X, Y>) source).correlateTo(this);
        queryStructure.addCorrelationRoot(correlation);
        return correlation;
    }


    @Override
    public <U> Subquery<U> subquery(Class<U> subqueryType) {
        return queryStructure.subquery(subqueryType);
    }


    // rendering ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public String render(RenderingContext renderingContext) {
        StringBuilder subqueryBuffer = new StringBuilder("(");
        queryStructure.render(subqueryBuffer, renderingContext);
        subqueryBuffer.append(')');
        return subqueryBuffer.toString();
    }

    @Override
    public String renderProjection(RenderingContext renderingContext) {
        throw new IllegalStateException("Subquery cannot occur in select clause");
    }
}
