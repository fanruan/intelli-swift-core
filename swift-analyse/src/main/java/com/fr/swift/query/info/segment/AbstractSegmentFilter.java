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
import com.fr.swift.segment.SegmentService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.hash.HashAllotRule;

import java.util.ArrayList;
import java.util.HashMap;
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

    public AbstractSegmentFilter(SwiftTableAllotRule tableAllotRule, SwiftSegmentBucket segmentBucket) {
        this.tableAllotRule = tableAllotRule;
        this.segmentBucket = segmentBucket;
        this.bucketMap = segmentBucket.getBucketMap();
    }

    @Override
    public List<Segment> filterSegs(SingleTableQueryInfo singleTableQueryInfo) throws SwiftMetaDataException {
        List<SegmentKey> segmentKeyList = filterSegKeys(singleTableQueryInfo);
        return segmentKeyList.stream().map(SEG_SVC::getSegment).collect(Collectors.toList());
    }

    @Override
    public List<SegmentKey> filterSegKeys(SingleTableQueryInfo singleTableQueryInfo) throws SwiftMetaDataException {
        if (singleTableQueryInfo.getQuerySegment() == null || singleTableQueryInfo.getQuerySegment().isEmpty()) {
            return reFilter(singleTableQueryInfo);
        } else {
            if (isDataEmpty(singleTableQueryInfo)) {
                return new ArrayList<>();
            }
            //允许exact query容错
            List<SegmentKey> filteredSegKeys = reFilter(singleTableQueryInfo);
            if (filteredSegKeys.isEmpty()) {
                singleTableQueryInfo.setQuerySegment(null);
                filteredSegKeys = reFilter(singleTableQueryInfo);
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

    private List<SegmentKey> reFilter(SingleTableQueryInfo singleTableQueryInfo) throws SwiftMetaDataException {
        if (isLineAllot(singleTableQueryInfo)) {
            return SEG_SVC.getSegmentKeysByIds(singleTableQueryInfo.getTable(), singleTableQueryInfo.getQuerySegment());
        }
        Set<Integer> virtualOrders = getIndexSet(singleTableQueryInfo.getFilterInfo(), singleTableQueryInfo.getTable());
        return filterSegment(virtualOrders, singleTableQueryInfo);
    }

    private boolean isLineAllot(SingleTableQueryInfo singleTableQueryInfo) {
        if (tableAllotRule == null || segmentBucket == null
                || singleTableQueryInfo.getFilterInfo() == null || tableAllotRule.getAllotRule().getType() == BaseAllotRule.AllotType.LINE) {
            return true;
        }
        return false;
    }

    protected abstract List<SegmentKey> filterSegment(Set<Integer> virtualOrders, SingleTableQueryInfo singleTableQueryInfo);

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
                int logicOrder = getMultiHashKeyVirtualOrder(table, childrenFilterInfoList);
                if (logicOrder != ALL_SEGMENT) {
                    set.add(logicOrder);
                    return set;
                }
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
                set.addAll(getSingleHashKeyVirtualOrder((SwiftDetailFilterInfo) filterInfo, table));
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

    private int getMultiHashKeyVirtualOrder(SourceKey table, List<FilterInfo> childrenFilterInfoList) throws SwiftMetaDataException {
        AllotRule allotRule = this.tableAllotRule.getAllotRule();
        HashAllotRule hashAllotRule = (HashAllotRule) allotRule;
        int hashKeyCount = hashAllotRule.getFieldIndexes().length;
        Map<String, String> filterInfoMap = new HashMap<>();
        if (childrenFilterInfoList.size() == hashKeyCount) {
            for (FilterInfo filterInfo : childrenFilterInfoList) {
                if (!(filterInfo instanceof SwiftDetailFilterInfo)) {
                    return ALL_SEGMENT;
                } else {
                    SwiftDetailFilterInfo filter = (SwiftDetailFilterInfo) filterInfo;
                    filterInfoMap.put(filter.getColumnKey().getName(), String.valueOf(((HashSet) filter.getFilterValue()).toArray()[0]));
                }
            }
            List<String> hashKeyList = new ArrayList<>(hashKeyCount);
            SwiftMetaData metadata = SwiftDatabase.getInstance().getTable(table).getMetadata();
            int[] fieldIndexes = hashAllotRule.getFieldIndexes();
            for (int fieldIndex : fieldIndexes) {
                int index = fieldIndex + 1;
                hashKeyList.add(metadata.getColumn(index).getName());
            }
            if (hashKeyList.containsAll(filterInfoMap.keySet())) {
                List<Object> keys = new ArrayList();
                for (String hashKey : hashKeyList) {
                    keys.add(filterInfoMap.get(hashKey));
                }
                return hashAllotRule.getHashFunction().indexOf(keys);
            }
        }
        return ALL_SEGMENT;
    }

    private Set<Integer> getSingleHashKeyVirtualOrder(SwiftDetailFilterInfo filterInfo, SourceKey table) throws SwiftMetaDataException {
        //获取hash后对应桶中的segments
        AllotRule allotRule = this.tableAllotRule.getAllotRule();
        Set<Object> filterValues = (Set<Object>) filterInfo.getFilterValue();
        HashAllotRule hashAllotRule = (HashAllotRule) allotRule;
        //计算所有字段名
        int hashFieldIndex = hashAllotRule.getFieldIndexes()[0];
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