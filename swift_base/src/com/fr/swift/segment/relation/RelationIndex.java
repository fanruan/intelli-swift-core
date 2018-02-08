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
    String RELATIONS_KEY = "relations";
    /**
     * 主表行号 -> 外表所有匹配行号
     *
     * @param pos    主表行号
     * @param bitmap 外表所有匹配行号索引
     */
    void putIndex(int pos, ImmutableBitMap bitmap);

    /**
     * 正向关联
     *
     * @param pos
     * @return
     */
    ImmutableBitMap getIndex(int pos);

    /**
     * @param bitmap 主表所有未匹配行号索引
     */
    void putNullIndex(ImmutableBitMap bitmap);

    /**
     * 获取空索引
     * @return 返回空索引
     */
    ImmutableBitMap getNullIndex();

    /**
     * 反向关联
     * 外表行号 -> 主表行号
     *
     * @param fPos 外表行号
     * @param tPos 主表行号
     */
    void putReverseIndex(int fPos, int tPos);

    /**
     * 获取反向关联
     * @param fPos
     * @return
     */
    int getReverseIndex(int fPos);
}