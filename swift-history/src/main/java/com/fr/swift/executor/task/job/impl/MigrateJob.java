package com.fr.swift.executor.task.job.impl;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.ServiceContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Moira
 * @date 2020/7/21
 * @description
 * @since swift-1.2.0
 */
public class MigrateJob extends BaseJob<Boolean, List<String>> {

    List<String> segmentIds;
    List<SegmentKey> segmentKeys;
    Map<File, SegmentKey> segments;
    boolean remote;
    String path;
    String prePath;


    public MigrateJob(Map<File, SegmentKey> segments, boolean remote, String path, String prePath) {
        this.segmentKeys = new ArrayList<>(segments.values());
        this.segmentIds = segmentKeys.stream().map(SegmentKey::getId).collect(Collectors.toList());
        this.segments = segments;
        this.remote = remote;
        this.path = path;
        this.prePath = prePath;
    }

    @Override
    public Boolean call() throws Exception {
        final ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
        return serviceContext.migrate(segments, remote, path, prePath);
    }

    @Override
    public List<String> serializedTag() {
        return segmentIds;
    }
}
