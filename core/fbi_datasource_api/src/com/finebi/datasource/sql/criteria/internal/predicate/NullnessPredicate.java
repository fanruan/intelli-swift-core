
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.UnaryOperatorExpression;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;

/**
 * Defines a {@link com.finebi.datasource.api.criteria.Predicate} for checking the
 * nullness state of an expression, aka an <tt>IS [NOT] NULL</tt> predicate.
 * <p/>
 * The <tt>NOT NULL</tt> form can be built by calling the constructor and then
 * calling {@link #not}.
 *
 * @author Steve Ebersole
 */
public class NullnessPredicate
        extends AbstractSimplePredicate
        implements UnaryOperatorExpression<Boolean>, Serializable {
    private final Expression<?> operand;

    /**
     * Constructs the affirmitive form of nullness checking (<i>IS NULL</i>).  To
     * construct the negative form (<i>IS NOT NULL</i>) call {@link #not} on the
     * constructed instance.
     *
     * @param criteriaBuilder The query builder from whcih this originates.
     * @param operand         The expression to check.
     */
    public NullnessPredicate(CriteriaBuilderImpl criteriaBuilder, Expression<?> operand) {
        super(criteriaBuilder);
        this.operand = operand;
    }

    @Override
    public Expression<?> getOperand() {
        return operand;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        Helper.possibleParameter(getOperand(), registry);
    }

    @Override
    public Object render(boolean isNegated, RenderingContext renderingContext) {
        RenderExtended renderExtended = (RenderExtended) renderingContext.getRenderFactory().getNullnessPredicateRender(this, "defaultTag");
        if (isNegated) {
            renderExtended.negate();
        }
        return renderExtended.render(renderingContext);
    }

}
