
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;

/**
 * Predicate to assert the explicit value of a boolean expression:<ul>
 * <li>x = true</li>
 * <li>x = false</li>
 * <li>x <> true</li>
 * <li>x <> false</li>
 * </ul>
 *
 * @author Steve Ebersole
 */
public class BooleanAssertionPredicate
        extends AbstractSimplePredicate
        implements Serializable {
    private final Expression<Boolean> expression;
    private final Boolean assertedValue;

    public BooleanAssertionPredicate(
            CriteriaBuilderImpl criteriaBuilder,
            Expression<Boolean> expression,
            Boolean assertedValue) {
        super(criteriaBuilder);
        this.expression = expression;
        this.assertedValue = assertedValue;
    }

    public Expression<Boolean> getExpression() {
        return expression;
    }

    public Boolean getAssertedValue() {
        return assertedValue;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        Helper.possibleParameter(expression, registry);
    }

    @Override
    public Object render(boolean isNegated, RenderingContext renderingContext) {
        RenderExtended renderExtended = (RenderExtended) renderingContext.getRenderFactory().getBooleanAssertionPredicateLiteralRender(this, "defaultTag");
        if (isNegated) {
            renderExtended.negate();
        }
        return renderExtended.render(renderingContext);
    }
}
