package com.fr.swift.config.dao;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.entity.SwiftSegmentEntity;

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

    /**
     * 删除SourceKey下的所有SegmentKey
     *
     * @param sourceKey
     * @return
     */
    boolean deleteBySourceKey(String sourceKey) throws SQLException;

    /**
     * 返回所有SegmentKey
     *
     * @return
     */
    List<SegmentKeyBean> findAll();
}
