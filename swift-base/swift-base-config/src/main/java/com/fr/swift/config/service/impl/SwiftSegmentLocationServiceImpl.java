package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Util;

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
    private static final String ID = "id";
    private static final String CLUSTER_ID = "clusterId";
    private static final String SEGMENT_ID = "segmentId";
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
        dao.deleteQuery((query, builder, from) ->
                query.select(from)
                        .where(builder.equal(from.get(ID).get(CLUSTER_ID), nodeId), builder.equal(from.get(TABLE_KEY), tableKey.getId())));
    }

    @Override
    public boolean existsOnNode(final String nodeId, final SegmentKey segKey) {
        List<?> select = dao.selectQuery((query, builder, from) ->
                query.select(from)
                        .where(builder.equal(from.get(ID).get(CLUSTER_ID), nodeId), builder.equal(from.get(ID).get(SEGMENT_ID), segKey.getId()))
        );
        return !select.isEmpty();
    }

    @Override
    public List<SwiftSegmentLocationEntity> getTableMatchedSegOnNode(final String nodeId, final SourceKey tableKey, final String segIdStartsWith) {
        List<?> select = dao.selectQuery((query, builder, from) ->
                query.select(from)
                        .where(builder.equal(from.get(ID).get(CLUSTER_ID), nodeId)
                                , builder.equal(from.get(TABLE_KEY), tableKey.getId())
                                , builder.like(from.get(ID).get(SEGMENT_ID), "%" + segIdStartsWith))
        );
        return (List<SwiftSegmentLocationEntity>) select;
    }

    @Override
    public List<SwiftSegmentLocationEntity> getTableSegsByClusterId(final SourceKey sourceKey, final String clusterId) {

        List<?> select = dao.selectQuery((query, builder, from) ->
                query.select(from)
                        .where(builder.equal(from.get(TABLE_KEY), sourceKey.getId())
                                , builder.equal(from.get(ID).get(CLUSTER_ID), clusterId))
        );
        return (List<SwiftSegmentLocationEntity>) select;
    }

    @Override
    public List<SwiftSegmentLocationEntity> getSegLocations(final String clusterId, final SourceKey tableKey) {

        List<?> select = dao.selectQuery((query, builder, from) ->
                query.select(from)
                        .where(builder.equal(from.get(ID).get(CLUSTER_ID), clusterId)
                                , builder.equal(from.get(TABLE_KEY), tableKey.getId()))
        );
        return (List<SwiftSegmentLocationEntity>) select;
    }

    @Override
    public List<SwiftSegmentLocationEntity> getTableMatchedSegOnNode(final String nodeId, final SourceKey tableKey, final List<String> inSegIds) {
        if (inSegIds == null || inSegIds.isEmpty()) {
            return Collections.emptyList();
        }
        final List<SwiftSegmentLocationEntity> entities = new ArrayList<>();
        for (final List<String> slice : Util.toSlices(inSegIds, 500)) {

            List<?> select = dao.selectQuery((query, builder, from) ->
                    query.select(from)
                            .where(builder.equal(from.get(ID).get(CLUSTER_ID), nodeId)
                                    , builder.equal(from.get(TABLE_KEY), tableKey.getId())
                                    , from.get(ID).get(SEGMENT_ID).in(slice))
            );
            entities.addAll((List<SwiftSegmentLocationEntity>) select);
        }
        return entities;
    }
}
