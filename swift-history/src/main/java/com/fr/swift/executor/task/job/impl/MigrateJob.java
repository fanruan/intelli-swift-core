package com.fr.swift.executor.task.job.impl;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.executor.task.job.BaseJob;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.service.ServiceContext;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
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

    private List<String> segmentIds;
    private List<SegmentKey> segmentKeys;
    private String path;


    public MigrateJob(List<SegmentKey> segmentKeys, String path) {
        this.segmentKeys = segmentKeys;
        this.segmentIds = segmentKeys.stream().map(SegmentKey::getId).collect(Collectors.toList());
        this.path = path;
    }

    @Override
    public Boolean call() throws Exception {
        final ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
        Map<SegmentKey, byte[]> segments = new HashMap<>();
        for (SegmentKey segmentKey : segmentKeys) {
            segments.put(segmentKey, Files.readAllBytes(Paths.get(new CubePathBuilder(segmentKey).asAbsolute().build())));
        }
        return serviceContext.migrate(segments, path);
    }

    @Override
    public List<String> serializedTag() {
        return segmentIds;
    }
}
