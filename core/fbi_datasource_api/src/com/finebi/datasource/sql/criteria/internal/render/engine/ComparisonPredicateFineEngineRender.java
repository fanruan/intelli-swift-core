package com.finebi.datasource.sql.criteria.internal.render.engine;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.ComparisonPredicate;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fineengine.criterion.*;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class ComparisonPredicateFineEngineRender extends BasicEngineRender<ComparisonPredicate, Condition> {
    public ComparisonPredicateFineEngineRender(ComparisonPredicate delegate) {
        super(delegate);
    }

    public Condition render(RenderingContext renderingContext) {
        return choseCondition(renderingContext);
    }

    private Condition eq(RenderingContext renderingContext) {
        return Restrictions.eq((Projection) ((Renderable) getDelegate().getLeftHandOperand()).render(renderingContext),
                ((Renderable) getDelegate().getRightHandOperand()).render(renderingContext));
    }

    private Condition neq(RenderingContext renderingContext) {
        return Restrictions.ne((Projection) ((Renderable) getDelegate().getLeftHandOperand()).render(renderingContext),
                ((Renderable) getDelegate().getRightHandOperand()).render(renderingContext));
    }

    private Condition lt(RenderingContext renderingContext) {
        return Restrictions.lt((Projection) ((Renderable) getDelegate().getLeftHandOperand()).render(renderingContext),
                ((Renderable) getDelegate().getRightHandOperand()).render(renderingContext));
    }

    private Condition le(RenderingContext renderingContext) {
        return Restrictions.le((Projection) ((Renderable) getDelegate().getLeftHandOperand()).render(renderingContext),
                ((Renderable) getDelegate().getRightHandOperand()).render(renderingContext));
    }

    private Condition gt(RenderingContext renderingContext) {
        return Restrictions.gt((Projection) ((Renderable) getDelegate().getLeftHandOperand()).render(renderingContext),
                ((Renderable) getDelegate().getRightHandOperand()).render(renderingContext));
    }

    private Condition ge(RenderingContext renderingContext) {
        return Restrictions.ge((Projection) ((Renderable) getDelegate().getLeftHandOperand()).render(renderingContext),
                ((Renderable) getDelegate().getRightHandOperand()).render(renderingContext));
    }

    private Condition choseCondition(RenderingContext renderingContext) {
        switch (getDelegate().getComparisonOperator()) {

            case EQUAL:
                return eq(renderingContext);
            case NOT_EQUAL:
                return neq(renderingContext);
            case LESS_THAN:
                return lt(renderingContext);
            case LESS_THAN_OR_EQUAL:
                return le(renderingContext);
            case GREATER_THAN:
                return gt(renderingContext);
            case GREATER_THAN_OR_EQUAL:
                return ge(renderingContext);
            default:
                throw BINonValueUtils.beyondControl("Please check :" + getDelegate().getComparisonOperator());
        }
    }

    public CompareCondition renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}