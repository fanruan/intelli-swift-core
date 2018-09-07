package com.fr.swift.config.service.impl;

import com.fr.swift.config.convert.hibernate.transaction.AbstractTransactionWorker;
import com.fr.swift.config.convert.hibernate.transaction.HibernateTransactionManager;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.entity.key.SwiftTablePathKey;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterEventType;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.selector.ClusterSelector;
import com.fr.third.org.hibernate.Session;
import com.fr.third.org.hibernate.criterion.Criterion;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/7/18
 */
@Service
public class SwiftTablePathServiceImpl implements SwiftTablePathService {
    private final SwiftConfigDao<SwiftTablePathEntity> swiftTablePathDao = new BasicDao<SwiftTablePathEntity>(SwiftTablePathEntity.class);
    @Autowired
    private HibernateTransactionManager tx;
    private String clusterId = SwiftTablePathKey.LOCALHOST;
    private ConcurrentHashMap<String, Integer> tablePath = new ConcurrentHashMap<String, Integer>();

    public SwiftTablePathServiceImpl() {
        ClusterListenerHandler.addListener(new ClusterEventListener() {
            @Override
            public void handleEvent(ClusterEvent clusterEvent) {
                tablePath.clear();
                if (clusterEvent.getEventType() == ClusterEventType.JOIN_CLUSTER) {
                    clusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
                } else if (clusterEvent.getEventType() == ClusterEventType.LEFT_CLUSTER) {
                    clusterId = SwiftTablePathKey.LOCALHOST;
                }
            }
        });
    }

    @Override
    public List<SwiftTablePathEntity> find(final Criterion... criterion) {
        try {
            return tx.doTransactionIfNeed(new AbstractTransactionWorker<List<SwiftTablePathEntity>>() {
                @Override
                public List<SwiftTablePathEntity> work(Session session) {
                    return swiftTablePathDao.find(session, criterion);
                }

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean saveOrUpdate(final SwiftTablePathEntity entity) {
        try {
            return tx.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    entity.getId().setClusterId(clusterId);
                    boolean success = swiftTablePathDao.saveOrUpdate(session, entity);
                    if (success) {
                        if (null != entity.getTablePath()) {
                            tablePath.put(entity.getId().getTableKey().getId(), entity.getTablePath());
                        }
                        return true;
                    }
                    return false;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("save or update table path error", e);
            return false;
        }
    }

    @Override
    public boolean removePath(final String table) {
        try {
            return tx.doTransactionIfNeed(new AbstractTransactionWorker<Boolean>() {
                @Override
                public Boolean work(Session session) throws SQLException {
                    boolean success = swiftTablePathDao.deleteById(session, new SwiftTablePathKey(table, clusterId));
                    if (success) {
                        tablePath.remove(table);
                        return true;
                    }
                    return false;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("remove table path error", e);
            return false;
        }
    }

    @Override
    public Integer getTablePath(final String table) {
        try {
            Integer path = tablePath.get(table);
            if (null == path) {
                path = tx.doTransactionIfNeed(new AbstractTransactionWorker<Integer>() {
                    @Override
                    public Integer work(Session session) throws SQLException {
                        SwiftTablePathEntity entity = swiftTablePathDao.select(session, new SwiftTablePathKey(table, clusterId));
                        if (null != entity) {
                            Integer path = entity.getTablePath();
                            if (null != path && path.intValue() > -1) {
                                tablePath.put(table, path);
                                return path;
                            }
                            return 0;
                        }
                        return 0;
                    }
                });
            }
            return path;
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public Integer getLastPath(final String table) {
        try {
            return tx.doTransactionIfNeed(new AbstractTransactionWorker<Integer>() {
                @Override
                public Integer work(Session session) throws SQLException {
                    SwiftTablePathEntity entity = swiftTablePathDao.select(session, new SwiftTablePathKey(table, clusterId));
                    if (null != entity) {
                        return entity.getLastPath();
                    }
                    return 0;
                }
            });
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public SwiftTablePathEntity get(final String table) {
        try {
            return tx.doTransactionIfNeed(new AbstractTransactionWorker<SwiftTablePathEntity>() {
                @Override
                public SwiftTablePathEntity work(Session session) throws SQLException {
                    return swiftTablePathDao.select(session, new SwiftTablePathKey(table, clusterId));
                }
            });
        } catch (SQLException e) {
            return null;
        }
    }
}
