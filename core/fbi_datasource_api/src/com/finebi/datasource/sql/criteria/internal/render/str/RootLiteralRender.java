package com.finebi.datasource.sql.criteria.internal.render.str;

import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.path.RootImpl;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class RootLiteralRender extends BasicLiteralRender<RootImpl> {
    public RootLiteralRender(RootImpl delegate) {
        super(delegate);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        getDelegate().prepareAlias(renderingContext);
        return getDelegate().getAlias();
    }

    @Override
    public String renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }
}
