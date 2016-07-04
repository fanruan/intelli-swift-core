package com.finebi.datasource.sql.criteria.internal.render;

import com.finebi.datasource.sql.criteria.internal.QueryStructure;

/**
 * This class created on 2016/7/4.
 *
 * @author Connery
 * @since 4.0
 */
public interface RenderFactory {
    QueryStructureBasicRender getQueryStructureRender(QueryStructure queryStructure, String driverTag);
}
