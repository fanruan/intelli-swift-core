package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Util;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author yee
 * @date 2018/7/24
 * @description seglocation db service
 */
@SwiftBean(name = "swiftSegmentLocationService")
public class SwiftSegmentLocationServiceImpl implements SwiftSegmentLocationService {
    private static final String NODE_ID = "id.clusterId";
    private static final String SEG_ID = "id.segmentId";
    private static final String TABLE_KEY = "sourceKey";

    private SwiftDao<SwiftSegmentLocationEntity> dao = new SwiftDaoImpl<>(SwiftSegmentLocationEntity.class);

    @Override
    public void saveOnNode(String nodeId, Set<SegmentKey> segKeys) {
        List<SwiftSegmentLocationEntity> segLocations = new ArrayList<>();
        for (SegmentKey segKey : segKeys) {
            segLocations.add(new SwiftSegmentLocationEntity(nodeId, segKey.getId(), segKey.getTable().getId()));
        }
        dao.insert(segLocations);
    }

    @Override
    public void deleteOnNode(String nodeId, Set<SegmentKey> segKeys) {
        List<SwiftSegmentLocationEntity> segLocations = new ArrayList<>();
        for (SegmentKey segKey : segKeys) {
            segLocations.add(new SwiftSegmentLocationEntity(nodeId, segKey.getId(), segKey.getTable().getId()));
        }
        dao.delete(segLocations);
    }

    @Override
    public void deleteOnNode(final String nodeId, final SourceKey tableKey) {
        dao.delete(criteria -> criteria.add(Restrictions.eq(NODE_ID, nodeId))
                .add(Restrictions.eq(TABLE_KEY, tableKey.getId())));
    }

    @Override
    public boolean existsOnNode(final String nodeId, final SegmentKey segKey) {
        final List<?> select = dao.select(criteria -> criteria.add(Restrictions.eq(NODE_ID, nodeId))
                .add(Restrictions.eq(SEG_ID, segKey.getId()))
                .setMaxResults(1));
        return !select.isEmpty();
    }

    @Override
    public List<SwiftSegmentLocationEntity> getTableMatchedSegOnNode(final String nodeId, final SourceKey tableKey, final String segIdStartsWith) {
        final List<?> select = dao.select(criteria -> criteria.add(Restrictions.eq(NODE_ID, nodeId))
                .add(Restrictions.eq(TABLE_KEY, tableKey.getId()))
                .add(Restrictions.like(SEG_ID, segIdStartsWith, MatchMode.START)));
        return (List<SwiftSegmentLocationEntity>) select;
    }

    @Override
    public List<SwiftSegmentLocationEntity> getTableSegsByClusterId(final SourceKey sourceKey, final String clusterId) {
        List<?> select = dao.select(criteria -> criteria.add(Restrictions.eq(TABLE_KEY, sourceKey.getId()))
                .add(Restrictions.eq(NODE_ID, clusterId)));
        return (List<SwiftSegmentLocationEntity>) select;
    }

    @Override
    public List<SwiftSegmentLocationEntity> getSegLocations(final String clusterId, final SourceKey tableKey) {
        List<?> select = dao.select(criteria -> criteria.add(Restrictions.eq(NODE_ID, clusterId))
                .add(Restrictions.eq(TABLE_KEY, tableKey.getId())));
        return (List<SwiftSegmentLocationEntity>) select;
    }

    @Override
    public List<SwiftSegmentLocationEntity> getTableMatchedSegOnNode(final String nodeId, final SourceKey tableKey, final List<String> inSegIds) {
        if (inSegIds == null || inSegIds.isEmpty()) {
            return Collections.emptyList();
        }
        final List<SwiftSegmentLocationEntity> entities = new ArrayList<>();
        for (final List<String> slice : Util.toSlices(inSegIds, 500)) {
            final List<?> select = dao.select(criteria -> criteria.add(Restrictions.eq(NODE_ID, nodeId))
                    .add(Restrictions.eq(TABLE_KEY, tableKey.getId()))
                    .add(Restrictions.in(SEG_ID, slice)));
            entities.addAll((List<SwiftSegmentLocationEntity>) select);
        }
        return entities;
    }
}
