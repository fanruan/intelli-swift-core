package com.finebi.datasource.sql.criteria.internal.render.engine;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.BetweenPredicate;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fineengine.criterion.Condition;
import com.fr.fineengine.criterion.Projection;
import com.fr.fineengine.criterion.Restrictions;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BetweenPredicateEngineRender extends BasicEngineRender<BetweenPredicate, Condition> {
    public BetweenPredicateEngineRender(BetweenPredicate delegate) {
        super(delegate);
    }

    public Condition render(RenderingContext renderingContext) {
        return isNegated() ? notBetween(renderingContext) : between(renderingContext);
    }

    private Condition between(RenderingContext renderingContext) {
        return Restrictions.between((Projection) ((Renderable) getDelegate().getExpression()).render(renderingContext),
                ((Renderable) getDelegate().getLowerBound()).render(renderingContext),
                ((Renderable) getDelegate().getUpperBound()).render(renderingContext));
    }

    private Condition notBetween(RenderingContext renderingContext) {
        throw BINonValueUtils.beyondControl();
    }

    public Condition renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}