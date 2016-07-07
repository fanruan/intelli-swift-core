
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
        return (String)delegateRender(renderingContext);
    }
    public String renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }
    public Object delegateRender(RenderingContext renderingContext) {
        RenderExtended render = choseRender(renderingContext);
        return render.render(renderingContext);
    }

    protected RenderExtended choseRender(RenderingContext renderingContext) {
        return (RenderExtended) renderingContext.getRenderFactory().getPathTypeExpressionLiteralRender(this, "default");
    }
    public AbstractPathImpl<T> getPath() {
        return pathImpl;
    }
}
