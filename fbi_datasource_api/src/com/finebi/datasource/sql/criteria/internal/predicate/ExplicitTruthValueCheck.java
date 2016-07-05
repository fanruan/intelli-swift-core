
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;

/**
 * ANSI-SQL defines <tt>TRUE</tt>, <tt>FALSE</tt> and <tt>UNKNOWN</tt> as <i>truth values</i>.  These
 * <i>truth values</i> are used to explicitly check the result of a boolean expression (the syntax is like
 * <tt>a > b IS TRUE</tt>.  <tt>IS TRUE</tt> is the assumed default.
 * <p/>
 * JPA defines support for only <tt>IS TRUE</tt> and <tt>IS FALSE</tt>, not <tt>IS UNKNOWN</tt> (<tt>a > NULL</tt>
 * is an example where the result would be UNKNOWN.
 *
 * @author Steve Ebersole
 */
public class ExplicitTruthValueCheck
        extends AbstractSimplePredicate
        implements Serializable {
    // TODO : given that JPA supports only TRUE and FALSE, can this be handled just with negation?
    private final Expression<Boolean> booleanExpression;
    private final TruthValue truthValue;

    public ExplicitTruthValueCheck(CriteriaBuilderImpl criteriaBuilder, Expression<Boolean> booleanExpression, TruthValue truthValue) {
        super(criteriaBuilder);
        this.booleanExpression = booleanExpression;
        this.truthValue = truthValue;
    }

    public Expression<Boolean> getBooleanExpression() {
        return booleanExpression;
    }

    public TruthValue getTruthValue() {
        return truthValue;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        Helper.possibleParameter(getBooleanExpression(), registry);
    }

    @Override
    public Object render(boolean isNegated, RenderingContext renderingContext) {
        RenderExtended renderExtended = (RenderExtended) renderingContext.getRenderFactory().getExplicitTruthValueCheckLiteralRender(this, "defaultTag");
        if (isNegated) {
            renderExtended.negate();
        }
        return renderExtended.render(renderingContext);
    }
}
