
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.api.criteria.Predicate;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A compound {@link Predicate predicate} is a grouping of other {@link Predicate predicates} in order to convert
 * either a conjunction (logical AND) or a disjunction (logical OR).
 *
 * @author Steve Ebersole
 */
public class CompoundPredicate
        extends AbstractPredicateImpl
        implements Serializable {
    private BooleanOperator operator;
    private final List<Expression<Boolean>> expressions = new ArrayList<Expression<Boolean>>();

    /**
     * Constructs an empty conjunction or disjunction.
     *
     * @param criteriaBuilder The query builder from which this originates.
     * @param operator        Indicates whether this predicate will function
     *                        as a conjunction or disjunction.
     */
    public CompoundPredicate(CriteriaBuilderImpl criteriaBuilder, BooleanOperator operator) {
        super(criteriaBuilder);
        this.operator = operator;
    }

    /**
     * Constructs a conjunction or disjunction over the given expressions.
     *
     * @param criteriaBuilder The query builder from which this originates.
     * @param operator        Indicates whether this predicate will function
     *                        as a conjunction or disjunction.
     * @param expressions     The expressions to be grouped.
     */
    public CompoundPredicate(
            CriteriaBuilderImpl criteriaBuilder,
            BooleanOperator operator,
            Expression<Boolean>... expressions) {
        this(criteriaBuilder, operator);
        applyExpressions(expressions);
    }

    /**
     * Constructs a conjunction or disjunction over the given expressions.
     *
     * @param criteriaBuilder The query builder from which this originates.
     * @param operator        Indicates whether this predicate will function
     *                        as a conjunction or disjunction.
     * @param expressions     The expressions to be grouped.
     */
    public CompoundPredicate(
            CriteriaBuilderImpl criteriaBuilder,
            BooleanOperator operator,
            List<Expression<Boolean>> expressions) {
        this(criteriaBuilder, operator);
        applyExpressions(expressions);
    }

    private void applyExpressions(Expression<Boolean>... expressions) {
        applyExpressions(Arrays.asList(expressions));
    }

    private void applyExpressions(List<Expression<Boolean>> expressions) {
        this.expressions.clear();
        this.expressions.addAll(expressions);
    }

    @Override
    public BooleanOperator getOperator() {
        return operator;
    }

    @Override
    public List<Expression<Boolean>> getExpressions() {
        return expressions;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        for (Expression expression : getExpressions()) {
            Helper.possibleParameter(expression, registry);
        }
    }

    @Override
    public Object render(RenderingContext renderingContext) {
        return render(isNegated(), renderingContext);
    }

    @Override
    public boolean isJunction() {
        return true;
    }

    @Override
    public Object render(boolean isNegated, RenderingContext renderingContext) {
        RenderExtended renderExtended = (RenderExtended) renderingContext.getRenderFactory().getCompoundPredicateLiteralRender(this, "defaultTag");
        if (isNegated) {
            renderExtended.negate();
        }
        return renderExtended.render(renderingContext);
    }

    @Override
    public Object renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }

    /**
     * Create negation of compound predicate by using logic rules:
     * 1. not (x || y) is (not x && not y)
     * 2. not (x && y) is (not x || not y)
     */
    @Override
    public Predicate not() {
        return new NegatedPredicateWrapper(this);
    }

    private void toggleOperator() {
        this.operator = reverseOperator(this.operator);
    }

    public static BooleanOperator reverseOperator(BooleanOperator operator) {
        return operator == BooleanOperator.AND
                ? BooleanOperator.OR
                : BooleanOperator.AND;
    }


}
