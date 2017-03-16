package com.finebi.datasource.sql.criteria.internal.render.engine;

import com.finebi.datasource.sql.criteria.internal.Renderable;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.predicate.LikePredicate;
import com.fr.fineengine.criterion.Condition;
import com.fr.fineengine.criterion.MatchMode;
import com.fr.fineengine.criterion.Projection;
import com.fr.fineengine.criterion.Restrictions;

/**
 * This class created on 2016/7/5.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class LikePredicateEngineRender extends BasicEngineRender<LikePredicate, Condition> {
    public LikePredicateEngineRender(LikePredicate delegate) {
        super(delegate);
    }

    public Condition render(RenderingContext renderingContext) {
        if (isNegated()) {
            return notLike(renderingContext);
        } else {
            return like(renderingContext);
        }
    }

    public Condition like(RenderingContext renderingContext) {
        return Restrictions.like((Projection) ((Renderable) getDelegate().getMatchExpression()).render(renderingContext),
                ((Renderable) getDelegate().getPattern()).render(renderingContext),
                MatchMode.EXACT);
    }

    public Condition notLike(RenderingContext renderingContext) {
        return Restrictions.notLike((Projection) ((Renderable) getDelegate().getMatchExpression()).render(renderingContext),
                ((Renderable) getDelegate().getPattern()).render(renderingContext),
                MatchMode.EXACT);
    }

    public Condition renderProjection(RenderingContext renderingContext) {
        throw new UnsupportedOperationException();
    }
}