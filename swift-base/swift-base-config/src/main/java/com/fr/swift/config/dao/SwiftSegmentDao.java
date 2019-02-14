package com.fr.swift.config.dao;

import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.converter.FindList;
import com.fr.swift.cube.io.Types;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftSegmentDao extends SwiftConfigDao<SegmentKey> {
    /**
     * 保存SegmentKeyBean
     *
     * @param bean
     * @return
     */
    boolean addOrUpdateSwiftSegment(ConfigSession session, SegmentKey bean) throws SQLException;

    List<SegmentKey> findBeanByStoreType(ConfigSession session, String sourceKey, Types.StoreType type) throws SQLException;

    /**
     * 删除SourceKey下的所有SegmentKey
     *
     * @param sourceKey
     * @return
     */
    boolean deleteBySourceKey(ConfigSession session, String sourceKey) throws SQLException;

    /**
     * 返回所有SegmentKey
     *
     * @return
     */
    FindList<SegmentKey> findAll(ConfigSession session);

    Map<SourceKey, List<SegmentKey>> findSegmentKeyWithSourceKey(ConfigSession session, ConfigWhere... criteria);
}
