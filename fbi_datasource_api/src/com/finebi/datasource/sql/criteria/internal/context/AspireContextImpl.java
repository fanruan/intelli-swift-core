package com.finebi.datasource.sql.criteria.internal.context;

import com.finebi.datasource.sql.criteria.internal.important.MetamodelExpander;
import com.finebi.datasource.sql.criteria.internal.render.RenderFactory;
import com.finebi.datasource.sql.criteria.internal.render.RenderFactoryEngineAdapter;
import com.finebi.datasource.sql.criteria.internal.render.RenderFactorySQL;

/**
 * This class created on 2016/7/1.
 *
 * @author Connery
 * @since 4.0
 */
public class AspireContextImpl implements AspireContext {
    private RenderFactory renderFactory;

    public AspireContextImpl() {
        renderFactory = new RenderFactorySQL();
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
