package com.fr.swift.cloud.segment;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.cube.io.Releasable;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.source.SwiftMetaData;

/**
 * @author pony
 * @date 2017/10/9
 * 表示cube中一张表的一个分块,可以取得各个列
 */
public interface Segment extends Releasable {
    /**
     * 获取该分片的总行数
     *
     * @return
     */
    int getRowCount();

    void putRowCount(int rowCount);

    /**
     * 获取一列
     *
     * @param key
     * @return
     */
    <T> Column<T> getColumn(ColumnKey key);

    /**
     * 获取未被删除的索引
     *
     * @return
     */
    ImmutableBitMap getAllShowIndex();

    void putAllShowIndex(ImmutableBitMap bitMap);

    SwiftMetaData getMetaData();

    IResourceLocation getLocation();

    boolean isReadable();

    boolean isHistory();
}