package com.fr.swift.service;

import com.fr.swift.query.query.QueryRunner;
import com.fr.swift.segment.SegmentLocationInfo;

import java.io.Serializable;

/**
 * @author yee
 * @date 2018/6/13
 */
public interface AnalyseService extends QueryRunner, Serializable {

    void updateSegmentInfo(SegmentLocationInfo locationInfo, SegmentLocationInfo.UpdateType updateType);
}
