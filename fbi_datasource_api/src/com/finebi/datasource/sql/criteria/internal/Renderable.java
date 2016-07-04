
package com.finebi.datasource.sql.criteria.internal;


import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public interface Renderable<T> {
    public String render(RenderingContext renderingContext);

    public String renderProjection(RenderingContext renderingContext);

}
