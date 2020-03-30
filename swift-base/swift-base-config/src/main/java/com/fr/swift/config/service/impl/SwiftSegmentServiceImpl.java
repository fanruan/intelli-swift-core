package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.entity.SwiftSegmentLocationEntity;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Util;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.fr.swift.config.SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_ORDER;
import static com.fr.swift.config.SwiftConfigConstants.SegmentConfig.COLUMN_SEGMENT_OWNER;
import static com.fr.swift.config.SwiftConfigConstants.SegmentConfig.COLUMN_STORE_TYPE;

/**
 * @author yee
 * @date 2018/6/7
 */
@SwiftBean(name = "swiftSegmentService")
public class SwiftSegmentServiceImpl implements SwiftSegmentService {
    private SwiftDao<SegmentKey> dao = new SwiftDaoImpl<>(SwiftSegmentEntity.class);

    private SwiftMetaDataService metaDataService = SwiftContext.get().getBean(SwiftMetaDataService.class);

    private SwiftSegmentLocationService locationService = SwiftContext.get().getBean(SwiftSegmentLocationService.class);


    @Override
    public void save(SegmentKey segKey) {
        dao.insert(new SwiftSegmentEntity(segKey));
    }

    @Override
    public void delete(SegmentKey segKey) {
        dao.delete(new SwiftSegmentEntity(segKey));
    }

    @Override
    public void delete(List<SegmentKey> segKeys) {
        List<SegmentKey> entities = new ArrayList<>();
        for (SegmentKey segKey : segKeys) {
            entities.add(new SwiftSegmentEntity(segKey));
        }
        dao.delete(entities);
    }

    @Override
    public List<SegmentKey> getTableSegKeys(final SourceKey tableKey) {
        final List<?> segKeys = dao.select(criteria -> criteria.add(Restrictions.eq(COLUMN_SEGMENT_OWNER, tableKey.getId())));
        return (List<SegmentKey>) segKeys;
    }

    @Override
    public Set<SegmentKey> getByIds(final Set<String> segIds) {
        if (segIds == null || segIds.isEmpty()) {
            return Collections.emptySet();
        }
        final Set<SegmentKey> segKeys = new HashSet<>();
        for (final List<String> slice : Util.toSlices(segIds, 500)) {
            final List<?> select = dao.select(criteria -> criteria.add(Restrictions.in("id", slice)));
            segKeys.addAll((List<SegmentKey>) select);
        }
        return segKeys;
    }

    @Override
    public SegmentKey tryAppendSegment(final SourceKey tableKey, final StoreType storeType) {
        final SwiftDatabase swiftDatabase = metaDataService.getMeta(tableKey).getSwiftDatabase();
        for (; ; ) {
            try {
                final List<?> select = dao.select(criteria -> criteria.setProjection(Projections.max(COLUMN_SEGMENT_ORDER))
                        .add(Restrictions.eq(COLUMN_SEGMENT_OWNER, tableKey.getId()))
                        .add(Restrictions.eq(COLUMN_STORE_TYPE, storeType)));
                int maxOrder = select.get(0) == null ? -1 : (Integer) select.get(0);
                final SwiftSegmentEntity entity = new SwiftSegmentEntity(tableKey, maxOrder + 1, storeType, swiftDatabase);
                dao.insert(entity);
                return entity;
            } catch (ConstraintViolationException ignore) {
            }
        }
    }

    @Override
    public List<SegmentKey> getOrderedRealtimeSegKeyOnNode(final String nodeId, final SourceKey tableKey) {
        String hql = "select a from SwiftSegmentEntity a, SwiftSegmentLocationEntity b where a.id = b.id.segmentId and b.id.clusterId = :nodeId and b.sourceKey = :tableKey and a.storeType = :storeType order by a.segmentOrder";
        final List<?> select = dao.select(hql, query -> query.setParameter("nodeId", nodeId)
                .setParameter("tableKey", tableKey.getId())
                .setParameter("storeType", StoreType.MEMORY));
        return (List<SegmentKey>) select;
    }

    @Override
    public List<SegmentKey> getRealtimeSegKeyOnNode(final String nodeId) {
        String hql = "select a from SwiftSegmentEntity a, SwiftSegmentLocationEntity b where a.id = b.id.segmentId and b.id.clusterId = :nodeId and a.storeType = :storeType";
        final List<?> select = dao.select(hql, query -> query.setParameter("nodeId", nodeId)
                .setParameter("storeType", StoreType.MEMORY));
        return (List<SegmentKey>) select;
    }

    @Override
    public List<SegmentKey> getSegKeyOnNode(final String nodeId) {
        String hql = "select a from SwiftSegmentEntity a, SwiftSegmentLocationEntity b where a.id = b.id.segmentId and b.id.clusterId = :nodeId";
        final List<?> select = dao.select(hql, query -> query.setParameter("nodeId", nodeId));
        return (List<SegmentKey>) select;
    }

    @Override
    public Map<SourceKey, List<SegmentKey>> getTransferedSegments() {
        final Map<SourceKey, List<SegmentKey>> result = new HashMap<SourceKey, List<SegmentKey>>();
        final List<?> select = dao.select(criteria -> criteria.add(Restrictions.eq("storeType", StoreType.FINE_IO)));

        for (SegmentKey segmentKey : (List<SegmentKey>) select) {
            SourceKey sourceKey = segmentKey.getTable();
            if (!result.containsKey(sourceKey)) {
                result.put(sourceKey, new ArrayList<>());
            }
            result.get(sourceKey).add(segmentKey);
        }
        return result;
    }

    @Override
    public List<SegmentKey> getOwnSegments(final SourceKey tableKey) {
        // TODO: 2020/3/13
        String machineId = SwiftProperty.getProperty().getMachineId();
        final List<SwiftSegmentLocationEntity> list = locationService.getSegLocations(machineId, tableKey);
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        // oracle不支持超过1000个元素的in，切片后查询，将每次查询到的结果汇总，然后返回
        List<List<SwiftSegmentLocationEntity>> segLocationSlices = Util.toSlices(list, 500);
        List<SegmentKey> segKeys = new ArrayList<>();
        for (List<SwiftSegmentLocationEntity> slice : segLocationSlices) {
            if (slice.isEmpty()) {
                continue;
            }
            final Set<String> segIdSlice = new HashSet<>();
            for (SwiftSegmentLocationEntity segLocation : slice) {
                segIdSlice.add(segLocation.getSegmentId());
            }
            final List<?> select = dao.select(criteria -> criteria.add(Restrictions.in("id", segIdSlice)));
            segKeys.addAll((List<SegmentKey>) select);
        }
        return segKeys;
    }

}
