package com.finebi.datasource.sql.criteria.internal.render.factory;

import com.finebi.datasource.sql.criteria.internal.QueryStructure;
import com.finebi.datasource.sql.criteria.internal.expression.PathTypeExpression;
import com.finebi.datasource.sql.criteria.internal.path.AbstractFromImpl;
import com.finebi.datasource.sql.criteria.internal.path.AbstractPathImpl;
import com.finebi.datasource.sql.criteria.internal.path.RootImpl;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public interface RenderFactory<R> {
    R getQueryStructureRender(QueryStructure queryStructure, String driverTag);

    R getAbstractPathRender(AbstractPathImpl path, String driverTag);

    R getAbstractFromRender(AbstractFromImpl from, String driverTag);

    R getRootRender(RootImpl root, String driverTag);

    R getPathTypeExpressionRender(PathTypeExpression pathTypeExpression, String driverTag);
}
