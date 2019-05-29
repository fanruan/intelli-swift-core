package com.fr.swift.config.dao.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.dao.BasicDao;
import com.fr.swift.config.dao.SwiftSegmentDao;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.cube.io.Types;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Strings;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yee
 * @date 2018/5/24
 */
@SwiftBean
public class SwiftSegmentDaoImpl extends BasicDao<SegmentKey> implements SwiftSegmentDao {

    public SwiftSegmentDaoImpl() {
        super(SwiftSegmentEntity.class);
    }

    @Override
    public boolean addOrUpdateSwiftSegment(ConfigSession session, SegmentKey bean) throws SQLException {
        return saveOrUpdate(session, bean);
    }

    @Override
    public List<SegmentKey> findBeanByStoreType(ConfigSession session, String sourceKey, Types.StoreType type) throws SQLException {
        if (Strings.isEmpty(sourceKey) || null == type) {
            throw new SQLException();
        }
        return find(session, ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey),
                ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE, type));
    }

    @Override
    public boolean deleteBySourceKey(final ConfigSession session, final String sourceKey) throws SQLException {
        try {
            List<SegmentKey> segmentKeys = find(session, ConfigWhereImpl.eq(SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER, sourceKey));
            for (SegmentKey segmentKey : segmentKeys) {
                session.delete(segmentKey);
            }
            return true;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public List<SegmentKey> findAll(ConfigSession session) {
        return find(session);
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> findSegmentKeyWithSourceKey(ConfigSession session, ConfigWhere... criteria) {
        final Map<SourceKey, List<SegmentKey>> result = new HashMap<SourceKey, List<SegmentKey>>();
        try {
            for (SegmentKey segmentKey : find(session, criteria)) {
                SourceKey sourceKey = segmentKey.getTable();
                if (!result.containsKey(sourceKey)) {
                    result.put(sourceKey, new ArrayList<SegmentKey>());
                }
                result.get(sourceKey).add(segmentKey);
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().warn("find segments failed", e);
        }
        return result;
    }
}
