package com.finebi.datasource.sql.criteria.internal.render.engine;

import com.finebi.datasource.sql.criteria.internal.PathSource;
import com.finebi.datasource.sql.criteria.internal.compile.RenderingContext;
import com.finebi.datasource.sql.criteria.internal.path.AbstractPathImpl;
import com.finebi.datasource.sql.criteria.internal.render.str.BasicLiteralRender;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class AbstractPathEngineRender extends BasicLiteralRender<AbstractPathImpl> {
    public AbstractPathEngineRender(AbstractPathImpl delegate) {
        super(delegate);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        PathSource<?> source = getDelegate().getPathSource();
        if (source != null) {
            source.prepareAlias(renderingContext);
            return source.getPathIdentifier() + "." + getDelegate().getAttribute().getName();
        } else {
            return getDelegate().getAttribute().getName();
        }
    }

    @Override
    public String renderProjection(RenderingContext renderingContext) {
        return render(renderingContext);
    }
}
