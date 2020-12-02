package com.fr.swift.db;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.query.FilterBean;
import com.fr.swift.segment.SegmentKey;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/3/26
 */
public interface Where extends Serializable {

    /**
     * 获取过滤信息
     *
     * @return
     */
    FilterBean getFilterBean();

    /**
     * 创建符合条件的BitMap
     *
     * @param table      表
     * @param segmentKey 数据块
     * @return
     * @throws Exception
     */
    ImmutableBitMap createWhereIndex(Table table, SegmentKey segmentKey) throws Exception;

    Map<SegmentKey, ImmutableBitMap> createWhereIndex(Table table) throws Exception;

    Collection<SegmentKey> createWhereSegments(Table table) throws Exception;
}