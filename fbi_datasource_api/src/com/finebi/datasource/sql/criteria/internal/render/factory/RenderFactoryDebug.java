package com.finebi.datasource.sql.criteria.internal.render.factory;

import com.finebi.datasource.sql.criteria.internal.QueryStructure;
import com.finebi.datasource.sql.criteria.internal.expression.PathTypeExpression;
import com.finebi.datasource.sql.criteria.internal.path.AbstractFromImpl;
import com.finebi.datasource.sql.criteria.internal.path.AbstractPathImpl;
import com.finebi.datasource.sql.criteria.internal.path.RootImpl;
import com.finebi.datasource.sql.criteria.internal.render.LiteralRender;
import com.finebi.datasource.sql.criteria.internal.render.str.AbstractFromRenderLiteral;
import com.finebi.datasource.sql.criteria.internal.render.str.AbstractPathRenderLiteral;
import com.finebi.datasource.sql.criteria.internal.render.str.QueryStructureRenderDebug;
import com.finebi.datasource.sql.criteria.internal.render.str.RootLiteralRender;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public class RenderFactoryDebug implements RenderFactory<LiteralRender> {
    public LiteralRender getQueryStructureRender(QueryStructure queryStructure, String driverTag) {
        return new QueryStructureRenderDebug(queryStructure);
    }

    @Override
    public LiteralRender getAbstractPathRender(AbstractPathImpl path, String driverTag) {
        return new AbstractPathRenderLiteral(path);
    }

    @Override
    public LiteralRender getAbstractFromRender(AbstractFromImpl from, String driverTag) {
        return new AbstractFromRenderLiteral(from);
    }

    @Override
    public LiteralRender getRootRender(RootImpl root, String driverTag) {
        return new RootLiteralRender(root);
    }

    @Override
    public LiteralRender getPathTypeExpressionRender(PathTypeExpression pathTypeExpression, String driverTag) {
        return null;
    }
}
