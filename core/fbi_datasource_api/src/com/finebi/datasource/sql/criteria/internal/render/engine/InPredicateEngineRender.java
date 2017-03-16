package com.finebi.datasource.sql.criteria.internal.render.engine;

import com.finebi.datasource.api.criteria.Expression;
import com.finebi.datasource.api.criteria.Subquery;
import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.InPredicate;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fineengine.criterion.Condition;
import com.fr.fineengine.criterion.Projection;
import com.fr.fineengine.criterion.Restrictions;

import java.util.List;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class InPredicateEngineRender extends BasicEngineRender<InPredicate, Condition> {
    public InPredicateEngineRender(InPredicate delegate) {
        super(delegate);
    }

    public Condition render(RenderingContext renderingContext) {

        boolean isInSubqueryPredicate = getDelegate().getValues().size() == 1
                && Subquery.class.isInstance(getDelegate().getValues().get(0));
        if (isNegated()) {
            if (isInSubqueryPredicate) {
                return notInSubquery(renderingContext);
            } else {
                return notIn(renderingContext);
            }
        } else {
            if (isInSubqueryPredicate) {
                return inSubquery(renderingContext);
            } else {
                return in(renderingContext);
            }
        }


    }

    public Condition in(RenderingContext renderingContext) {
        List<Expression> values = getDelegate().getValues();
        Object[] objects = new Object[values.size()];
        for (int i = 0; i < values.size(); i++) {
            objects[i] = ((Renderable) values.get(i)).render(renderingContext);
        }

        return Restrictions.in((Projection) ((Renderable) getDelegate().getExpression()).render(renderingContext), objects);
    }

    public Condition notIn(RenderingContext renderingContext) {
        List<Expression> values = getDelegate().getValues();
        Object[] objects = new Object[values.size()];
        for (int i = 0; i < values.size(); i++) {
            objects[i] = ((Renderable) values.get(i)).render(renderingContext);
        }

        return Restrictions.notIn((Projection) ((Renderable) getDelegate().getExpression()).render(renderingContext), objects);
    }

    public Condition inSubquery(RenderingContext renderingContext) {
        throw BINonValueUtils.beyondControl();
    }

    public Condition notInSubquery(RenderingContext renderingContext) {
        throw BINonValueUtils.beyondControl();
    }

    public Condition renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}