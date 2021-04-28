package com.fr.swift.cloud.analyse;

import com.fr.swift.cloud.result.Pagination;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.SwiftMetaData;

import java.util.List;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/15
 */
public interface CalcSegment extends AutoCloseable, Pagination<List<Row>> {

    /**
     * 流式查询下的rowcount 只是一个批次的rowcount
     *
     * @return 一个batch下的rowcount
     */
    int rowCount();

    /**
     * 流式查询下的getNextRow 必须配合hasNext来运行，否则只能够获取某一个批次的数据
     * 结合rowCount 和 getNextRow 可以达成查询单一批次数据的目的
     *
     * @return 下一条数据
     */
    Row getNextRow();

    /**
     * 获取元数据，描述本次查询结果
     *
     * @return SwiftMetaData
     */
    SwiftMetaData getMetaData();

    /**
     * 是否有更多的数据，配合getNextRow就能够真正查询全部的数据
     *
     * @return 是否有更多的数据
     */
    boolean hasNext();

    int getFetchSize();
}
