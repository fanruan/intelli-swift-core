package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.oper.BaseTransactionWorker;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.source.SourceKey;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yee
 * @date 2018/7/24
 */
@SwiftBean
public class SwiftSegmentLocationServiceImpl implements SwiftSegmentLocationService {

    private SwiftSegmentLocationDao segmentLocationDao = SwiftContext.get().getBean(SwiftSegmentLocationDao.class);
    private TransactionManager tx = SwiftContext.get().getBean(TransactionManager.class);

    @Override
    public boolean delete(final String table, final String clusterId) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(final ConfigSession session) {
                    try {
                        for (SwiftSegmentLocationEntity sourceKey : segmentLocationDao.find(session,
                                ConfigWhereImpl.eq("id.clusterId", clusterId),
                                ConfigWhereImpl.eq("sourceKey", table))) {
                            session.delete(sourceKey);
                        }
                    } catch (Throwable e) {
                        SwiftLoggers.getLogger().warn(e);
                        return false;
                    }
                    return true;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn(e);
            return false;
        }
    }

    @Override
    public Map<SegmentKey, Set<String>> findLocationsBySegKeys(final Set<SegmentKey> segKeys) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<Map<SegmentKey, Set<String>>>() {
                @Override
                public Map<SegmentKey, Set<String>> work(ConfigSession session) throws SQLException {
                    Map<SegmentKey, Set<String>> segKeyToNodeIds = new HashMap<>();
                    for (SegmentKey segKey : segKeys) {
                        if (!segKeyToNodeIds.containsKey(segKey)) {
                            segKeyToNodeIds.put(segKey, new HashSet<String>());
                        }

                        List<SwiftSegmentLocationEntity> locationEntities = segKeyToNodeIds.isEmpty() ? Collections.<SwiftSegmentLocationEntity>emptyList() :
                                segmentLocationDao.find(session, ConfigWhereImpl.eq("id.segmentId", segKey.getId()));
                        for (SwiftSegmentLocationEntity locationEntity : locationEntities) {
                            segKeyToNodeIds.get(segKey).add(locationEntity.getClusterId());
                        }
                    }
                    return segKeyToNodeIds;
                }
            });
        } catch (SQLException e) {
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<String, List<SwiftSegmentLocationEntity>> findAll() {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<Map<String, List<SwiftSegmentLocationEntity>>>(false) {
                @Override
                public Map<String, List<SwiftSegmentLocationEntity>> work(ConfigSession session) {
                    final Map<String, List<SwiftSegmentLocationEntity>> result = new HashMap<String, List<SwiftSegmentLocationEntity>>();
                    try {
                        for (SwiftSegmentLocationEntity item : segmentLocationDao.findAll(session)) {
                            String sourceKey = item.getSourceKey();
                            if (null == result.get(sourceKey)) {
                                result.put(sourceKey, new ArrayList<SwiftSegmentLocationEntity>());
                            }
                            result.get(sourceKey).add(item);
                        }
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().warn(e);
                    }
                    return result;
                }
            });
        } catch (SQLException e) {
            return Collections.emptyMap();
        }
    }

    @Override
    public List<SwiftSegmentLocationEntity> find(final ConfigWhere... criterion) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<List<SwiftSegmentLocationEntity>>(false) {
                @Override
                public List<SwiftSegmentLocationEntity> work(ConfigSession session) {
                    return segmentLocationDao.find(session, criterion);
                }
            });
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean saveOrUpdate(final SwiftSegmentLocationEntity obj) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) throws SQLException {
                    return segmentLocationDao.saveOrUpdate(session, obj);
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().warn(e);
            return false;
        }
    }

    @Override
    public List<SwiftSegmentLocationEntity> findBySourceKey(final SourceKey sourceKey) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<List<SwiftSegmentLocationEntity>>(false) {
                @Override
                public List<SwiftSegmentLocationEntity> work(ConfigSession session) {
                    return segmentLocationDao.findBySourceKey(session, sourceKey.getId());
                }
            });
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void delete(final Set<SegmentKey> segKeys) {
        try {
            tx.doTransactionIfNeed(new BaseTransactionWorker<Void>() {
                @Override
                public Void work(ConfigSession session) throws SQLException {
                    Set<String> segIds = new HashSet<>();
                    for (SegmentKey segKey : segKeys) {
                        segIds.add(segKey.getId());
                    }
                    List<SwiftSegmentLocationEntity> segLocations = segIds.isEmpty() ? Collections.<SwiftSegmentLocationEntity>emptyList() :
                            segmentLocationDao.find(session,
                                    ConfigWhereImpl.in("id.segmentId", segIds));
                    for (SwiftSegmentLocationEntity segLocation : segLocations) {
                        segmentLocationDao.delete(session, segLocation);
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private static SwiftSegmentLocationEntity toLocalSegLocation(SegmentKey segKey) {
        return new SwiftSegmentLocationEntity(SwiftProperty.getProperty().getClusterId(), segKey.getId(), segKey.getTable().getId());
    }

    @Override
    public boolean containsLocal(final SegmentKey segKey) {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<Boolean>() {
                @Override
                public Boolean work(ConfigSession session) {
                    return !segmentLocationDao.find(session,
                            ConfigWhereImpl.eq("id.clusterId", SwiftProperty.getProperty().getClusterId()),
                            ConfigWhereImpl.eq("id.segmentId", segKey.getId())).isEmpty();
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
            return false;
        }
    }

    @Override
    public void saveOrUpdateLocal(final Set<SegmentKey> segKeys) {
        try {
            tx.doTransactionIfNeed(new BaseTransactionWorker<Void>() {
                @Override
                public Void work(ConfigSession session) {
                    for (SegmentKey segKey : segKeys) {
                        try {
                            segmentLocationDao.saveOrUpdate(session, toLocalSegLocation(segKey));
                            for (SegmentContainer value : SegmentContainer.values()) {
                                value.register(segKey);
                            }
                        } catch (SQLException e) {
                            SwiftLoggers.getLogger().error(e);
                        }
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public Map<SourceKey, List<SwiftSegmentLocationEntity>> getAllLocal() {
        try {
            return tx.doTransactionIfNeed(new BaseTransactionWorker<Map<SourceKey, List<SwiftSegmentLocationEntity>>>() {
                @Override
                public Map<SourceKey, List<SwiftSegmentLocationEntity>> work(ConfigSession session) {
                    List<SwiftSegmentLocationEntity> localSegLocations = segmentLocationDao.find(session,
                            ConfigWhereImpl.eq("id.clusterId", SwiftProperty.getProperty().getClusterId()));

                    Map<SourceKey, List<SwiftSegmentLocationEntity>> tableToLocations = new HashMap<>();
                    for (SwiftSegmentLocationEntity localSegLocation : localSegLocations) {
                        SourceKey tableKey = new SourceKey(localSegLocation.getSourceKey());
                        if (!tableToLocations.containsKey(tableKey)) {
                            tableToLocations.put(tableKey, new ArrayList<SwiftSegmentLocationEntity>());
                        }
                        tableToLocations.get(tableKey).add(localSegLocation);
                    }
                    return tableToLocations;
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
            return Collections.emptyMap();
        }
    }
}
