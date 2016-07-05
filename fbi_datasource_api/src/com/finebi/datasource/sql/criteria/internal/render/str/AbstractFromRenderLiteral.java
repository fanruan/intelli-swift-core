package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.path.AbstractFromImpl;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class AbstractFromRenderLiteral extends BasicLiteralRender<AbstractFromImpl> {

    public AbstractFromRenderLiteral(AbstractFromImpl delegate) {
        super(delegate);
    }

    @Override
    public String renderProjection(RenderingContext renderingContext) {
        getDelegate().prepareAlias(renderingContext);
        return getDelegate().getAlias();
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return renderProjection(renderingContext);
    }
}
