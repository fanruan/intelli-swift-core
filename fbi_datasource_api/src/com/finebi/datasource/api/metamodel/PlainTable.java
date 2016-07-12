package com.finebi.datasource.api.metamodel;

import java.util.List;

/**
 * This class created on 2016/6/23.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface PlainTable<T> {
    PlainColumn getColumn(String name);

    List<PlainColumn> getColumns();

    String getTableName();
}
