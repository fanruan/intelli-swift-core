package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;

/**
 * @author pony
 * @date 2017/12/6
 */
public interface DetailResultSet extends SwiftResultSet {

    /**
     * 获取一页数据，类似buffer的作用
     *
     * @return
     */
    List<Row> getPage();

    /**
     * 是否有下一页
     *
     * @return
     */
    boolean hasNextPage();

    /**
     * 获取总行数
     *
     * @return
     */
    int getRowCount();

    void setMetaData(SwiftMetaData metaData);
}