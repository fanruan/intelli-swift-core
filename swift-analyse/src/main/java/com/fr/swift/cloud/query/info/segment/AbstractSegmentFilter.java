package com.fr.swift.cloud.query.info.segment;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.entity.SwiftSegmentBucket;
import com.fr.swift.cloud.config.entity.SwiftTableAllotRule;
import com.fr.swift.cloud.db.impl.SwiftDatabase;
import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.query.filter.SwiftDetailFilterType;
import com.fr.swift.cloud.query.filter.info.FilterInfo;
import com.fr.swift.cloud.query.filter.info.GeneralFilterInfo;
import com.fr.swift.cloud.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.cloud.query.info.SegmentFilter;
import com.fr.swift.cloud.query.info.SingleTableQueryInfo;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.source.alloter.AllotRule;
import com.fr.swift.cloud.source.alloter.impl.BaseAllotRule;
import com.fr.swift.cloud.source.alloter.impl.hash.HashAllotRule;

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

    protected SwiftTableAllotRule tableAllotRule;
    protected SwiftSegmentBucket segmentBucket;
    protected Map<Integer, List<SegmentKey>> bucketMap;
    protected SegmentFuzzyBucket segmentFuzzyBucket;

    public AbstractSegmentFilter(SwiftTableAllotRule tableAllotRule, SwiftSegmentBucket segmentBucket) {
        this.tableAllotRule = tableAllotRule;
        this.segmentBucket = segmentBucket;
        this.bucketMap = segmentBucket.getBucketMap();
        this.segmentFuzzyBucket = new SegmentFuzzyBucket(tableAllotRule.getAllotRule(), bucketMap.keySet());
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
        if (filterInfo instanceof GeneralFilterInfo) {
            GeneralFilterInfo generalFilterInfo = (GeneralFilterInfo) filterInfo;
            List<FilterInfo> childrenFilterInfoList = generalFilterInfo.getChildren();
            Set<Integer> set = null;
            if (generalFilterInfo.getType() == GeneralFilterInfo.OR) {
                // 或操作需要全部完成
                for (FilterInfo filter : childrenFilterInfoList) {
                    Set<Integer> subIndexSet = getIndexSet(filter, table);
                    if (set == null) {
                        set = subIndexSet;
                    } else {
                        set.addAll(subIndexSet);
                    }
                }
                //这里不会产生npe问题，因为由底层返回加子节点扩展保证的，除非没有子节点，但这是不可能的
                if (set.contains(ALL_SEGMENT)) {
                    return new HashSet<Integer>() {{
                        add(ALL_SEGMENT);
                    }};
                } else {
                    return set;
                }
            } else {
                for (FilterInfo filter : childrenFilterInfoList) {
                    Set<Integer> subIndexSet = getIndexSet(filter, table);
                    if (set == null) {
                        set = subIndexSet;
                    } else if (set.contains(ALL_SEGMENT) || subIndexSet.contains(ALL_SEGMENT)) {
                        // 与判断的双方任意一个包含全部，则全部加进来，且去掉ALL_SEGMENT
                        set.addAll(subIndexSet);
                        if (set.size() > 1) {
                            set.remove(ALL_SEGMENT);
                        }
                    } else {
                        set.retainAll(subIndexSet);
                    }
                    // 与操作可以提前终止
                    if (set.isEmpty()) {
                        return set;
                    }
                }
                return set;
            }
        } else if (filterInfo instanceof SwiftDetailFilterInfo) {
            // 基础情形1和2不返回null即可保证逻辑正确性
            if (((SwiftDetailFilterInfo) filterInfo).getType() == SwiftDetailFilterType.IN) {
                return segmentFuzzyBucket.getIncludedKey(getSingleKeyVirtualOrder((SwiftDetailFilterInfo) filterInfo, table));
            } else {
                return new HashSet<Integer>() {{
                    add(ALL_SEGMENT);
                }};
            }
        }
        // TODO: 2020/6/1  not 有逻辑 bug
//        else if (filterInfo instanceof NotFilterInfo) {
//            set.addAll(segmentFuzzyBucket.getNotIncludedKey(getIndexSet(((NotFilterInfo) filterInfo).getFilterInfo(), table)));
//        }
        else {
            // 基础情形3也不能返回null
            return new HashSet<Integer>() {{
                add(ALL_SEGMENT);
            }};
        }
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