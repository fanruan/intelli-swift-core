package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.dao.SwiftMetaDataDao;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.transaction.AbstractTransactionWorker;
import com.fr.swift.config.transaction.SwiftTransactionManager;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/6/6
 */
@Service("swiftMetaDataService")
public class SwiftMetaDataServiceImpl implements SwiftMetaDataService {

    @Autowired
    private SwiftTransactionManager transactionManager;
    @Autowired
    private SwiftMetaDataDao swiftMetaDataDao;

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftMetaDataServiceImpl.class);
    private ConcurrentHashMap<String, SwiftMetaData> metaDataCache = new ConcurrentHashMap<String, SwiftMetaData>();

    @Override
    public boolean addMetaData(final String sourceKey, final SwiftMetaData metaData) {
        try {
            final SwiftMetaDataBean bean = (SwiftMetaDataBean) metaData;
            bean.setId(sourceKey);
            return (Boolean) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() throws SQLException {
                    swiftMetaDataDao.addOrUpdateSwiftMetaData(bean);
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
            return (Boolean) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {
                @Override
                public Object work() throws SQLException {
                    Iterator<Map.Entry<String, SwiftMetaData>> iterator = metaDatas.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, SwiftMetaData> entry = iterator.next();
                        SwiftMetaDataBean bean = (SwiftMetaDataBean) entry.getValue();
                        bean.setId(entry.getKey());
                        swiftMetaDataDao.addOrUpdateSwiftMetaData(bean);
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
            return (Boolean) transactionManager.doTransactionIfNeed(new AbstractTransactionWorker() {

                @Override
                public Object work() throws SQLException {
                    for (String sourceKey : sourceKeys) {
                        swiftMetaDataDao.deleteSwiftMetaDataBean(sourceKey);
                        metaDataCache.remove(sourceKey);
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
            List<SwiftMetaDataBean> beans = swiftMetaDataDao.findAll();
            Map<String, SwiftMetaData> result = new HashMap<String, SwiftMetaData>();
            for (SwiftMetaDataBean bean : beans) {
                result.put(bean.getId(), bean);
            }
            metaDataCache.putAll(result);
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
                metaData = swiftMetaDataDao.findBySourceKey(sourceKey);
                metaDataCache.put(sourceKey, metaData);
                return metaData;
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
            return metaDataCache.containsKey(sourceKey.getId()) || null != swiftMetaDataDao.findBySourceKey(sourceKey.getId());
        } catch (Exception e) {
            return false;
        }
    }
}
