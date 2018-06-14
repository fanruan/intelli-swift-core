package com.fr.swift.result;

import com.fr.swift.source.SwiftResultSet;

/**
 * Created by pony on 2018/4/19.
 */
public interface NodeResultSet<T extends SwiftNode> extends SwiftResultSet {

    /**
     * 获取代表一页数据的Node节点
     * todo 这个接口有问题，应该是表示一页结果集对象，有待分析改进
     *
     * @return
     */
    SwiftNode<T> getNode();

    /**
     * 是否有下一页
     *
     * @return
     */
    boolean hasNextPage();
}
