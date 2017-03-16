
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;

/**
 * Predicate used to assert a static boolean condition.
 *
 * @author Steve Ebersole
 */
public class BooleanStaticAssertionPredicate
        extends AbstractSimplePredicate
        implements Serializable {
    private final Boolean assertedValue;

    public BooleanStaticAssertionPredicate(
            CriteriaBuilderImpl criteriaBuilder,
            Boolean assertedValue) {
        super(criteriaBuilder);
        this.assertedValue = assertedValue;
    }

    public Boolean getAssertedValue() {
        return assertedValue;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        // nada
    }

    @Override
    public Object render(boolean isNegated, RenderingContext renderingContext) {
        RenderExtended renderExtended = (RenderExtended) renderingContext.getRenderFactory().getBooleanStaticAssertionPredicateRender(this, "defaultTag");
        if (isNegated) {
            renderExtended.negate();
        }
        return renderExtended.render(renderingContext);
    }

}
