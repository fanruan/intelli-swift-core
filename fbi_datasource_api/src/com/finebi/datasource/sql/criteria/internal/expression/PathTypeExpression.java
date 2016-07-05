
package com.finebi.datasource.sql.criteria.internal.expression;

import com.finebi.datasource.sql.criteria.internal.CriteriaBuilderImpl;
import com.finebi.datasource.sql.criteria.internal.ParameterRegistry;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.path.AbstractPathImpl;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;

import java.io.Serializable;

/**
 * Used to construct the result of {@link com.finebi.datasource.api.criteria.Path#type()}
 *
 * @author Steve Ebersole
 */
public class PathTypeExpression<T> extends ExpressionImpl<T> implements Serializable {
    private final AbstractPathImpl<T> pathImpl;

    public PathTypeExpression(CriteriaBuilderImpl criteriaBuilder, Class<T> javaType, AbstractPathImpl<T> pathImpl) {
        super(criteriaBuilder, javaType);
        this.pathImpl = pathImpl;
    }

    public void registerParameters(ParameterRegistry registry) {
        // nothing to do
    }

    public String render(RenderingContext renderingContext) {
        RenderExtended render = (RenderExtended) renderingContext.getRenderFactory().getPathTypeExpressionRender(this, "d");
        render.render(renderingContext);
        return render.getRenderResult().toString();
    }

    public AbstractPathImpl<T> getPath() {
        return pathImpl;
    }

    public String renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }
}
