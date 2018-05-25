package com.fr.swift.config.conf.dao;

import com.fr.swift.config.conf.bean.SegmentBean;
import com.fr.swift.config.conf.entity.SegmentEntity;

import java.util.List;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftSegmentDAO extends SwiftConfigDAO<SegmentEntity> {
    boolean addOrUpdateSwiftSegment(SegmentBean bean);

    List<SegmentBean> findBySourceKey(String sourceKey);

    boolean deleteBySourceKey(String sourceKey);

    List<SegmentBean> findAll();
}
