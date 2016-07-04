
package com.finebi.datasource.sql.criteria.internal;

import com.finebi.datasource.api.criteria.*;
import com.finebi.datasource.api.metamodel.EntityType;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.path.RootImpl;
import com.finebi.datasource.sql.criteria.internal.render.QueryStructureRender;

import java.io.Serializable;
import java.util.*;

/**
 * Models basic query structure.  Used as a delegate in implementing both
 * {@link com.finebi.datasource.api.criteria.CriteriaQuery} and
 * {@link com.finebi.datasource.api.criteria.Subquery}.
 * <p/>
 * Note the <tt>ORDER BY</tt> specs are neglected here.  That's because it is not valid
 * for a subquery to define an <tt>ORDER BY</tt> clause.  So we just handle them on the
 * root query directly...
 *
 * @author Steve Ebersole
 */
public class QueryStructure<T> implements Serializable {
    private final AbstractQuery<T> owner;
    private final CriteriaBuilderImpl criteriaBuilder;
    private final boolean isSubQuery;

    public QueryStructure(AbstractQuery<T> owner, CriteriaBuilderImpl criteriaBuilder) {
        this.owner = owner;
        this.criteriaBuilder = criteriaBuilder;
        this.isSubQuery = Subquery.class.isInstance(owner);
    }

    private boolean distinct;
    private Selection<? extends T> selection;
    private Set<Root<?>> roots = new LinkedHashSet<Root<?>>();
    private Set<FromImplementor> correlationRoots;
    private Predicate restriction;
    private List<Expression<?>> groupings = Collections.emptyList();
    private Predicate having;
    private List<Subquery<?>> subqueries;


    // PARAMETERS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public Set<ParameterExpression<?>> getParameters() {
        final Set<ParameterExpression<?>> parameters = new LinkedHashSet<ParameterExpression<?>>();
        final ParameterRegistry registry = new ParameterRegistry() {
            public void registerParameter(ParameterExpression<?> parameter) {
                parameters.add(parameter);
            }
        };

        ParameterContainer.Helper.possibleParameter(selection, registry);
        ParameterContainer.Helper.possibleParameter(restriction, registry);
        ParameterContainer.Helper.possibleParameter(having, registry);
        if (subqueries != null) {
            for (Subquery subquery : subqueries) {
                ParameterContainer.Helper.possibleParameter(subquery, registry);
            }
        }

        // both group-by and having expressions can (though unlikely) contain parameters...
        ParameterContainer.Helper.possibleParameter(having, registry);
        if (groupings != null) {
            for (Expression<?> grouping : groupings) {
                ParameterContainer.Helper.possibleParameter(grouping, registry);
            }
        }

        return parameters;
    }


    // SELECTION ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public Selection<? extends T> getSelection() {
        return selection;
    }

    public void setSelection(Selection<? extends T> selection) {
        this.selection = selection;
    }


    // ROOTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public Set<Root<?>> getRoots() {
        return roots;
    }

    public <X> Root<X> from(Class<X> entityClass) {
        EntityType<X> entityType = criteriaBuilder.getEntityManagerFactory()
                .getMetamodel()
                .entity(entityClass);
        if (entityType == null) {
            throw new IllegalArgumentException(entityClass + " is not an entity");
        }
        return from(entityType);
    }

    public <X> Root<X> from(EntityType<X> entityType) {
        RootImpl<X> root = new RootImpl<X>(criteriaBuilder, entityType);
        roots.add(root);
        return root;
    }


    // CORRELATION ROOTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void addCorrelationRoot(FromImplementor fromImplementor) {
        if (!isSubQuery) {
            throw new IllegalStateException("Query is not identified as sub-query");
        }
        if (correlationRoots == null) {
            correlationRoots = new HashSet<FromImplementor>();
        }
        correlationRoots.add(fromImplementor);
    }

    public Set<Join<?, ?>> collectCorrelatedJoins() {
        if (!isSubQuery) {
            throw new IllegalStateException("Query is not identified as sub-query");
        }
        final Set<Join<?, ?>> correlatedJoins;
        if (correlationRoots != null) {
            correlatedJoins = new HashSet<Join<?, ?>>();
            for (FromImplementor<?, ?> correlationRoot : correlationRoots) {
                if (correlationRoot instanceof Join<?, ?> && correlationRoot.isCorrelated()) {
                    correlatedJoins.add((Join<?, ?>) correlationRoot);
                }
                correlatedJoins.addAll(correlationRoot.getJoins());
            }
        } else {
            correlatedJoins = Collections.emptySet();
        }
        return correlatedJoins;
    }


    // RESTRICTIONS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public Predicate getRestriction() {
        return restriction;
    }

    public void setRestriction(Predicate restriction) {
        this.restriction = restriction;
    }


    // GROUPINGS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public List<Expression<?>> getGroupings() {
        return groupings;
    }

    public void setGroupings(List<Expression<?>> groupings) {
        this.groupings = groupings;
    }

    public void setGroupings(Expression<?>... groupings) {
        if (groupings != null && groupings.length > 0) {
            this.groupings = Arrays.asList(groupings);
        } else {
            this.groupings = Collections.emptyList();
        }
    }

    public Predicate getHaving() {
        return having;
    }

    public void setHaving(Predicate having) {
        this.having = having;
    }


    // SUB-QUERIES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public List<Subquery<?>> getSubqueries() {
        return subqueries;
    }

    public List<Subquery<?>> internalGetSubqueries() {
        if (subqueries == null) {
            subqueries = new ArrayList<Subquery<?>>();
        }
        return subqueries;
    }

    public <U> Subquery<U> subquery(Class<U> subqueryType) {
        CriteriaSubqueryImpl<U> subquery = new CriteriaSubqueryImpl<U>(criteriaBuilder, subqueryType, owner);
        internalGetSubqueries().add(subquery);
        return subquery;
    }

    @SuppressWarnings({"unchecked"})
    public void render(StringBuilder jpaqlQuery, RenderingContext renderingContext) {
        QueryStructureRender render = criteriaBuilder.getEntityManagerFactory().getRenderFactory().getQueryStructureRender(this, "sql");
        render.render(renderingContext);
        jpaqlQuery.append(render.getRenderResult().toString());
    }

    public boolean isSubQuery() {
        return isSubQuery;
    }

    public Set<FromImplementor> getCorrelationRoots() {
        return correlationRoots;
    }


}
