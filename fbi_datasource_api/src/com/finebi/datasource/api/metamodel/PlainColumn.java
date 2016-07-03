package com.finebi.datasource.api.metamodel;

import com.finebi.datasource.sql.criteria.AttributeType;

/**
 * This class created on 2016/6/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface PlainColumn {
    String getColumnName();

    AttributeType getJavaType();

}
