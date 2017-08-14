package com.fr.bi.report.result;

import java.util.Iterator;
import java.util.List;

/**
 * Created by andrew_asa on 2017/8/2.
 * 明细表结构
 */
public interface BIDetailTableResult extends Iterator<List<BIDetailCell>> {

    /**
     * 总行数
     *
     * @return
     */
    int rowSize();

    /**
     * 列数
     *
     * @return
     */
    int columnSize();

    /**
     * 设置总行数
     *
     * @param size
     */
    void setColSize(int size);

    /**
     * 设置总列数
     *
     * @param size
     */
    void setColumnSize(int size);
}
