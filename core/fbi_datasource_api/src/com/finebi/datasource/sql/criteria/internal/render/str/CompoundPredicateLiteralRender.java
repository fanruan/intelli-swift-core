package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.api.criteria.Predicate;
import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.CompoundPredicate;
import com.finebi.datasource.sql.criteria.internal.predicate.PredicateImplementor;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class CompoundPredicateLiteralRender extends BasicLiteralRender<CompoundPredicate> {
    public CompoundPredicateLiteralRender(CompoundPredicate delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        return render(getDelegate(), renderingContext);
    }

    public String renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }


    public static String render(PredicateImplementor predicate, RenderingContext renderingContext) {
        if (!predicate.isJunction()) {
            throw new IllegalStateException("CompoundPredicate.render should only be used to render junctions");
        }

        // for junctions, the negation is already cooked into the expressions and operator; we just need to render
        // them as is

        if (predicate.getExpressions().isEmpty()) {
            boolean implicitTrue = predicate.getOperator() == Predicate.BooleanOperator.AND;
            // AND is always true for empty; OR is always false
            return implicitTrue ? "1=1" : "0=1";
        }

        // single valued junction ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        if (predicate.getExpressions().size() == 1) {
            return ((Renderable) predicate.getExpressions().get(0)).render(renderingContext).toString();
        }

        final StringBuilder buffer = new StringBuilder();
        String sep = "";
        for (Expression expression : predicate.getExpressions()) {
            buffer.append(sep)
                    .append("( ")
                    .append(((Renderable) expression).render(renderingContext))
                    .append(" )");
            sep = operatorTextWithSeparator(predicate.getOperator());
        }
        return buffer.toString();
    }

    private static String operatorTextWithSeparator(Predicate.BooleanOperator operator) {
        return operator == Predicate.BooleanOperator.AND
                ? " and "
                : " or ";
    }
}