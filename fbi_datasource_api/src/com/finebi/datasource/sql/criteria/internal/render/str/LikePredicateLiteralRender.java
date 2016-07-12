package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.LikePredicate;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class LikePredicateLiteralRender extends BasicLiteralRender<LikePredicate> {
    public LikePredicateLiteralRender(LikePredicate delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        final String operator = isNegated() ? " not like " : " like ";
        StringBuilder buffer = new StringBuilder();
        buffer.append(((Renderable) getDelegate().getMatchExpression()).render(renderingContext))
                .append(operator)
                .append(((Renderable) getDelegate().getPattern()).render(renderingContext));
        if (getDelegate().getEscapeCharacter() != null) {
            buffer.append(" escape ")
                    .append(((Renderable) getDelegate().getEscapeCharacter()).render(renderingContext));
        }
        return buffer.toString();
    }

    public String renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}