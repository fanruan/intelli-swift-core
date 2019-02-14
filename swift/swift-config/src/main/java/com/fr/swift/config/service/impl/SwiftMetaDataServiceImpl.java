package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftMetaDataDao;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.converter.FindList;
import com.fr.swift.event.global.CleanMetaDataCacheEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

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
 * @date 2018/6/6
 */
@SwiftBean
public class SwiftMetaDataServiceImpl implements SwiftMetaDataService {

    private TransactionManager transactionManager;
    private SwiftMetaDataDao swiftMetaDataDao;

    private ConcurrentHashMap<String, SwiftMetaData> metaDataCache = new ConcurrentHashMap<String, SwiftMetaData>();

    public SwiftMetaDataServiceImpl() {
        transactionManager = SwiftContext.get().getBean(TransactionManager.class);
        swiftMetaDataDao = SwiftContext.get().getBean(SwiftMetaDataDao.class);
    }

    @Override
    public boolean addMetaData(final String sourceKey, final SwiftMetaData metaData) {
        try {
            final SwiftMetaDataBean bean = (SwiftMetaDataBean) metaData;
            bean.setId(sourceKey);
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    swiftMetaDataDao.addOrUpdateSwiftMetaData(session, bean);
                    metaDataCache.put(sourceKey, bean);
                    return true;
                }


            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("Add or update metadata error!", e);
            return false;
        }
    }

    @Override
    public boolean addMetaDatas(final Map<String, SwiftMetaData> metaDatas) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    Iterator<Map.Entry<String, SwiftMetaData>> iterator = metaDatas.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, SwiftMetaData> entry = iterator.next();
                        SwiftMetaDataBean bean = (SwiftMetaDataBean) entry.getValue();
                        bean.setId(entry.getKey());
                        swiftMetaDataDao.addOrUpdateSwiftMetaData(session, bean);
                        metaDataCache.put(entry.getKey(), bean);
                    }
                    return true;
                }


            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("Add metadata error!", e);
            return false;
        }
    }

    @Override
    public boolean removeMetaDatas(final SourceKey... sourceKeys) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {

                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    for (SourceKey sourceKey : sourceKeys) {
                        swiftMetaDataDao.deleteSwiftMetaDataBean(session, sourceKey.getId());
                        metaDataCache.remove(sourceKey);
                    }
                    // 集群情况下才去发rpc
                    // 现在日志这边没必要
                    boolean isCluster = SwiftProperty.getProperty().isCluster();
                    if (sourceKeys.length > 0 && isCluster) {
                        ProxyFactory factory = ProxySelector.getInstance().getFactory();
                        SwiftServiceListenerHandler handler = factory.getProxy(RemoteSender.class);
                        handler.trigger(new CleanMetaDataCacheEvent(sourceKeys));
                    }
                    return true;
                }


            });

        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("Remove metadata error!", e);
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
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Map<String, SwiftMetaData>>(false) {
                @Override
                public Map<String, SwiftMetaData> work(ConfigSession session) throws SQLException {
                    try {
                        final Map<String, SwiftMetaData> result = new HashMap<String, SwiftMetaData>();
                        swiftMetaDataDao.findAll(session).forEach(new FindList.SimpleEach<SwiftMetaDataBean>() {
                            @Override
                            public void each(int idx, SwiftMetaDataBean bean) throws Exception {
                                result.put(bean.getId(), bean);
                            }
                        });
                        metaDataCache.putAll(result);
                        return result;
                    } catch (Exception e) {
                        if (e instanceof SQLException) {
                            throw (SQLException) e;
                        }
                        throw new SQLException(e);
                    }


                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });

        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("Select metadata error!", e);
            return new HashMap<String, SwiftMetaData>();
        }
    }

    @Override
    public SwiftMetaData getMetaDataByKey(final String sourceKey) {
        SwiftMetaData metaData = metaDataCache.get(sourceKey);
        if (null == metaData) {
            try {
                return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<SwiftMetaData>(false) {
                    @Override
                    public SwiftMetaData work(ConfigSession session) throws SQLException {
                        SwiftMetaData metaData = swiftMetaDataDao.findBySourceKey(session, sourceKey);
                        if (null != metaData) {
                            metaDataCache.put(sourceKey, metaData);
                        }
                        return metaData;
                    }
                });

            } catch (Exception e) {
                SwiftLoggers.getLogger().warn("Select metadata error!", e);
                return null;
            }
        }
        return metaData;

    }

    @Override
    public boolean containsMeta(final SourceKey sourceKey) {
        try {
            if (!metaDataCache.containsKey(sourceKey.getId())) {
                return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                    @Override
                    public Boolean work(ConfigSession session) throws SQLException {
                        SwiftMetaData metaData = swiftMetaDataDao.findBySourceKey(session, sourceKey.getId());
                        if (null != metaData) {
                            metaDataCache.put(sourceKey.getId(), metaData);
                            return true;
                        }
                        return false;
                    }
                });
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void cleanCache(String[] sourceKeys) {
        synchronized (metaDataCache) {
            if (null != sourceKeys) {
                for (String sourceKey : sourceKeys) {
                    metaDataCache.remove(sourceKey);
                }
            }
        }
    }

    @Override
    public List<SwiftMetaData> find(final ConfigWhere... criterion) {
        try {
            return transactionManager.doTransactionIfNeed(new BaseTransactionWorker<List<SwiftMetaData>>(false) {
                @Override
                public List<SwiftMetaData> work(ConfigSession session) {
                    return new ArrayList<SwiftMetaData>(swiftMetaDataDao.find(session, criterion).list());
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn(e);
            return Collections.emptyList();
        }
    }

    @Override
    public boolean saveOrUpdate(SwiftMetaData obj) {
        return addMetaData(obj.getId(), obj);
    }
}
