package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

/**
 * @author pony
 * @date 2017/12/6
 */
public interface DetailResultSet extends SwiftResultSet, Pagination<List<Row>> {

    /**
     * 获取总行数
     *
     * @return
     */
    int getRowCount();

    void setMetaData(SwiftMetaData metaData);
}