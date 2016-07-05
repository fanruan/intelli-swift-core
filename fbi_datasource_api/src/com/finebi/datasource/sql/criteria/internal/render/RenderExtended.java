package com.finebi.datasource.sql.criteria.internal.render;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public interface RenderExtended<R> {
    R render(RenderingContext renderingContext);

    R renderProjection(RenderingContext renderingContext);

    R getRenderResult();
}
