package com.fr.bi.report.result;

/**
 * Created by andrew_asa on 2017/8/4.
 * 复杂分组表结果结构
 */
public interface BIComplexGroupResult {

    /**
     * 分组表个数
     *
     * @return
     */
    int size();

    /**
     * 获取第index个node
     *
     * @param index
     * @return
     */
    BIGroupNode getNode(int index);
}
