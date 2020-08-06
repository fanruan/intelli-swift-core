package com.fr.swift.query.info.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.config.service.SwiftSegmentService;
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
import com.fr.swift.segment.SegmentService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.hash.HashAllotRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Create by lifan on 2019-07-24 17:00
 */
public abstract class AbstractSegmentFilter implements SegmentFilter {

    protected final static int ALL_SEGMENT = -1;

    protected static final SegmentService SEG_SVC = SwiftContext.get().getBean(SegmentService.class);
    protected static final SwiftSegmentService SWIFT_SEG_SVC = SwiftContext.get().getBean(SwiftSegmentService.class);

    protected SwiftTableAllotRule tableAllotRule;
    protected SwiftSegmentBucket segmentBucket;
    protected Map<Integer, List<SegmentKey>> bucketMap;
    protected SegmentFuzzyBucket segmentFuzzyBucket;

    public AbstractSegmentFilter(SwiftTableAllotRule tableAllotRule, SwiftSegmentBucket segmentBucket) {
        this.tableAllotRule = tableAllotRule;
        this.segmentBucket = segmentBucket;
        this.bucketMap = segmentBucket.getBucketMap();
        this.segmentFuzzyBucket = new SegmentFuzzyBucket(segmentBucket);
    }

    @Override
    public List<Segment> filterSegs(SingleTableQueryInfo singleTableQueryInfo) throws SwiftMetaDataException {
        List<SegmentKey> segmentKeyList = filterSegKeys(singleTableQueryInfo);
        return segmentKeyList.stream().map(SEG_SVC::getSegment).collect(Collectors.toList());
    }

    @Override
    public List<SegmentKey> filterSegKeys(SingleTableQueryInfo singleTableQueryInfo) throws SwiftMetaDataException {
        if (singleTableQueryInfo.getQuerySegment() == null || singleTableQueryInfo.getQuerySegment().isEmpty()) {
            return filter(singleTableQueryInfo);
        } else {
            if (isDataEmpty(singleTableQueryInfo)) {
                return new ArrayList<>();
            }
            //允许exact query容错
            List<SegmentKey> filteredSegKeys = exactFilter(singleTableQueryInfo);
            if (filteredSegKeys.isEmpty()) {
                singleTableQueryInfo.setQuerySegment(null);
                filteredSegKeys = filter(singleTableQueryInfo);
            }
            return filteredSegKeys;
        }
    }

    private boolean isDataEmpty(SingleTableQueryInfo singleTableQueryInfo) {
        Set<String> querySegments = singleTableQueryInfo.getQuerySegment();
        if (querySegments.size() == 1) {
            String segKey = (String) querySegments.toArray()[0];
            if (segKey.contains("@FINE_IO@-1")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 非精确计算
     *
     * @param singleTableQueryInfo
     * @return
     * @throws SwiftMetaDataException
     */
    private List<SegmentKey> filter(SingleTableQueryInfo singleTableQueryInfo) throws SwiftMetaDataException {
        if (isLineAllot(singleTableQueryInfo)) {
            return SEG_SVC.getSegmentKeysByIds(singleTableQueryInfo.getTable(), singleTableQueryInfo.getQuerySegment());
        }
        Set<Integer> virtualOrders = getIndexSet(singleTableQueryInfo.getFilterInfo(), singleTableQueryInfo.getTable());
        return filterSegment(virtualOrders, singleTableQueryInfo);
    }

    /**
     * 精确计算segments
     *
     * @param singleTableQueryInfo
     * @return
     * @throws SwiftMetaDataException
     */
    private List<SegmentKey> exactFilter(SingleTableQueryInfo singleTableQueryInfo) {
        return SEG_SVC.getSegmentKeysByIds(singleTableQueryInfo.getTable(), singleTableQueryInfo.getQuerySegment());
    }

    private boolean isLineAllot(SingleTableQueryInfo singleTableQueryInfo) {
        if (tableAllotRule == null || segmentBucket == null
                || singleTableQueryInfo.getFilterInfo() == null || tableAllotRule.getAllotRule().getType() == BaseAllotRule.AllotType.LINE) {
            return true;
        }
        return false;
    }

    protected abstract List<SegmentKey> filterSegment(Set<Integer> virtualOrders, SingleTableQueryInfo singleTableQueryInfo);

    /**
     * filter 递归逻辑拆解
     */
    public Set<Integer> getIndexSet(FilterInfo filterInfo, SourceKey table) throws SwiftMetaDataException {
        Set<Integer> set = new HashSet<>();
        if (filterInfo instanceof GeneralFilterInfo) {
            GeneralFilterInfo generalFilterInfo = (GeneralFilterInfo) filterInfo;
            List<FilterInfo> childrenFilterInfoList = generalFilterInfo.getChildren();
            if (generalFilterInfo.getType() == GeneralFilterInfo.OR) {
                for (FilterInfo filter : childrenFilterInfoList) {
                    set.addAll(getIndexSet(filter, table));
                }
                if (set.contains(ALL_SEGMENT)) {
                    Set<Integer> orSet = new HashSet<>();
                    orSet.add(ALL_SEGMENT);
                    set = orSet;
                }
            } else {
                for (FilterInfo filter : childrenFilterInfoList) {
                    Set<Integer> subIndexSet = getIndexSet(filter, table);
                    if (subIndexSet.contains(ALL_SEGMENT) && subIndexSet.size() == 1) {
                        continue;
                    }
                    if (set.isEmpty()) {
                        set.addAll(subIndexSet);
                    } else {
                        set.retainAll(subIndexSet);
                    }
                }
                set.add(ALL_SEGMENT);
                if (set.contains(ALL_SEGMENT) && set.size() != 1) {
                    set.remove(ALL_SEGMENT);
                }
            }
        } else if (filterInfo instanceof SwiftDetailFilterInfo) {
            if (((SwiftDetailFilterInfo) filterInfo).getType() == SwiftDetailFilterType.IN) {
                set.addAll(segmentFuzzyBucket.getIncludedKey(getSingleKeyVirtualOrder((SwiftDetailFilterInfo) filterInfo, table)));
            } else {
                set.add(ALL_SEGMENT);
            }
        }
        // TODO: 2020/6/1  not 有逻辑 bug
//        else if (filterInfo instanceof NotFilterInfo) {
//            set.addAll(segmentFuzzyBucket.getNotIncludedKey(getIndexSet(((NotFilterInfo) filterInfo).getFilterInfo(), table)));
//        }
        else {
            set.add(ALL_SEGMENT);
        }
        return set;
    }

    private Set<Integer> getSingleKeyVirtualOrder(SwiftDetailFilterInfo filterInfo, SourceKey table) throws SwiftMetaDataException {
        AllotRule allotRule = this.tableAllotRule.getAllotRule();
        HashAllotRule hashAllotRule = (HashAllotRule) allotRule;

        int[] fieldIndexes = hashAllotRule.getFieldIndexes();
        SwiftMetaData metadata = SwiftDatabase.getInstance().getTable(table).getMetadata();
        Set<String> hashKeys = new HashSet<>();
        for (int fieldIndex : fieldIndexes) {
            hashKeys.add(metadata.getColumnName(fieldIndex + 1));
        }

        String columnKey = filterInfo.getColumnKey().getName();
        if (hashKeys.contains(columnKey)) {
            Set<Integer> hashIndexSet = new HashSet<>();
            Set<Object> filterValues = (Set<Object>) filterInfo.getFilterValue();
            for (Object filterValue : filterValues) {
                Integer hashIndex = hashAllotRule.getHashFunction().indexOf(filterValue);
                hashIndexSet.add(hashIndex);
            }
            return hashIndexSet;
        } else {
            Set<Integer> allQuerySet = new HashSet<>();
            allQuerySet.add(ALL_SEGMENT);
            return allQuerySet;
        }
    }
}