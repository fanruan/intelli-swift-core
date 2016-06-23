package com.finebi.analysis.api.criteria;

import com.finebi.analysis.api.metamodel.EntityType;

/**
 * This class created on 2016/6/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface PlainTable<T> extends Path,EntityType {
    PlainColumn getColumn(String name);
}
