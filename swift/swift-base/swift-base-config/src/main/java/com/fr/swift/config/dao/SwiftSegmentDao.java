package com.fr.swift.config.dao;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.FindList;
import com.fr.swift.cube.io.Types;
import com.fr.swift.segment.SegmentKey;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftSegmentDao extends SwiftConfigDao<SegmentKeyBean> {
    /**
     * 保存SegmentKeyBean
     *
     * @param bean
     * @return
     */
    boolean addOrUpdateSwiftSegment(ConfigSession session, SegmentKey bean) throws SQLException;

    /**
     * 根据SourceKey查找
     *
     * @param sourceKey
     * @return
     */
    List<SegmentKey> findBySourceKey(ConfigSession session, String sourceKey);

    List<SegmentKey> findBeanByStoreType(ConfigSession session, String sourceKey, Types.StoreType type) throws SQLException;

    List<SegmentKey> selectSelective(ConfigSession session, SegmentKey segmentKey);

    /**
     * 删除SourceKey下的所有SegmentKey
     *
     * @param sourceKey
     * @return
     */
    boolean deleteBySourceKey(ConfigSession session, String sourceKey) throws SQLException;

    /**
     * 根据保存类型删除
     *
     * @param storeType
     * @return
     * @throws SQLException
     */
    boolean deleteByStoreType(ConfigSession session, Types.StoreType storeType) throws SQLException;

    /**
     * 返回所有SegmentKey
     *
     * @return
     */
    FindList<SegmentKeyBean> findAll(ConfigSession session);
}
