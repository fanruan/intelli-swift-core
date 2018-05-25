package com.fr.swift.config.service;

import com.fr.config.Configuration;
import com.fr.swift.config.SwiftPathConfig;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.dao.SwiftMetaDataDAO;
import com.fr.swift.config.dao.SwiftSegmentDAO;
import com.fr.swift.config.transaction.SwiftMetaDataTransactionWorker;
import com.fr.swift.config.transaction.SwiftSegmentTransactionWorker;
import com.fr.swift.config.transaction.SwiftTransactionManager;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.transaction.Configurations;
import com.fr.transaction.Worker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/3/23
 */
public class SwiftConfigServiceImpl implements SwiftConfigService {

    private SwiftPathConfig swiftPathConfig = SwiftPathConfig.getInstance();
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftConfigServiceImpl.class);

    private ConcurrentHashMap<String, SwiftMetaData> metaDataCache = new ConcurrentHashMap<String, SwiftMetaData>();

    @Override
    public boolean addMetaData(final String sourceKey, final SwiftMetaData metaData) {
        try {
            final SwiftMetaDataBean bean = (SwiftMetaDataBean) metaData;
            bean.setId(sourceKey);
            return (Boolean) SwiftTransactionManager.doTransactionIfNeed(new SwiftMetaDataTransactionWorker() {
                @Override
                public Object work(SwiftMetaDataDAO dao) throws SQLException {
                    dao.addOrUpdateSwiftMetaData(bean);
                    metaDataCache.put(sourceKey, bean);
                    return true;
                }
            });
        } catch (Exception e) {
            LOGGER.error("Add or update metadata error!", e);
            return false;
        }
    }

    @Override
    public boolean addMetaDatas(final Map<String, SwiftMetaData> metaDatas) {
        try {
            return (Boolean) SwiftTransactionManager.doTransactionIfNeed(new SwiftMetaDataTransactionWorker() {
                @Override
                public Object work(SwiftMetaDataDAO dao) throws SQLException {
                    Iterator<Map.Entry<String, SwiftMetaData>> iterator = metaDatas.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, SwiftMetaData> entry = iterator.next();
                        SwiftMetaDataBean bean = (SwiftMetaDataBean) entry.getValue();
                        bean.setId(entry.getKey());
                        dao.addOrUpdateSwiftMetaData(bean);
                        metaDataCache.put(entry.getKey(), bean);
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            LOGGER.error("Add metadata error!", e);
            return false;
        }
    }

    @Override
    public boolean removeMetaDatas(final String... sourceKeys) {
        try {
            return (Boolean) SwiftTransactionManager.doTransactionIfNeed(new SwiftMetaDataTransactionWorker() {

                @Override
                public Object work(SwiftMetaDataDAO dao) throws SQLException {
                    for (String sourceKey : sourceKeys) {
                        dao.deleteSwiftMetaDataBean(sourceKey);
                    }
                    return true;
                }
            });

        } catch (Exception e) {
            LOGGER.error("Remove metadata error!", e);
            return false;
        }
    }

    @Override
    public boolean updateMetaData(final String sourceKey, final SwiftMetaData metaData) {
        return addMetaData(sourceKey, metaData);
    }

    @Override
    public Map<String, SwiftMetaData> getAllMetaData() {
        try {
            List<SwiftMetaDataBean> beans = (List<SwiftMetaDataBean>) SwiftTransactionManager.doTransactionIfNeed(new SwiftMetaDataTransactionWorker() {
                @Override
                public Object work(SwiftMetaDataDAO dao) {
                    return dao.findAll();
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
            Map<String, SwiftMetaData> result = new HashMap<String, SwiftMetaData>();
            for (SwiftMetaDataBean bean : beans) {
                result.put(bean.getId(), bean);
            }
            return result;
        } catch (Exception e) {
            LOGGER.error("Select metadata error!", e);
            return new HashMap<String, SwiftMetaData>();
        }
    }

    @Override
    public SwiftMetaData getMetaDataByKey(final String sourceKey) {
        SwiftMetaData metaData = metaDataCache.get(sourceKey);
        if (null == metaData) {
            try {
                return (SwiftMetaDataBean) SwiftTransactionManager.doTransactionIfNeed(new SwiftMetaDataTransactionWorker() {
                    @Override
                    public Object work(SwiftMetaDataDAO dao) throws SQLException {
                        SwiftMetaDataBean metaData = dao.findBySourceKey(sourceKey);
                        metaDataCache.put(sourceKey, metaData);
                        return metaData;
                    }

                    @Override
                    public boolean needTransaction() {
                        return false;
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Select metadata error!", e);
                return null;
            }
        }
        return metaData;

    }

    @Override
    public boolean containsMeta(final SourceKey sourceKey) {
        try {
            return (Boolean) SwiftTransactionManager.doTransactionIfNeed(new SwiftMetaDataTransactionWorker() {
                @Override
                public Object work(SwiftMetaDataDAO dao) throws SQLException {
                    SwiftMetaDataBean metaData = dao.findBySourceKey(sourceKey.getId());
                    return null != metaData;
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean addSegments(final List<SegmentKey> segments) {
        try {
            return (Boolean) SwiftTransactionManager.doTransactionIfNeed(new SwiftSegmentTransactionWorker() {
                @Override
                public Object work(SwiftSegmentDAO dao) throws SQLException {
                    for (SegmentKey bean : segments) {
                        dao.addOrUpdateSwiftSegment((SegmentKeyBean) bean);
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            LOGGER.error("Add or update segments error!", e);
            return false;
        }
    }

    @Override
    public boolean removeSegments(final String... sourceKeys) {
        try {
            return (Boolean) SwiftTransactionManager.doTransactionIfNeed(new SwiftSegmentTransactionWorker() {
                @Override
                public Object work(SwiftSegmentDAO dao) throws SQLException {
                    for (String sourceKey : sourceKeys) {
                        dao.deleteBySourceKey(sourceKey);
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            LOGGER.error("Remove segments error!", e);
            return false;
        }
    }

    @Override
    public boolean updateSegments(final String sourceKey, final List<SegmentKey> segments) {
        try {
            return (Boolean) SwiftTransactionManager.doTransactionIfNeed(new SwiftSegmentTransactionWorker() {
                @Override
                public Object work(SwiftSegmentDAO dao) throws SQLException {
                    dao.deleteBySourceKey(sourceKey);
                    for (SegmentKey segment : segments) {
                        dao.addOrUpdateSwiftSegment((SegmentKeyBean) segment);
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            LOGGER.error("Update segment failed!", e);
            return false;
        }
    }

    @Override
    public Map<String, List<SegmentKey>> getAllSegments() {
        Map<String, List<SegmentKey>> result = new HashMap<String, List<SegmentKey>>();
        try {
            List<SegmentKeyBean> beans = (List<SegmentKeyBean>) SwiftTransactionManager.doTransactionIfNeed(new SwiftSegmentTransactionWorker() {
                @Override
                public Object work(SwiftSegmentDAO dao) {
                    return dao.findAll();
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
            for (SegmentKeyBean bean : beans) {
                if (!result.containsKey(bean.getSourceKey())) {
                    result.put(bean.getSourceKey(), new ArrayList<SegmentKey>());
                }
                result.get(bean.getSourceKey()).add(bean);
            }
        } catch (Exception e) {
            LOGGER.error("Select segments error!", e);
        }
        return result;
    }

    @Override
    public List<SegmentKey> getSegmentByKey(final String sourceKey) {
        try {
            return (List<SegmentKey>) SwiftTransactionManager.doTransactionIfNeed(new SwiftSegmentTransactionWorker() {
                @Override
                public Object work(SwiftSegmentDAO dao) {
                    return dao.findBySourceKey(sourceKey);
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (Exception e) {
            LOGGER.error("Select segments error!", e);
            return Collections.emptyList();
        }
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

    private abstract class SwiftPathConfigWorker implements Worker {
        @Override
        public Class<? extends Configuration>[] targets() {
            return new Class[]{SwiftPathConfig.class};
        }
    }
}