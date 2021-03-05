package com.fr.swift.cloud.query.aggregator.funnel;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public interface IMergeColumn {
    int getIndex(int row);

    Object getValue(int row);

    int dictSize();
}
