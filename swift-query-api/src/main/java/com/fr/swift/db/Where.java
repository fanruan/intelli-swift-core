package com.fr.swift.db;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.query.FilterBean;
import com.fr.swift.segment.Segment;

import java.net.URI;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/3/26
 */
public interface Where {

    /**
     * 获取过滤信息
     *
     * @return
     */
    FilterBean getFilterBean();

    /**
     * 创建符合条件的BitMap
     *
     * @param table   表
     * @param segment 数据块
     * @return
     * @throws Exception
     */
    ImmutableBitMap createWhereIndex(Table table, Segment segment) throws Exception;

    Map<URI, ImmutableBitMap> createWhereIndex(Table table) throws Exception;

}