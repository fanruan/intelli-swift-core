package com.fr.swift.source;

/**
 * This class created on 2017-12-19 13:38:23
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface Source {
    /**
     * 标识这个数据源的key
     *
     * @return
     */
    SourceKey getSourceKey();
}
