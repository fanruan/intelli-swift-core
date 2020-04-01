package com.fr.swift.query.info;


import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;

import java.util.List;

/**
 * Create by lifan on 2019-07-18 15:34
 */
public interface SegmentFilter {

    /**
     * @param
     * @return
     * @description 根据导入方式查询segment
     */
    List<Segment> filterSegs(SingleTableQueryInfo singleTableQueryInfo) throws Exception;

    List<SegmentKey> filterSegKeys(SingleTableQueryInfo singleTableQueryInfo) throws Exception;
}
