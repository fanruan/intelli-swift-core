package com.fr.swift.segment.relation;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.Flushable;
import com.fr.swift.cube.io.Releasable;

/**
 * @author anchore
 * @date 2018/1/17
 */
public interface RelationIndex extends Releasable, Flushable {
    String NULL_INDEX = "null_index";
    String INDEX = "index";
    String REVERSE = "reverse";
    /**
     * 主表行号 -> 外表所有匹配行号
     *
     * @param pos    主表行号
     * @param bitmap 外表所有匹配行号索引
     */
    void putIndex(int pos, ImmutableBitMap bitmap);

    ImmutableBitMap getIndex(int pos);

    /**
     * @param bitmap 主表所有未匹配行号索引
     */
    void putNullIndex(ImmutableBitMap bitmap);

    ImmutableBitMap getNullIndex();

    /**
     * 反向关联
     * 外表行号 -> 主表行号
     *
     * @param fPos 外表行号
     * @param tPos 主表行号
     */
    void putReverseIndex(int fPos, int tPos);

    int getReverseIndex(int fPos);
}