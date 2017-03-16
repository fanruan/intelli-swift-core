package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.api.criteria.Join;
import com.finebi.datasource.api.criteria.JoinType;
import com.finebi.datasource.api.criteria.Root;
import com.finebi.datasource.sql.criteria.internal.FromImplementor;
import com.finebi.datasource.sql.criteria.internal.JoinImplementor;
import com.finebi.datasource.sql.criteria.internal.QueryStructure;
import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class QueryStructureRenderDebug extends BasicLiteralRender<QueryStructure> {


    public QueryStructureRenderDebug(QueryStructure delegate) {
        super(delegate);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        jpaqlQuery.append("select ");
        if (getDelegate().isDistinct()) {
            jpaqlQuery.append("distinct ");
        }
        if (getDelegate().getSelection() == null) {
            jpaqlQuery.append(locateImplicitSelection().renderProjection(renderingContext));
        } else {
            jpaqlQuery.append(((Renderable) getDelegate().getSelection()).renderProjection(renderingContext));
        }

        renderFromClause(jpaqlQuery, renderingContext);

        if (getDelegate().getRestriction() != null) {
            jpaqlQuery.append(" where ")
                    .append(((Renderable) getDelegate().getRestriction()).render(renderingContext));
        }

        if (!getDelegate().getGroupings().isEmpty()) {
            jpaqlQuery.append(" group by ");
            String sep = "";
            List<Expression<?>> expressions = getDelegate().getGroupings();
            for (Expression grouping : expressions) {
                jpaqlQuery.append(sep)
                        .append(((Renderable) grouping).render(renderingContext));
                sep = ", ";
            }

            if (getDelegate().getHaving() != null) {
                jpaqlQuery.append(" having ")
                        .append(((Renderable) getDelegate().getHaving()).render(renderingContext));
            }
        }
        return jpaqlQuery.toString();
    }

    public FromImplementor locateImplicitSelection() {
        FromImplementor implicitSelection = null;

        if (!getDelegate().isSubQuery()) {
            // we should have only a single root (query validation should have checked this...)
            implicitSelection = (FromImplementor) getDelegate().getRoots().iterator().next();
        } else {
            // we should only have a single "root" which can act as the implicit selection
            final Set<Join<?, ?>> correlatedJoins = getDelegate().collectCorrelatedJoins();
            if (correlatedJoins != null) {
                if (correlatedJoins.size() == 1) {
                    implicitSelection = (FromImplementor) correlatedJoins.iterator().next();
                }
            }
        }

        if (implicitSelection == null) {
            throw new IllegalStateException("No explicit selection and an implicit one could not be determined");
        }

        return implicitSelection;
    }

    private void renderFromClause(StringBuilder jpaqlQuery, RenderingContext renderingContext) {
        jpaqlQuery.append(" from ");
        String sep = "";
        Set<Root<?>> roots = getDelegate().getRoots();

        for (Root root : roots) {
            ((FromImplementor) root).prepareAlias(renderingContext);
            jpaqlQuery.append(sep);
            jpaqlQuery.append(((FromImplementor) root).renderTableExpression(renderingContext));
            sep = ", ";
        }
        for (Root root : roots) {
            renderJoins(jpaqlQuery, renderingContext, root.getJoins());
        }

        if (getDelegate().isSubQuery()) {
            if (getDelegate().getCorrelationRoots() != null) {
                Set<FromImplementor<?, ?>> correlationRoots = getDelegate().getCorrelationRoots();
                for (FromImplementor<?, ?> correlationRoot : correlationRoots) {
                    final FromImplementor correlationParent = correlationRoot.getCorrelationParent();
                    correlationParent.prepareAlias(renderingContext);
                    final String correlationRootAlias = correlationParent.getAlias();
                    for (Join<?, ?> correlationJoin : correlationRoot.getJoins()) {
                        final JoinImplementor correlationJoinImpl = (JoinImplementor) correlationJoin;
                        // IMPL NOTE: reuse the sep from above!
                        jpaqlQuery.append(sep);
                        correlationJoinImpl.prepareAlias(renderingContext);
                        jpaqlQuery.append(correlationRootAlias)
                                .append('.')
                                .append(correlationJoinImpl.getAttribute().getName())
                                .append(" as ")
                                .append(correlationJoinImpl.getAlias());
                        sep = ", ";
                        renderJoins(jpaqlQuery, renderingContext, correlationJoinImpl.getJoins());
                    }
                }
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private void renderJoins(
            StringBuilder jpaqlQuery,
            RenderingContext renderingContext,
            Collection<Join<?, ?>> joins) {
        if (joins == null) {
            return;
        }

        for (Join join : joins) {
            ((FromImplementor) join).prepareAlias(renderingContext);
            jpaqlQuery.append(renderJoinType(join.getJoinType()))
                    .append(((FromImplementor) join).renderTableExpression(renderingContext));
            renderJoins(jpaqlQuery, renderingContext, join.getJoins());
        }
    }

    private String renderJoinType(JoinType joinType) {
        switch (joinType) {
            case INNER: {
                return " inner join ";
            }
            case LEFT: {
                return " left join ";
            }
            case RIGHT: {
                return " right join ";
            }
        }
        throw new IllegalStateException("Unknown join type " + joinType);
    }

    @Override
    public String renderProjection(RenderingContext renderingContext) {
        return null;
    }
}
