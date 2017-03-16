
package com.finebi.datasource.sql.criteria.internal;


import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

/**
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface Renderable<R> {
    R render(RenderingContext renderingContext);

    R renderProjection(RenderingContext renderingContext);

}
