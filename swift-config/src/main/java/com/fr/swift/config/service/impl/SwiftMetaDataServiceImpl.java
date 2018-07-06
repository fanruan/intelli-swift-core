package com.fr.swift.config.service.impl;

import com.fr.swift.Invoker;
import com.fr.swift.ProxyFactory;
import com.fr.swift.Result;
import com.fr.swift.URL;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.dao.SwiftMetaDataDao;
import com.fr.swift.config.hibernate.transaction.AbstractTransactionWorker;
import com.fr.swift.config.hibernate.transaction.HibernateTransactionManager;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.event.global.CleanMetaDataCacheEvent;
import com.fr.swift.invocation.SwiftInvocation;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.rpc.client.AsyncRpcCallback;
import com.fr.swift.rpc.client.async.RpcFuture;
import com.fr.swift.rpc.server.RpcServer;
import com.fr.swift.selector.ProxySelector;
import com.fr.swift.selector.UrlSelector;
import com.fr.swift.service.listener.SwiftServiceListenerHandler;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.third.org.hibernate.Session;
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
    private HibernateTransactionManager transactionManager;
    @Autowired
    private SwiftMetaDataDao swiftMetaDataDao;
    @Autowired(required = false)
    private RpcServer server;

    private ConcurrentHashMap<String, SwiftMetaData> metaDataCache = new ConcurrentHashMap<String, SwiftMetaData>();

    @Override
    public boolean addMetaData(final String sourceKey, final SwiftMetaData metaData) {
        try {
            final SwiftMetaDataBean bean = (SwiftMetaDataBean) metaData;
            bean.setId(sourceKey);
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    swiftMetaDataDao.addOrUpdateSwiftMetaData(session, bean);
                    metaDataCache.put(sourceKey, bean);
                    return true;
                }


            });
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Add or update metadata error!", e);
            return false;
        }
    }

    @Override
    public boolean addMetaDatas(final Map<String, SwiftMetaData> metaDatas) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
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
            SwiftLoggers.getLogger().error("Add metadata error!", e);
            return false;
        }
    }

    @Override
    public boolean removeMetaDatas(final String... sourceKeys) {
        try {
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {

                @Override
                public Boolean work(Session session) throws SQLException {
                    for (String sourceKey : sourceKeys) {
                        swiftMetaDataDao.deleteSwiftMetaDataBean(session, sourceKey);
                        metaDataCache.remove(sourceKey);
                    }
                    // 集群情况下才去发rpc
                    // 现在日志这边没必要
//                    boolean isCluster = SwiftContext.getInstance().getBean("swiftProperty", SwiftProperty.class).isCluster();
                    boolean isCluster = false;
                    if (null != sourceKeys && isCluster) {
                        URL masterURL = getMasterURL();
                        ProxyFactory factory = ProxySelector.getInstance().getFactory();
                        Invoker invoker = factory.getInvoker(null, SwiftServiceListenerHandler.class, masterURL, false);
                        Result result = invoker.invoke(new SwiftInvocation(server.getMethodByName("rpcTrigger"), new Object[]{new CleanMetaDataCacheEvent(sourceKeys)}));
                        RpcFuture future = (RpcFuture) result.getValue();
                        future.addCallback(new AsyncRpcCallback() {
                            @Override
                            public void success(Object result) {
                            }

                            @Override
                            public void fail(Exception e) {
                            }
                        });
                    }
                    return true;
                }


            });

        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Remove metadata error!", e);
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
            return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Map<String, SwiftMetaData>>() {
                @Override
                public Map<String, SwiftMetaData> work(Session session) {
                    List<SwiftMetaDataBean> beans = swiftMetaDataDao.findAll(session);
                    Map<String, SwiftMetaData> result = new HashMap<String, SwiftMetaData>();
                    for (SwiftMetaDataBean bean : beans) {
                        result.put(bean.getId(), bean);
                    }
                    metaDataCache.putAll(result);
                    return result;
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });

        } catch (Exception e) {
            SwiftLoggers.getLogger().error("Select metadata error!", e);
            return new HashMap<String, SwiftMetaData>();
        }
    }

    @Override
    public SwiftMetaData getMetaDataByKey(final String sourceKey) {
        SwiftMetaData metaData = metaDataCache.get(sourceKey);
        if (null == metaData) {
            try {
                return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<SwiftMetaData>() {
                    @Override
                    public SwiftMetaData work(Session session) throws SQLException {
                        SwiftMetaData metaData = swiftMetaDataDao.findBySourceKey(session, sourceKey);
                        metaDataCache.put(sourceKey, metaData);
                        return metaData;
                    }

                    @Override
                    public boolean needTransaction() {
                        return false;
                    }
                });

            } catch (Exception e) {
                SwiftLoggers.getLogger().error("Select metadata error!", e);
                return null;
            }
        }
        return metaData;

    }

    @Override
    public boolean containsMeta(final SourceKey sourceKey) {
        try {
            if (!metaDataCache.containsKey(sourceKey.getId())) {
                return transactionManager.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                    @Override
                    public Boolean work(Session session) throws SQLException {
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

    private URL getMasterURL() {
        List<SwiftServiceInfoBean> swiftServiceInfoBeans = SwiftContext.getInstance().getBean(SwiftServiceInfoService.class).getServiceInfoByService("cluster_master_service");
        SwiftServiceInfoBean swiftServiceInfoBean = swiftServiceInfoBeans.get(0);
        return UrlSelector.getInstance().getFactory().getURL(swiftServiceInfoBean.getServiceInfo());
    }
}
