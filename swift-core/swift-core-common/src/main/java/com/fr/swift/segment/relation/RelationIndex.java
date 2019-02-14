package com.fr.swift.segment.relation;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.Flushable;
import com.fr.swift.cube.io.Releasable;
import com.fr.swift.cube.io.location.IResourceLocation;

/**
 * @author anchore
 * @date 2018/1/17
 * @modify yee
 * @description 每块数据接续着上一块的位置往下写
 */
public interface RelationIndex extends Releasable, Flushable {
    /**
     * 主表行号 -> 外表所有匹配行号
     *
     * @param segIndex 主Segment号
     * @param pos    主表行号
     * @param bitmap 外表所有匹配行号索引
     */
//    void putIndex(int pos, LongArray rows);m
//
    void putIndex(int segIndex, int pos, ImmutableBitMap bitmap);

    /**
     * 正向关联
     *
     * @param pos
     * @return
     */
//    LongArray getIndex(int pos);

    ImmutableBitMap getIndex(int segIndex, int pos);

    /**
     * @param pos 块号
     * @param bitmap 主表所有未匹配行号索引
     */
    void putNullIndex(int pos, ImmutableBitMap bitmap);

    /**
     * 获取空索引
     * @param pos 块号
     * @return 返回空索引
     */
    ImmutableBitMap getNullIndex(int pos);

    /**
     * 反向关联
     * 外表行号 -> 主表行号
     *
     * @param fPos 外表行号
     * @param tPos 主表块号+行号
     */
    void putReverseIndex(int fPos, long tPos);

    /**
     * 获取反向关联
     * @param fPos
     * @return
     */
    long getReverseIndex(int fPos);

    /**
     * @return 反向关联数
     */
    int getReverseCount();

    /**
     * @param count 反向关联数
     */
    void putReverseCount(int count);

    IResourceLocation getBaseLocation();
}