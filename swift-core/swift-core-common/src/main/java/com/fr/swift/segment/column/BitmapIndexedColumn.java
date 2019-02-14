package com.fr.swift.segment.column;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.cube.io.Flushable;
import com.fr.swift.cube.io.IfReadable;
import com.fr.swift.cube.io.Releasable;

/**
 * @author pony
 * @date 2017/10/9
 * 列的位图索引
 */
public interface BitmapIndexedColumn extends Releasable, Flushable, IfReadable {
    /**
     * 写入索引
     *
     * @param index  字典序号
     * @param bitmap 位图
     */
    void putBitMapIndex(int index, ImmutableBitMap bitmap);

    /**
     * 获取位图索引
     *
     * @param index 字典序号
     * @return 位图索引
     */
    ImmutableBitMap getBitMapIndex(int index);

    /**
     * 写入空值索引
     *
     * @param bitMap 索引
     */
    void putNullIndex(ImmutableBitMap bitMap);

    /**
     * 返回null值对应的索引
     *
     * @return 空值索引
     */
    ImmutableBitMap getNullIndex();

}
