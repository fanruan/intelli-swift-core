package com.fr.swift.config.conf.service;

import com.fr.config.Configuration;
import com.fr.swift.config.IConfigSegment;
import com.fr.swift.config.conf.MetaDataConfig;
import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.config.conf.SwiftPathConfig;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.transaction.Configurations;
import com.fr.transaction.Worker;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/3/23
 */
public class SwiftConfigServiceImpl implements SwiftConfigService {
    private MetaDataConfig metaDataConfig = MetaDataConfig.getInstance();

    private SegmentConfig segmentConfig = SegmentConfig.getInstance();

    private SwiftPathConfig swiftPathConfig = SwiftPathConfig.getInstance();

    private Map<String, SwiftMetaData> metaDataCache = new ConcurrentHashMap<String, SwiftMetaData>();

    @Override
    public boolean addMetaData(final String sourceKey, final SwiftMetaData metaData) {
        return Configurations.update(new MetaDataConfigWorker() {
            @Override
            public void run() {
                try {
                    metaDataConfig.addMetaData(sourceKey, metaData);
                    metaDataCache.put(sourceKey, metaData);
                } catch (SwiftMetaDataException e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        });
    }

    @Override
    public boolean addMetaDatas(final Map<String, SwiftMetaData> metaDatas) {
        return Configurations.update(new MetaDataConfigWorker() {
            @Override
            public void run() {
                try {
                    for (Entry<String, SwiftMetaData> entry : metaDatas.entrySet()) {
                        metaDataConfig.addMetaData(entry.getKey(), entry.getValue());
                        metaDataCache.put(entry.getKey(), entry.getValue());
                    }
                } catch (SwiftMetaDataException e) {
                    SwiftLoggers.getLogger().error(e);
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
    public boolean updateMetaData(final String sourceKey, final SwiftMetaData metaData) {
        return Configurations.update(new MetaDataConfigWorker() {
            @Override
            public void run() {
                try {
                    metaDataConfig.modifyMetaData(sourceKey, metaData);
                    metaDataCache.put(sourceKey, metaData);
                } catch (SwiftMetaDataException e) {
                    SwiftLoggers.getLogger().error(e);
                }
            }
        });
    }

    @Override
    public Map<String, SwiftMetaData> getAllMetaData() throws SwiftMetaDataException {
        return metaDataConfig.getAllMetaData();
    }

    @Override
    public SwiftMetaData getMetaDataByKey(String sourceKey) throws SwiftMetaDataException {
        SwiftMetaData metaData = metaDataCache.get(sourceKey);
        if (null == metaData) {
            metaData = metaDataConfig.getMetaDataByKey(sourceKey);
            metaDataCache.put(sourceKey, metaData);
        }
        return metaData;
    }

    @Override
    public boolean containsMeta(SourceKey sourceKey) {
        return metaDataConfig.contains(sourceKey);
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

    @Override
    public boolean setSwiftPath(final String path) {
        return Configurations.update(new SwiftPathConfigWorker() {
            @Override
            public void run() {
                swiftPathConfig.setPath(path);
            }
        });
    }

    @Override
    public String getSwiftPath() {
        return this.swiftPathConfig.getPath();
    }

    private abstract class MetaDataConfigWorker implements Worker {

        @Override
        public Class<? extends Configuration>[] targets() {
            return new Class[]{MetaDataConfig.class};
        }
    }

    private abstract class SegmentConfigWorker implements Worker {

        @Override
        public Class<? extends Configuration>[] targets() {
            return new Class[]{SegmentConfig.class};
        }
    }

    private abstract class SwiftPathConfigWorker implements Worker {
        @Override
        public Class<? extends Configuration>[] targets() {
            return new Class[]{SwiftPathConfig.class};
        }
    }
}