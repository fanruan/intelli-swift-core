package com.fr.swift.service;

import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/13.
 */
public class SegmentLocationProvider {
    private static SegmentLocationProvider ourInstance = new SegmentLocationProvider();

    public static SegmentLocationProvider getInstance() {
        return ourInstance;
    }

    private SegmentLocationManager manager;

    private SegmentLocationProvider() {
    }

    /**
     * 给AnalysisService调用并更新segment location信息
     *
     * @param locationInfo
     */
    void updateSegmentInfo(SegmentLocationInfo locationInfo) {
        manager.updateSegmentInfo(locationInfo);
    }

    /**
     * 通过表名获取当前表的所有segment在各节点中的分布情况
     * { node0: { seg0, seg2 }, node1: { seg1, seg2, seg3 }, node2: { seg2, seg3 } }
     * 然后根据各节点上的负载情况决定查询方案：比如{ node0: { seg0, seg2 }, node2: { seg1, seg3 } }
     * 根据节点进行转发，同时把要查询的segment信息传给相关节点
     *
     * @param table
     * @return
     */
    public List<URI> getSegmentLocationURI(SourceKey table) {
        // TODO: 2018/5/30
        List<URI> uris = new ArrayList<URI>();
        try {
            uris.add(new URI(""));
        } catch (URISyntaxException e) {
        }
        return uris;
    }
}
