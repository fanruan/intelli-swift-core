package com.fr.swift.structure.iterator;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.bitmap.traversal.TraversalAction;

/**
 * 按行遍历
 */
public interface RowTraversal {
    /**
     * 遍历
     *
     * @param action 遍历的具体操作
     */
    void traversal(TraversalAction action);

    /**
     * 可中断的遍历
     *
     * @param action 遍历的具体操作
     * @return 是否遍历完
     */
    boolean breakableTraversal(BreakTraversalAction action);

    /**
     * 获取基数
     *
     * @return 有数据的行数
     */
    int getCardinality();

    /**
     * 是否为未过滤的
     *
     * @return 是否为未过滤的
     */
    boolean isFull();

    /**
     * 是否为全为空
     *
     * @return 是否为空
     */
    boolean isEmpty();

    ImmutableBitMap toBitMap();
}
