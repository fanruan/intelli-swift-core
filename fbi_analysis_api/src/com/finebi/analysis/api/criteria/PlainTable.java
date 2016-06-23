package com.finebi.analysis.api.criteria;

/**
 * This class created on 2016/6/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface PlainTable<T> extends Path {
    PlainColumn getColumn(String name);
}
