package com.fr.swift.cube.space.impl;

import com.fr.swift.config.ISegmentKey;
import com.fr.swift.config.conf.service.SwiftConfigService;
import com.fr.swift.config.conf.service.SwiftConfigServiceProvider;
import com.fr.swift.cube.space.SpaceUsageDetector;
import com.fr.swift.cube.space.SpaceUsageService;
import com.fr.swift.source.SourceKey;

import java.net.URI;
import java.util.List;

/**
 * @author anchore
 * @date 2018/4/13
 */
public class SpaceUsageServiceImpl implements SpaceUsageService {
    private SpaceUsageDetector detector = new LocalSpaceUsageDetector();

    private SwiftConfigService confSvc = SwiftConfigServiceProvider.getInstance();

    @Override
    public long getTableUsedSpace(SourceKey table) throws Exception {
        List<ISegmentKey> segs = confSvc.getSegmentByKey(table.getId()).getSegments();

        long size = 0;
        for (ISegmentKey seg : segs) {
            size += detector.detectUsed(URI.create(seg.getUri()));
        }
        return size;
    }

    @Override
    public long getTableUsedSpace(List<SourceKey> tables) throws Exception {
        long used = 0;
        for (SourceKey table : tables) {
            used += getTableUsedSpace(table);
        }
        return used;
    }

    @Override
    public long getUsedOverall() throws Exception {
        URI baseUri = null;
        return detector.detectUsed(baseUri);
    }

    @Override
    public long getUsableOverall() throws Exception {
        URI baseUri = null;
        return detector.detectUsable(baseUri);
    }

    @Override
    public long getTotalOverall() throws Exception {
        URI baseUri = null;
        return detector.detectTotal(baseUri);
    }

    private static final SpaceUsageService INSTANCE = new SpaceUsageServiceImpl();

    private SpaceUsageServiceImpl() {
    }

    public static SpaceUsageService getInstance() {
        return INSTANCE;
    }
}