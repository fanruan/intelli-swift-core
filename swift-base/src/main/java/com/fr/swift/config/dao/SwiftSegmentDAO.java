package com.fr.swift.config.dao;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.cube.io.Types;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftSegmentDAO extends SwiftConfigDAO<SwiftSegmentEntity> {
    /**
     * 保存SegmentKeyBean
     *
     * @param bean
     * @return
     */
    boolean addOrUpdateSwiftSegment(SegmentKeyBean bean) throws SQLException;

    /**
     * 根据SourceKey查找
     *
     * @param sourceKey
     * @return
     */
    List<SegmentKeyBean> findBySourceKey(String sourceKey);

    List<SegmentKeyBean> findBeanByStoreType(String sourceKey, Types.StoreType type) throws SQLException;

    boolean contains(SegmentKeyBean bean) throws SQLException;

    /**
     * 删除SourceKey下的所有SegmentKey
     *
     * @param sourceKey
     * @return
     */
    boolean deleteBySourceKey(String sourceKey) throws SQLException;

    /**
     * 根据保存类型删除
     *
     * @param storeType
     * @return
     * @throws SQLException
     */
    boolean deleteByStoreType(Types.StoreType storeType) throws SQLException;

    /**
     * 返回所有SegmentKey
     *
     * @return
     */
    List<SegmentKeyBean> findAll();
}
