
package com.finebi.datasource.sql.criteria.internal.predicate;

import com.finebi.datasource.api.criteria.Subquery;
import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;

/**
 * Models an <tt>EXISTS(<subquery>)</tt> predicate
 *
 * @author Steve Ebersole
 */
public class ExistsPredicate
        extends AbstractSimplePredicate
        implements Serializable {
    private final Subquery<?> subquery;

    public ExistsPredicate(CriteriaBuilderImpl criteriaBuilder, Subquery<?> subquery) {
        super(criteriaBuilder);
        this.subquery = subquery;
    }

    public Subquery<?> getSubquery() {
        return subquery;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        // nothing to do here
    }

    @Override
    public Object render(boolean isNegated, RenderingContext renderingContext) {
        RenderExtended renderExtended = (RenderExtended) renderingContext.getRenderFactory().getExistsPredicateRender(this, "defaultTag");
        if (isNegated) {
            renderExtended.negate();
        }
        return renderExtended.render(renderingContext);
    }
}
