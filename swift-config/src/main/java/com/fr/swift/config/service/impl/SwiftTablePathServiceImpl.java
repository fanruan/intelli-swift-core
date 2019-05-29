package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftTablePathDao;
import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.entity.key.SwiftTablePathKey;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.event.ClusterEvent;
import com.fr.swift.event.ClusterEventListener;
import com.fr.swift.event.ClusterListenerHandler;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/7/18
 */
@SwiftBean
public class SwiftTablePathServiceImpl implements SwiftTablePathService {
    private SwiftTablePathDao swiftTablePathDao = SwiftContext.get().getBean(SwiftTablePathDao.class);
    private TransactionManager tx = SwiftContext.get().getBean(TransactionManager.class);
    private ConcurrentHashMap<String, SwiftTablePathEntity> tablePath = new ConcurrentHashMap<String, SwiftTablePathEntity>();

    public SwiftTablePathServiceImpl() {
        ClusterListenerHandler.addExtraListener(new ClusterEventListener() {
            @Override
            public void handleEvent(ClusterEvent clusterEvent) {
                tablePath.clear();
            }
        });
    }

    @Override
    public List<SwiftTablePathEntity> find(final ConfigWhere... criterion) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<List<SwiftTablePathEntity>>(false) {
                @Override
                public List<SwiftTablePathEntity> work(ConfigSession session) {
                    return swiftTablePathDao.find(session, criterion);
                }
            });
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean saveOrUpdate(final SwiftTablePathEntity entity) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    entity.getId().setClusterId(SwiftProperty.getProperty().getClusterId());
                    boolean success = swiftTablePathDao.saveOrUpdate(session, entity);
                    if (success) {
                        tablePath.put(entity.getId().getTableKey().getId(), entity);
                        return true;
                    }
                    return false;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn("save or update table path error", e);
            return false;
        }
    }

    @Override
    public boolean removePath(final String table) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(final ConfigSession session) throws SQLException {
                    try {
                        SwiftTablePathKey key = new SwiftTablePathKey(table, SwiftProperty.getProperty().getClusterId());

                        SwiftTablePathEntity item = swiftTablePathDao.select(session, key);
                        session.delete(item);
                        tablePath.remove(table);
                        return true;
                    } catch (Throwable e) {
                        SwiftLoggers.getLogger().warn(e);
                    }
                    return false;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn("remove table path error", e);
            return false;
        }
    }

    @Override
    public Integer getTablePath(final String table) {
        try {
            SwiftTablePathEntity path = tablePath.get(table);
            if (null == path) {
                return tx.doTransactionIfNeed(new BaseTransactionWorker<Integer>() {
                    @Override
                    public Integer work(ConfigSession session) throws SQLException {
                        try {
                            SwiftTablePathKey key = new SwiftTablePathKey(table, SwiftProperty.getProperty().getClusterId());
                            SwiftTablePathEntity entity = swiftTablePathDao.select(session, key);
                            if (null != entity) {
                                Integer path = entity.getTablePath();
                                if (null != path && path > -1) {
                                    tablePath.put(table, entity);
                                    return path;
                                }
                                return 0;
                            }
                            return 0;
                        } catch (Exception e) {
                            SwiftLoggers.getLogger().warn(e);
                            return 0;
                        }
                    }
                });
            }
            return path.getTablePath() == null ? 0 : path.getTablePath();
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public Integer getLastPath(final String table) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<Integer>() {
                @Override
                public Integer work(ConfigSession session) throws SQLException {
                    try {
                        SwiftTablePathKey key = new SwiftTablePathKey(table, SwiftProperty.getProperty().getClusterId());
                        SwiftTablePathEntity entity = swiftTablePathDao.select(session, key);
                        if (null != entity) {
                            return entity.getLastPath();
                        }
                        return 0;
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().warn(e);
                        return 0;
                    }
                }
            });
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public SwiftTablePathEntity get(final String table) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<SwiftTablePathEntity>() {
                @Override
                public SwiftTablePathEntity work(ConfigSession session) throws SQLException {
                    try {
                        SwiftTablePathKey key = new SwiftTablePathKey(table, SwiftProperty.getProperty().getClusterId());
                        SwiftTablePathEntity entity = swiftTablePathDao.select(session, key);
                        if (null != entity) {
                            tablePath.put(table, entity);
                        }
                        return entity;
                    } catch (Exception e) {
                        throw new SQLException(e);
                    }
                }
            });
        } catch (SQLException e) {
            return null;
        }
    }
}
