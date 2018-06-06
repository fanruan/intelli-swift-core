package com.fr.swift.config.service.impl;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.dao.SwiftSegmentDAO;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.config.transaction.SwiftSegmentTransactionWorker;
import com.fr.swift.config.transaction.SwiftTransactionManager;
import com.fr.swift.cube.io.Types;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.third.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/6
 */
@Service("swiftSegmentService")
public class SwiftSegmentServiceImpl implements SwiftSegmentService {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftSegmentServiceImpl.class);

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
    public boolean removeByStoreType(final Types.StoreType type) {
        try {
            return (Boolean) SwiftTransactionManager.doTransactionIfNeed(new SwiftSegmentTransactionWorker() {
                @Override
                public Object work(SwiftSegmentDAO dao) throws SQLException {
                    dao.deleteByStoreType(type);
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
    public List<SegmentKey> getUnStoreSegments(final String sourceKey) {
        try {
            return (List<SegmentKey>) SwiftTransactionManager.doTransactionIfNeed(new SwiftSegmentTransactionWorker() {
                @Override
                public Object work(SwiftSegmentDAO dao) throws SQLException {
                    return dao.findBeanByStoreType(sourceKey, Types.StoreType.MEMORY);
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
}
