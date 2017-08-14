package com.fr.bi.report.result;

/**
 * Created by andrew_asa on 2017/8/4.
 * 复杂交叉表结构
 */
public interface BIComplexCrossResult {

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
    BICrossNode[] getNode(int index);

}
