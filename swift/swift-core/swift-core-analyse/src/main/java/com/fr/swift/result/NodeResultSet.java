package com.fr.swift.result;

import com.fr.swift.structure.Pair;

import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2018/4/19.
 */
public interface NodeResultSet<T extends SwiftNode> extends SwiftResultSet {

    /**
     * 获取代表一页数据的Node节点
     *
     * @return
     */
    Pair<T, List<Map<Integer, Object>>> getPage();

    /**
     * 是否有下一页
     *
     * @return
     */
    boolean hasNextPage();
}
