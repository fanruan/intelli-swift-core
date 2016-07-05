
package com.finebi.datasource.sql.criteria.internal;


import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public interface Renderable {
    public Object render(RenderingContext renderingContext);

    public Object renderProjection(RenderingContext renderingContext);

}
