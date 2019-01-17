package com.fr.swift.config.dao;

import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.cube.io.Types;
import com.fr.swift.segment.SegmentKey;
import com.fr.third.org.hibernate.Session;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftSegmentDao extends SwiftConfigDao<SwiftSegmentEntity> {
    /**
     * 保存SegmentKeyBean
     *
     * @param bean
     * @return
     */
    boolean addOrUpdateSwiftSegment(Session session, SegmentKey bean) throws SQLException;

    /**
     * 根据SourceKey查找
     *
     * @param sourceKey
     * @return
     */
    List<SegmentKey> findBySourceKey(Session session, String sourceKey);

    List<SegmentKey> findBeanByStoreType(Session session, String sourceKey, Types.StoreType type) throws SQLException;

    List<SegmentKey> selectSelective(Session session, SegmentKey segmentKey);

    /**
     * 删除SourceKey下的所有SegmentKey
     *
     * @param sourceKey
     * @return
     */
    boolean deleteBySourceKey(Session session, String sourceKey) throws SQLException;

    /**
     * 根据保存类型删除
     *
     * @param storeType
     * @return
     * @throws SQLException
     */
    boolean deleteByStoreType(Session session, Types.StoreType storeType) throws SQLException;

    /**
     * 返回所有SegmentKey
     *
     * @return
     */
    List<SegmentKey> findAll(Session session);

    /**
     * key:segmentId
     * value:segmentKey
     * @param session
     * @return
     */
    Map<String,SegmentKey> findAllWithId(Session session);
}
