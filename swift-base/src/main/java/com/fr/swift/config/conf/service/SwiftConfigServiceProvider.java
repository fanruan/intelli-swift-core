package com.fr.swift.config.conf.service;

import com.fr.swift.config.IConfigSegment;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.util.Map;

/**
 * @author yee
 * @date 2018/3/23
 */
public class SwiftConfigServiceProvider implements SwiftConfigService {

    private SwiftConfigService service = null;
    private static SwiftConfigServiceProvider instance;

    private SwiftConfigServiceProvider() {
        this.service = new SwiftConfigServiceImpl();
    }

    public static SwiftConfigServiceProvider getInstance() {
        if (null == instance) {
            synchronized (SwiftConfigServiceProvider.class) {
                instance = new SwiftConfigServiceProvider();
            }
        }
        return instance;
    }

    @Override
    public boolean addMetaData(String sourceKey, SwiftMetaData metaData) {
        return service.addMetaData(sourceKey, metaData);
    }

    @Override
    public boolean addMetaDatas(Map<String, SwiftMetaData> metaDatas) {
        return service.addMetaDatas(metaDatas);
    }

    @Override
    public boolean removeMetaDatas(String... sourceKey) {
        return service.removeMetaDatas(sourceKey);
    }

    @Override
    public boolean updateMetaData(String sourceKey, SwiftMetaData metaData) {
        return service.updateMetaData(sourceKey, metaData);
    }

    @Override
    public Map<String, SwiftMetaData> getAllMetaData() throws SwiftMetaDataException {
        return service.getAllMetaData();
    }

    @Override
    public SwiftMetaData getMetaDataByKey(String sourceKey) throws SwiftMetaDataException {
        return service.getMetaDataByKey(sourceKey);
    }

    @Override
    public boolean containsMeta(SourceKey sourceKey) {
        return service.containsMeta(sourceKey);
    }

    @Override
    public boolean addSegments(IConfigSegment... segments) {
        return service.addSegments(segments);
    }

    @Override
    public boolean removeSegments(String... sourceKey) {
        return service.removeSegments(sourceKey);
    }

    @Override
    public boolean updateSegments(IConfigSegment... segments) {
        return service.updateSegments(segments);
    }

    @Override
    public Map<String, IConfigSegment> getAllSegments() {
        return service.getAllSegments();
    }

    @Override
    public IConfigSegment getSegmentByKey(String sourceKey) {
        return service.getSegmentByKey(sourceKey);
    }

    @Override
    public boolean setSwiftPath(String path) {
        return service.setSwiftPath(path);
    }

    @Override
    public String getSwiftPath() {
        return service.getSwiftPath();
    }
}
