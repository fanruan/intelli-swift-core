package com.finebi.datasource.sql.criteria.internal.render.engine;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.path.AbstractFromImpl;
import com.finebi.datasource.sql.criteria.internal.render.str.BasicLiteralRender;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class AbstractFromEngineRender extends BasicLiteralRender<AbstractFromImpl> {

    public AbstractFromEngineRender(AbstractFromImpl delegate) {
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
