package com.fr.swift.config.dao;

import com.fr.swift.config.bean.SegmentBean;
import com.fr.swift.config.entity.SwiftSegmentEntity;

import java.util.List;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftSegmentDAO extends SwiftConfigDAO<SwiftSegmentEntity> {
    boolean addOrUpdateSwiftSegment(SegmentBean bean);

    List<SegmentBean> findBySourceKey(String sourceKey);

    boolean deleteBySourceKey(String sourceKey);

    List<SegmentBean> findAll();
}
