package com.fr.swift.query.aggregator.funnel.impl;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public interface Index {
    void addIndex(int index);

    int nextIndex();

    void resetIndex();

    void removeCurrentIndex();

    void setLastIndex(int i);

    void clear();
}
