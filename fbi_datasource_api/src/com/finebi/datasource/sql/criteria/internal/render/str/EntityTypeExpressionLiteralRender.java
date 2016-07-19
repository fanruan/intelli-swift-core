package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.expression.EntityTypeExpression;

/**
 * This class created on 2016/7/7.
 *
 * @author Osborn
 * @since Advanced FineBI Analysis 1.0
 */
public class EntityTypeExpressionLiteralRender extends BasicLiteralRender<EntityTypeExpression> {
    public EntityTypeExpressionLiteralRender(EntityTypeExpression delegate) {
        super(delegate);
    }

    public String render(RenderingContext renderingContext) {
        // todo : is it valid for this to get rendered into the query itself?
        throw new IllegalArgumentException("Unexpected call on EntityTypeExpression#render");
    }

    public String renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }
}
