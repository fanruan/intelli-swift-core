
package com.finebi.datasource.sql.criteria.internal;

import com.finebi.datasource.sql.criteria.internal.compile.CompilableCriteria;
import com.finebi.datasource.sql.criteria.internal.compile.CriteriaInterpretation;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.path.RootImpl;

import com.finebi.datasource.api.criteria.*;
import com.finebi.datasource.api.metamodel.EntityType;
import java.util.List;

/**
 * Base class for commonality between {@link com.finebi.datasource.api.criteria.CriteriaUpdate} and
 * {@link com.finebi.datasource.api.criteria.CriteriaDelete}
 *
 * @author Steve Ebersole
 */
public abstract class AbstractManipulationCriteriaQuery<T> implements CompilableCriteria, CommonAbstractCriteria {
    private final CriteriaBuilderImpl criteriaBuilder;

    private RootImpl<T> root;
    private Predicate restriction;
    private List<Subquery<?>> subQueries;

    protected AbstractManipulationCriteriaQuery(CriteriaBuilderImpl criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
    }

    protected CriteriaBuilderImpl criteriaBuilder() {
        return criteriaBuilder;
    }


    // Root ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public Root from(Class<T> entityClass) {
        EntityType<T> entityType = criteriaBuilder.getEntityManagerFactory()
                .getMetamodel()
                .entity(entityClass);
        if (entityType == null) {
            throw new IllegalArgumentException(entityClass + " is not an entity");
        }
        return from(entityType);
    }

    public Root<T> from(EntityType<T> entityType) {
        root = new RootImpl<T>(criteriaBuilder, entityType, false);
        return root;
    }

    public Root<T> getRoot() {
        return root;
    }


    // Restriction ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    protected void setRestriction(Expression<Boolean> restriction) {
        this.restriction = criteriaBuilder.wrap(restriction);
    }

    public void setRestriction(Predicate... restrictions) {
        this.restriction = criteriaBuilder.and(restrictions);
    }

    public Predicate getRestriction() {
        return restriction;
    }

    public <U> Subquery<U> subquery(Class<U> type) {
        return new CriteriaSubqueryImpl<U>(criteriaBuilder(), type, this);
    }


    // compiling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void validate() {
        if (root == null) {
            throw new IllegalStateException("UPDATE/DELETE criteria must name root entity");
        }
    }

    @Override
    public CriteriaInterpretation interpret(RenderingContext renderingContext) {
        final String jpaqlString = renderQuery(renderingContext);
        return null;
    }

    protected abstract String renderQuery(RenderingContext renderingContext);

    protected void renderRoot(StringBuilder jpaql, RenderingContext renderingContext) {
        jpaql.append(((FromImplementor) root).renderTableExpression(renderingContext));
    }

    protected void renderRestrictions(StringBuilder jpaql, RenderingContext renderingContext) {
        if (getRestriction() != null) {
            jpaql.append(" where ")
                    .append(((Renderable) getRestriction()).render(renderingContext));
        }
    }
}
