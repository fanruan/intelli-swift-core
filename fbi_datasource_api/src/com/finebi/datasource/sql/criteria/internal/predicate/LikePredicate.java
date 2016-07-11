
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.LiteralExpression;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;

/**
 * Models a SQL <tt>LIKE</tt> expression.
 *
 * @author Steve Ebersole
 */
public class LikePredicate extends AbstractSimplePredicate implements Serializable {
    private final Expression<String> matchExpression;
    private final Expression<String> pattern;
    private final Expression<Character> escapeCharacter;

    public LikePredicate(
            CriteriaBuilderImpl criteriaBuilder,
            Expression<String> matchExpression,
            Expression<String> pattern) {
        this(criteriaBuilder, matchExpression, pattern, null);
    }

    public LikePredicate(
            CriteriaBuilderImpl criteriaBuilder,
            Expression<String> matchExpression,
            String pattern) {
        this(criteriaBuilder, matchExpression, new LiteralExpression<String>(criteriaBuilder, pattern));
    }

    public LikePredicate(
            CriteriaBuilderImpl criteriaBuilder,
            Expression<String> matchExpression,
            Expression<String> pattern,
            Expression<Character> escapeCharacter) {
        super(criteriaBuilder);
        this.matchExpression = matchExpression;
        this.pattern = pattern;
        this.escapeCharacter = escapeCharacter;
    }

    public LikePredicate(
            CriteriaBuilderImpl criteriaBuilder,
            Expression<String> matchExpression,
            Expression<String> pattern,
            char escapeCharacter) {
        this(
                criteriaBuilder,
                matchExpression,
                pattern,
                new LiteralExpression<Character>(criteriaBuilder, escapeCharacter)
        );
    }

    public LikePredicate(
            CriteriaBuilderImpl criteriaBuilder,
            Expression<String> matchExpression,
            String pattern,
            char escapeCharacter) {
        this(
                criteriaBuilder,
                matchExpression,
                new LiteralExpression<String>(criteriaBuilder, pattern),
                new LiteralExpression<Character>(criteriaBuilder, escapeCharacter)
        );
    }

    public LikePredicate(
            CriteriaBuilderImpl criteriaBuilder,
            Expression<String> matchExpression,
            String pattern,
            Expression<Character> escapeCharacter) {
        this(
                criteriaBuilder,
                matchExpression,
                new LiteralExpression<String>(criteriaBuilder, pattern),
                escapeCharacter
        );
    }

    public Expression<Character> getEscapeCharacter() {
        return escapeCharacter;
    }

    public Expression<String> getMatchExpression() {
        return matchExpression;
    }

    public Expression<String> getPattern() {
        return pattern;
    }

    public void registerParameters(ParameterRegistry registry) {
        Helper.possibleParameter(getEscapeCharacter(), registry);
        Helper.possibleParameter(getMatchExpression(), registry);
        Helper.possibleParameter(getPattern(), registry);
    }

    @Override
    public Object render(boolean isNegated, RenderingContext renderingContext) {
        RenderExtended renderExtended = (RenderExtended) renderingContext.getRenderFactory().getLikePredicateRender(this, "defaultTag");
        if (isNegated) {
            renderExtended.negate();
        }
        return renderExtended.render(renderingContext);
    }
}
