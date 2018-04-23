package com.fr.swift.config.conf.service;

import com.fr.config.Configuration;
import com.fr.swift.config.IConfigSegment;
import com.fr.swift.config.IMetaData;
import com.fr.swift.config.conf.MetaDataConfig;
import com.fr.swift.config.conf.MetaDataConvertUtil;
import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.config.pojo.SwiftMetaDataPojo;
import com.fr.swift.source.SwiftMetaData;
import com.fr.transaction.Configurations;
import com.fr.transaction.Worker;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/3/23
 */
public class SwiftConfigServiceImpl implements SwiftConfigService {

    private MetaDataConfig metaDataConfig = MetaDataConfig.getInstance();
    private SegmentConfig segmentConfig = SegmentConfig.getInstance();
    private ConcurrentHashMap<String, SwiftMetaDataPojo> metaDataCache = new ConcurrentHashMap<String, SwiftMetaDataPojo>();

    @Override
    public boolean addMetaData(final String sourceKey, final IMetaData metaData) {
        return Configurations.update(new MetaDataConfigWorker() {
            @Override
            public void run() {
                metaDataConfig.addMetaData(sourceKey, metaData);
                metaDataCache.put(sourceKey, MetaDataConvertUtil.toSwiftMetadataPojo(metaData));
            }
        });
    }

    @Override
    public boolean addMetaDatas(final Map<String, IMetaData> metaDatas) {
        return Configurations.update(new MetaDataConfigWorker() {
            @Override
            public void run() {
                Iterator<Map.Entry<String, IMetaData>> iterator = metaDatas.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, IMetaData> entry = iterator.next();
                    metaDataConfig.addMetaData(entry.getKey(), entry.getValue());
                    metaDataCache.put(entry.getKey(), MetaDataConvertUtil.toSwiftMetadataPojo(entry.getValue()));
                }
            }
        });
    }

    @Override
    public boolean removeMetaDatas(final String... sourceKeys) {
        return Configurations.update(new MetaDataConfigWorker() {
            @Override
            public void run() {
                for (String sourceKey : sourceKeys) {
                    metaDataConfig.removeMetaData(sourceKey);
                    metaDataCache.remove(sourceKey);
                }
            }
        });
    }

    @Override
    public boolean updateMetaData(final String sourceKey, final IMetaData metaData) {
        return Configurations.update(new MetaDataConfigWorker() {
            @Override
            public void run() {
                metaDataConfig.modifyMetaData(sourceKey, metaData);
                metaDataCache.put(sourceKey, MetaDataConvertUtil.toSwiftMetadataPojo(metaData));
            }
        });
    }

    @Override
    public Map<String, IMetaData> getAllMetaData() {
        return metaDataConfig.getAllMetaData();
    }

    @Override
    public IMetaData getMetaDataByKey(String sourceKey) {
        IMetaData metaData = metaDataCache.get(sourceKey);
        if (null == metaData) {
            metaData = metaDataConfig.getMetaDataByKey(sourceKey);
            metaDataCache.put(sourceKey, MetaDataConvertUtil.toSwiftMetadataPojo(metaData));
        }
        return metaData;
    }

    @Override
    public boolean addSegments(final IConfigSegment... segments) {
        return Configurations.update(new SegmentConfigWorker() {
            @Override
            public void run() {
                for (IConfigSegment segment : segments) {
                    segmentConfig.putSegment(segment);
                }
            }
        });
    }

    @Override
    public boolean removeSegments(final String... sourceKeys) {
        return Configurations.update(new SegmentConfigWorker() {
            @Override
            public void run() {
                for (String sourceKey : sourceKeys) {
                    segmentConfig.removeSegment(sourceKey);
                }
            }
        });
    }

    @Override
    public boolean updateSegments(final IConfigSegment... segments) {
        return Configurations.update(new SegmentConfigWorker() {
            @Override
            public void run() {
                for (IConfigSegment segment : segments) {
                    segmentConfig.modifySegment(segment);
                }
            }
        });
    }

    @Override
    public Map<String, IConfigSegment> getAllSegments() {
        return segmentConfig.getAllSegments();
    }

    @Override
    public IConfigSegment getSegmentByKey(String sourceKey) {
        return segmentConfig.getSegmentByKey(sourceKey);
    }

    private abstract class MetaDataConfigWorker implements Worker {

        @Override
        public Class<? extends Configuration>[] targets() {
            return new Class[] {MetaDataConfig.class};
        }
    }

    private abstract class SegmentConfigWorker implements Worker {

        @Override
        public Class<? extends Configuration>[] targets() {
            return new Class[] {SegmentConfig.class};
        }
    }
}