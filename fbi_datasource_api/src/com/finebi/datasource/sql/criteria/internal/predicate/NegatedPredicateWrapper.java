
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.api.criteria.Predicate;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterContainer;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.ExpressionImpl;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Steve Ebersole
 */
public class NegatedPredicateWrapper extends ExpressionImpl<Boolean> implements PredicateImplementor, Serializable {
    private final PredicateImplementor predicate;
    private final BooleanOperator negatedOperator;
    private final List<Expression<Boolean>> negatedExpressions;

    @SuppressWarnings("unchecked")
    public NegatedPredicateWrapper(PredicateImplementor predicate) {
        super(predicate.criteriaBuilder(), Boolean.class);
        this.predicate = predicate;
        this.negatedOperator = predicate.isJunction()
                ? CompoundPredicate.reverseOperator(predicate.getOperator())
                : predicate.getOperator();
        this.negatedExpressions = negateCompoundExpressions(predicate.getExpressions(), predicate.criteriaBuilder());
    }

    private static List<Expression<Boolean>> negateCompoundExpressions(
            List<Expression<Boolean>> expressions,
            CriteriaBuilderImpl criteriaBuilder) {
        if (expressions == null || expressions.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Expression<Boolean>> negatedExpressions = new ArrayList<Expression<Boolean>>();
        for (Expression<Boolean> expression : expressions) {
            if (Predicate.class.isInstance(expression)) {
                negatedExpressions.add(((Predicate) expression).not());
            } else {
                negatedExpressions.add(criteriaBuilder.not(expression));
            }
        }
        return negatedExpressions;
    }

    public PredicateImplementor getPredicate() {
        return predicate;
    }

    @Override
    public BooleanOperator getOperator() {
        return negatedOperator;
    }

    @Override
    public boolean isJunction() {
        return predicate.isJunction();
    }

    @Override
    public boolean isNegated() {
        return !predicate.isNegated();
    }

    @Override
    public List<Expression<Boolean>> getExpressions() {
        return negatedExpressions;
    }

    @Override
    public Predicate not() {
        return new NegatedPredicateWrapper(this);
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        if (ParameterContainer.class.isInstance(predicate)) {
            ((ParameterContainer) predicate).registerParameters(registry);
        }
    }

    @Override
    public Object render(boolean isNegated, RenderingContext renderingContext) {
        RenderExtended renderExtended = (RenderExtended) renderingContext.getRenderFactory().getNegatedPredicateLiteralRender(this, "defaultTag");
        if (isNegated) {
            renderExtended.negate();
        }
        return renderExtended.render(renderingContext);
    }

    @Override
    public Object render(RenderingContext renderingContext) {
        return render(isNegated(), renderingContext);
    }

    @Override
    public Object renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }
}
