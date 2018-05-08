package com.fr.swift.result;

import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/28.
 */
public interface XNodeMergeResultSet extends NodeMergeResultSet<XLeftNode> {

    /**
     * 各个维度的node节点用到的字典值
     *
     * @return
     */
    List<Map<Integer, Object>> getColGlobalDictionaries();

    /**
     * 获取表头节点
     *
     * @return
     */
    TopGroupNode getTopGroupNode();
}
