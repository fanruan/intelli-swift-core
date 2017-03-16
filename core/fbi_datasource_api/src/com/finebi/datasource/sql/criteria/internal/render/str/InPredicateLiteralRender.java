package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.api.criteria.Subquery;
import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.InPredicate;

import java.util.List;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class InPredicateLiteralRender extends BasicLiteralRender<InPredicate> {
    public InPredicateLiteralRender(InPredicate delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        final StringBuilder buffer = new StringBuilder();
        final Expression exp = getDelegate().getExpression();

        buffer.append(((Renderable) getDelegate().getExpression()).render(renderingContext));

        if (isNegated()) {
            buffer.append(" not");
        }
        buffer.append(" in ");
        // subquery expressions are already wrapped in parenthesis, so we only need to
        // render the parenthesis here if the values represent an explicit value list
        boolean isInSubqueryPredicate = getDelegate().getValues().size() == 1
                && Subquery.class.isInstance(getDelegate().getValues().get(0));
        if (isInSubqueryPredicate) {
            buffer.append(((Renderable) getDelegate().getValues().get(0)).render(renderingContext));
        } else {
            buffer.append('(');
            String sep = "";
            List<Expression> values = getDelegate().getValues();
            for (Expression value : values) {
                buffer.append(sep)
                        .append(((Renderable) value).render(renderingContext));
                sep = ", ";
            }
            buffer.append(')');
        }
        return buffer.toString();
    }

    public String renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}