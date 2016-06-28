package com.finebi.datasource.api.criteria;

/**
 * This class created on 2016/6/23.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface PlainTable<T> extends Path {
    PlainColumn getColumn(String name);
}
