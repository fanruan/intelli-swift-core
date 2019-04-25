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
import com.fr.swift.source.SourceKey;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

                @Override
                public boolean needTransaction() {
                    return false;
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

                @Override
                public boolean needTransaction() {
                    return false;
                }
            });
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }
}
