package com.finebi.datasource.sql.criteria.internal.render.factory;

import com.finebi.datasource.sql.criteria.internal.QueryStructure;
import com.finebi.datasource.sql.criteria.internal.expression.PathTypeExpression;
import com.finebi.datasource.sql.criteria.internal.path.AbstractFromImpl;
import com.finebi.datasource.sql.criteria.internal.path.AbstractPathImpl;
import com.finebi.datasource.sql.criteria.internal.path.RootImpl;
import com.finebi.datasource.sql.criteria.internal.render.RenderExtended;
import com.finebi.datasource.sql.criteria.internal.render.engine.AbstractPathEngineRender;
import com.finebi.datasource.sql.criteria.internal.render.engine.QueryStructureRenderFineEngine;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class RenderFactoryEngineAdapter implements RenderFactory {
    public RenderExtended getQueryStructureRender(QueryStructure queryStructure, String driverTag) {
        return new QueryStructureRenderFineEngine(queryStructure);
    }

    @Override
    public Object getAbstractPathRender(AbstractPathImpl path, String driverTag) {
        return new AbstractPathEngineRender(path);
    }

    @Override
    public Object getAbstractFromRender(AbstractFromImpl from, String driverTag) {
        return null;
    }

    @Override
    public Object getRootRender(RootImpl root, String driverTag) {
        return null;
    }

    @Override
    public Object getPathTypeExpressionRender(PathTypeExpression pathTypeExpression, String driverTag) {
        return null;
    }
}
