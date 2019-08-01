package com.fr.swift.query.info.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.info.SegmentFilter;
import com.fr.swift.query.info.SingleTableQueryInfo;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.impl.hash.HashAllotRule;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Create by lifan on 2019-07-24 17:00
 */
public abstract class AbstractSegmentFilter implements SegmentFilter {

    protected final static int ALL_SEGMENT = -1;

    protected static final SwiftSegmentManager SEG_SVC = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    protected SwiftTableAllotRule tableAllotRule;
    protected SwiftSegmentBucket segmentBucket;
    protected Map<Integer, List<SegmentKey>> bucketMap;

    public AbstractSegmentFilter(SwiftTableAllotRule tableAllotRule, SwiftSegmentBucket segmentBucket) {
        this.tableAllotRule = tableAllotRule;
        this.segmentBucket = segmentBucket;
        this.bucketMap = segmentBucket.getBucketMap();
    }

    public List<Segment> filter(SingleTableQueryInfo singleTableQueryInfo) throws SwiftMetaDataException {
        if (tableAllotRule == null || segmentBucket == null || singleTableQueryInfo.getFilterInfo() == null) {
            return SEG_SVC.getSegmentsByIds(singleTableQueryInfo.getTable(), singleTableQueryInfo.getQuerySegment());
        }
        // List<Segment> segments = SEG_SVC.getSegmentsByIds(detailQueryInfo.getTable(), detailQueryInfo.getQuerySegment());
        Set<Integer> virtualOrders = getIndexSet(singleTableQueryInfo.getFilterInfo(), singleTableQueryInfo.getTable());
        if (virtualOrders.contains(-1)) {
            return SEG_SVC.getSegmentsByIds(singleTableQueryInfo.getTable(), singleTableQueryInfo.getQuerySegment());
        }
        return filterSegment(virtualOrders);

    }

    public abstract List<Segment> filterSegment(Set<Integer> virtualOrders);

    public Set<Integer> getIndexSet(FilterInfo filterInfo, SourceKey table) throws SwiftMetaDataException {
        Set<Integer> set = new HashSet<Integer>();
        if (filterInfo instanceof GeneralFilterInfo) {
            GeneralFilterInfo generalFilterInfo = (GeneralFilterInfo) filterInfo;
            List<FilterInfo> childrenFilterInfoList = generalFilterInfo.getChildren();
            if (generalFilterInfo.getType() == GeneralFilterInfo.OR) {
                for (FilterInfo filter : childrenFilterInfoList) {
                    set.addAll(getIndexSet(filter, table));
                }
                if (set.contains(ALL_SEGMENT)) {
                    Set<Integer> orSet = new HashSet<Integer>();
                    orSet.add(ALL_SEGMENT);
                    set = orSet;
                }
            } else {
                for (FilterInfo filter : childrenFilterInfoList) {
                    set.addAll(getIndexSet(filter, table));
                }
                if (set.contains(ALL_SEGMENT) && set.size() != 1) {
                    set.remove(ALL_SEGMENT);
                }
            }
        } else if (filterInfo instanceof SwiftDetailFilterInfo) {
            if (((SwiftDetailFilterInfo) filterInfo).getType() == SwiftDetailFilterType.IN) {
                // getVirtualOrder
                set.addAll(getVirtualOrder((SwiftDetailFilterInfo) filterInfo, table));
            } else {
                //所有都要查
                set.add(ALL_SEGMENT);
            }
        } else {
            //所有都要查
            set.add(ALL_SEGMENT);
        }
        return set;
    }

    private Set<Integer> getVirtualOrder(SwiftDetailFilterInfo filterInfo, SourceKey table) throws SwiftMetaDataException {
        //获取hash后对应桶中的segments
        AllotRule allotRule = this.tableAllotRule.getAllotRule();
        Set<Object> filterValues = (Set<Object>) filterInfo.getFilterValue();

        HashAllotRule hashAllotRule = (HashAllotRule) allotRule;
        //计算所有字段名
        int hashFieldIndex = hashAllotRule.getFieldIndex();
        SwiftMetaDataColumn swiftMetaDataColumn = SwiftDatabase.getInstance().getTable(table).getMetadata().getColumn(hashFieldIndex + 1);
        String hashColumnName = swiftMetaDataColumn.getName(); //hash字段名

        if (filterInfo.getColumnKey().getName().equals(hashColumnName)) {
            //计算索引值 key的order 根据filterValue计算
            Set<Integer> hashIndexSet = new HashSet<Integer>();
            for (Object filterValue : filterValues) {
                Integer hashIndex = hashAllotRule.getHashFunction().indexOf(filterValue);
                hashIndexSet.add(hashIndex);
            }
            return hashIndexSet;
        } else {
            //所有都要查
            Set<Integer> allQuerySet = new HashSet<Integer>();
            allQuerySet.add(ALL_SEGMENT);
            return allQuerySet;
        }
    }


}
