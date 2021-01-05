package com.fr.swift.query.builder;

import com.fr.swift.SwiftContext;
import com.fr.swift.compare.Comparators;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.filter.info.value.SwiftNumberInRangeFilterValue;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentService;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.util.exception.LambdaWrapper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author xiqiu
 * @date 2021/1/5
 * @description
 * @since swift-1.2.0
 */
public enum QuerySegmentFilter {
    /**
     * 枚举单例真实块过滤器
     */
    QUERY_SEGMENT_FILTER;

    private final SegmentService SEG_SVC = SwiftContext.get().getBean(SegmentService.class);

    /**
     * 查询前优化，根据查询的信息来进行块的过滤
     *
     * @param filterInfo     过滤信息
     * @param segmentKeyList 块列表
     * @return 过滤好的块
     * @throws SwiftMetaDataException
     */
    public List<Segment> getDetailSegment(FilterInfo filterInfo, List<SegmentKey> segmentKeyList) throws SwiftMetaDataException {
        Map<String, Segment> idSegments = new HashMap<>();
        segmentKeyList.forEach(segmentKey -> idSegments.put(segmentKey.getId(), SEG_SVC.getSegment(segmentKey)));
        Set<String> strings = dfsSearch(filterInfo, idSegments);
        return strings.stream().map(idSegments::get).collect(Collectors.toList());
    }

    private Set<String> dfsSearch(FilterInfo filterInfo, Map<String, Segment> idSegments) throws SwiftMetaDataException {
        if (filterInfo instanceof GeneralFilterInfo) {
            GeneralFilterInfo generalFilterInfo = (GeneralFilterInfo) filterInfo;
            List<FilterInfo> childrenFilterInfoList = generalFilterInfo.getChildren();
            Set<String> set = null;
            if (generalFilterInfo.getType() == GeneralFilterInfo.OR) {
                // 或判断必须遍历完成
                for (FilterInfo filter : childrenFilterInfoList) {
                    Set<String> subIndexSet = dfsSearch(filter, idSegments);
                    if (set == null) {
                        set = subIndexSet;
                    } else {
                        set.addAll(subIndexSet);
                    }
                }
                return set;
            } else {
                for (FilterInfo filter : childrenFilterInfoList) {
                    Set<String> subIndexSet = dfsSearch(filter, idSegments);
                    if (set == null) {
                        set = subIndexSet;
                    } else {
                        set.retainAll(subIndexSet);
                    }
                    // 与判断的提前终止
                    if (set.isEmpty()) {
                        return set;
                    }
                }
                return set;
            }
        } else if (filterInfo instanceof SwiftDetailFilterInfo) {
            SwiftDetailFilterInfo detailFilterInfo = (SwiftDetailFilterInfo) filterInfo;
            return idSegments.entrySet().stream()
                    .filter(LambdaWrapper.rethrowPredicate(stringSegmentEntry -> canAdd(detailFilterInfo, stringSegmentEntry.getValue())))
                    .map(Map.Entry::getKey).collect(Collectors.toSet());
        }
        return idSegments.keySet();
    }

    private boolean canAdd(SwiftDetailFilterInfo detailFilterInfo, Segment segment) throws SwiftMetaDataException {
        // todo::简易版，后续补充和优化
        switch (detailFilterInfo.getType()) {
            case IN:
                return judgeForIn(detailFilterInfo, segment);
            case STRING_LIKE:
                return true;
            case STRING_STARTS_WITH:
                return true;
            case STRING_ENDS_WITH:
                return true;
            case STRING_LIKE_IGNORE_CASE:
                return true;
            case NUMBER_IN_RANGE:
                return judgeForNumberInRange(detailFilterInfo, segment);
            case NUMBER_AVERAGE:
                return true;
            case TOP_N:
                return true;
            case BOTTOM_N:
                return true;
            case NULL:
                return true;
            case FORMULA:
                return true;
            case KEY_WORDS:
                return true;
            case EMPTY:
                return true;
            case WORK_DAY:
                return true;
            default:
                return true;
        }
    }

    private boolean judgeForIn(SwiftDetailFilterInfo detailFilterInfo, Segment segment) throws SwiftMetaDataException {
        // 利用字典值有序的条件，在in这种情况下，过滤的值一定大于或等于字典第一个值，小于或等于最后一个值，任意一个满足条件即可，时间复杂度O(n),n为filter取值的个数
        // 考虑整个块只有null值的情况
        DictionaryEncodedColumn dictionaryEncodedColumn = segment.getColumn(detailFilterInfo.getColumnKey()).getDictionaryEncodedColumn();
        if (dictionaryEncodedColumn.size() <= 1) {
            return false;
        }
        Comparator asc = getComparator(ColumnTypeUtils.getClassType(segment.getMetaData().getColumn(detailFilterInfo.getColumnKey().getName())));
        Set setValues = (Set) detailFilterInfo.getFilterValue();
        Object start = dictionaryEncodedColumn.getValue(1);
        Object end = dictionaryEncodedColumn.getValue(dictionaryEncodedColumn.size() - 1);
        for (Object tempValue : setValues) {
            if (asc.compare(tempValue, start) >= 0 && asc.compare(tempValue, end) <= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean judgeForNumberInRange(SwiftDetailFilterInfo detailFilterInfo, Segment segment) throws SwiftMetaDataException {
        DictionaryEncodedColumn dictionaryEncodedColumn = segment.getColumn(detailFilterInfo.getColumnKey()).getDictionaryEncodedColumn();
        if (dictionaryEncodedColumn.size() <= 1) {
            return false;
        }
        Comparator asc = getComparator(ColumnTypeUtils.getClassType(segment.getMetaData().getColumn(detailFilterInfo.getColumnKey().getName())));
        SwiftNumberInRangeFilterValue value = (SwiftNumberInRangeFilterValue) detailFilterInfo.getFilterValue();
        Object start = dictionaryEncodedColumn.getValue(1);
        Object end = dictionaryEncodedColumn.getValue(dictionaryEncodedColumn.size() - 1);
        // 最大值比最小值大，或者最大值比最小值小，都是没有，分开写避免麻烦的类型转换问题，而且便于理解
        if (value.getMin().equals(Double.NEGATIVE_INFINITY)) {
            return asc.compare(start, value.getMax()) <= 0;
        } else if (value.getMax().equals(Double.POSITIVE_INFINITY)) {
            return asc.compare(end, value.getMin()) >= 0;
        } else {
            return asc.compare(end, value.getMin()) >= 0 && asc.compare(start, value.getMax()) <= 0;
        }
    }


    private Comparator<?> getComparator(ColumnTypeConstants.ClassType classType) {
        switch (classType) {
            case INTEGER:
            case LONG:
            case DATE:
            case DOUBLE:
                return Comparators.asc();
            case STRING:
                return Comparators.STRING_ASC;
            default:
                throw new IllegalStateException(String.format("unsupported type %s", classType));
        }
    }

}
