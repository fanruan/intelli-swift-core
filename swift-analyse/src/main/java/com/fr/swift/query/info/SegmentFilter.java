package com.fr.swift.query.info;


import com.fr.swift.segment.Segment;

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
    List<Segment> filter(SingleTableQueryInfo singleTableQueryInfo) throws Exception;

}
