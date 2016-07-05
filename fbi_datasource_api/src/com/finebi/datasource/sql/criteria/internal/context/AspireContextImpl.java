package com.finebi.datasource.sql.criteria.internal.context;

import com.finebi.datasource.sql.criteria.internal.important.MetamodelExpander;
import com.finebi.datasource.sql.criteria.internal.render.factory.RenderFactory;
import com.finebi.datasource.sql.criteria.internal.render.factory.RenderFactoryEngineAdapter;

/**
 * This class created on 2016/7/1.
 *
 * @author Connery
 * @since 4.0
 */
public class AspireContextImpl implements AspireContext {
    private RenderFactory renderFactory;

    public AspireContextImpl() {
        renderFactory = new RenderFactoryEngineAdapter();
    }

    public AspireContextImpl(RenderFactory renderFactory) {
        this.renderFactory = renderFactory;
    }

    @Override
    public MetamodelExpander getMetamodel() {
        return null;
    }

    @Override
    public RenderFactory getRenderFactory() {
        return renderFactory;
    }
}
