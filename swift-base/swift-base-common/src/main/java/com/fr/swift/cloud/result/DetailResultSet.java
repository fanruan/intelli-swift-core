package com.fr.swift.cloud.result;

import com.fr.swift.cloud.source.Row;

import java.util.List;

/**
 * @author pony
 * @date 2017/12/6
 * TODO: 2019/6/24 anchore 为啥要继承SwiftResultSet和Pagination？匪夷所思
 */
public interface DetailResultSet extends SwiftResultSet, Pagination<List<Row>> {

    /**
     * 获取总行数
     *
     * @return
     */
    int getRowCount();
}