package com.fr.swift.cloud.query.builder;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.compare.Comparators;
import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.query.builder.feture.SegmentColumnFeature;
import com.fr.swift.cloud.query.filter.info.FilterInfo;
import com.fr.swift.cloud.query.filter.info.GeneralFilterInfo;
import com.fr.swift.cloud.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.cloud.query.filter.info.value.SwiftNumberInRangeFilterValue;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.SegmentKey;
import com.fr.swift.cloud.segment.SegmentService;
import com.fr.swift.cloud.source.ColumnTypeConstants;
import com.fr.swift.cloud.source.ColumnTypeUtils;
import com.fr.swift.cloud.source.SwiftMetaDataColumn;
import com.fr.swift.cloud.util.exception.LambdaWrapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author xiqiu
 * @date 2021/1/5
 * @description 真实块过滤器，对各种特定查询结构进行优化
 * @since swift-1.2.0
 */
public class QuerySegmentFilter {

    private static final int MAX_LIKE_COUNT = 300;
    private static final SegmentService SEG_SVC = SwiftContext.get().getBean(SegmentService.class);
    private Map<String, Segment> idSegments = new HashMap<>();
    //  每个列都有缓存Comparators，省的重复拿比较器
    private Map<String, Comparator> comparatorsCache = new HashMap<>(2);
    //  优化块信息抽取方式，封装起来并保证速度，某些极端场景可以加速
    private Map<String, SegmentColumnFeature> columnFeatureMap = new HashMap<>(10);

    /**
     * 查询前优化，根据查询的信息来进行块的过滤
     *
     * @param filterInfo     过滤信息
     * @param segmentKeyList 块列表
     * @return 过滤好的块
     * @throws SwiftMetaDataException
     */
    public List<Segment> getDetailSegment(FilterInfo filterInfo, List<SegmentKey> segmentKeyList) throws SwiftMetaDataException {
        long start = System.currentTimeMillis();
        segmentKeyList.forEach(segmentKey -> idSegments.put(segmentKey.getId(), SEG_SVC.getSegment(segmentKey)));
        Set<String> strings = dfsSearch(filterInfo);
        List<Segment> result = new ArrayList<>();
        // 这里不需要释放块资源，块资源相关句柄都是null，没有意义
        idSegments.forEach((key, value) -> {
            if (strings.contains(key)) {
                result.add(value);
            }
        });
        SwiftLoggers.getLogger().debug("pre real segment filter speed up consume {} ms , total segments count is {}", System.currentTimeMillis() - start, result.size());
        return result;
    }

    private Set<String> dfsSearch(FilterInfo filterInfo) throws SwiftMetaDataException {
        if (filterInfo instanceof GeneralFilterInfo) {
            GeneralFilterInfo generalFilterInfo = (GeneralFilterInfo) filterInfo;
            List<FilterInfo> childrenFilterInfoList = generalFilterInfo.getChildren();
            Set<String> set = null;
            if (generalFilterInfo.getType() == GeneralFilterInfo.OR) {
                // 或判断必须遍历完成
                for (FilterInfo filter : childrenFilterInfoList) {
                    Set<String> subIndexSet = dfsSearch(filter);
                    if (set == null) {
                        set = subIndexSet;
                    } else {
                        set.addAll(subIndexSet);
                    }
                }
                return set;
            } else {
                for (FilterInfo filter : childrenFilterInfoList) {
                    Set<String> subIndexSet = dfsSearch(filter);
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
        }
        // todo::非绝对正确的判断，所以不能用非逻辑，否则会出错
//        else if (filterInfo instanceof NotFilterInfo) {
//            NotFilterInfo notFilterInfo = (NotFilterInfo) filterInfo;
//            FilterInfo children = notFilterInfo.getFilterInfo();
//            Set<String> result = new HashSet<>(idSegments.keySet());
//            result.removeAll(dfsSearch(children, idSegments));
//            return result;
//
//        }
        else if (filterInfo instanceof SwiftDetailFilterInfo) {
            SwiftDetailFilterInfo detailFilterInfo = (SwiftDetailFilterInfo) filterInfo;
            return idSegments.entrySet().stream()
                    .filter(LambdaWrapper.rethrowPredicate(stringSegmentEntry -> canAdd(detailFilterInfo, stringSegmentEntry)))
                    .map(Map.Entry::getKey).collect(Collectors.toSet());
        }
        // 为其他情况准备的
        return new HashSet<>(idSegments.keySet());
    }

    private boolean canAdd(SwiftDetailFilterInfo detailFilterInfo, Map.Entry<String, Segment> stringSegmentEntry) throws SwiftMetaDataException {
        // todo::简易版，后续补充和优化
        switch (detailFilterInfo.getType()) {
            case IN:
                return judgeForIn(detailFilterInfo, stringSegmentEntry);
            case STRING_LIKE:
                return judgeForLike(detailFilterInfo, stringSegmentEntry);
            case STRING_STARTS_WITH:
                return judgeForStartWith(detailFilterInfo, stringSegmentEntry);
            case STRING_ENDS_WITH:
                return judgeForLike(detailFilterInfo, stringSegmentEntry);
            case STRING_LIKE_IGNORE_CASE:
                return true;
            case NUMBER_IN_RANGE:
                return judgeForNumberInRange(detailFilterInfo, stringSegmentEntry);
            case NUMBER_AVERAGE:
                return true;
            case TOP_N:
                return true;
            case BOTTOM_N:
                return true;
            case NULL:
                return judgeForNull(detailFilterInfo, stringSegmentEntry);
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

    private boolean judgeForIn(SwiftDetailFilterInfo detailFilterInfo, Map.Entry<String, Segment> stringSegmentEntry) throws SwiftMetaDataException {
        // 利用字典值有序的条件，在in这种情况下，过滤的值一定大于或等于字典第一个值，小于或等于最后一个值，任意一个满足条件即可，时间复杂度O(n),n为filter取值的个数
        detailFilterInfo.getColumnKey().getName();
        SegmentColumnFeature columnFeature = getSegmentColumnFeature(detailFilterInfo, stringSegmentEntry);
        if (columnFeature.getDictSize() <= 1) {
            return false;
        }
        Comparator asc = getComparator(detailFilterInfo, stringSegmentEntry.getValue());
        Set setValues = (Set) detailFilterInfo.getFilterValue();
        Object start = columnFeature.getStart();
        Object end = columnFeature.getEnd();
        for (Object tempValue : setValues) {
            if (asc.compare(tempValue, start) >= 0 && asc.compare(tempValue, end) <= 0) {
                return true;
            }
        }
        return false;
    }


    private boolean judgeForStartWith(SwiftDetailFilterInfo detailFilterInfo, Map.Entry<String, Segment> stringSegmentEntry) throws SwiftMetaDataException {
        SegmentColumnFeature columnFeature = getSegmentColumnFeature(detailFilterInfo, stringSegmentEntry);
        if (columnFeature.getDictSize() <= 1) {
            return false;
        }
        Comparator asc = getComparator(detailFilterInfo, stringSegmentEntry.getValue());
        Object likeValue = detailFilterInfo.getFilterValue();
        Object start = columnFeature.getStart();
        Object end = columnFeature.getEnd();
        // start like 和  in filter 类似，原理差不多
        if (asc.compare(likeValue, start) >= 0 && asc.compare(likeValue, end) <= 0) {
            return true;
        }
        return false;
    }

    private boolean judgeForNumberInRange(SwiftDetailFilterInfo detailFilterInfo, Map.Entry<String, Segment> stringSegmentEntry) throws SwiftMetaDataException {
        SegmentColumnFeature columnFeature = getSegmentColumnFeature(detailFilterInfo, stringSegmentEntry);
        if (columnFeature.getDictSize() <= 1) {
            return false;
        }
        Comparator asc = getComparator(detailFilterInfo, stringSegmentEntry.getValue());
        SwiftNumberInRangeFilterValue value = (SwiftNumberInRangeFilterValue) detailFilterInfo.getFilterValue();
        Object start = columnFeature.getStart();
        Object end = columnFeature.getEnd();
        // 最大值比最小值大，或者最大值比最小值小，都是没有，分开写避免麻烦的类型转换问题，而且便于理解，等号才能避免开闭区间的问题
        if (value.getMin().equals(Double.NEGATIVE_INFINITY)) {
            return asc.compare(start, value.getMax()) <= 0;
        } else if (value.getMax().equals(Double.POSITIVE_INFINITY)) {
            return asc.compare(end, value.getMin()) >= 0;
        } else {
            return asc.compare(end, value.getMin()) >= 0 && asc.compare(start, value.getMax()) <= 0;
        }
    }

    private boolean judgeForNull(SwiftDetailFilterInfo detailFilterInfo, Map.Entry<String, Segment> stringSegmentEntry) {
        SegmentColumnFeature columnFeature = getSegmentColumnFeature(detailFilterInfo, stringSegmentEntry);
        return columnFeature.getNullCount() > 0;
    }

    private boolean judgeForLike(SwiftDetailFilterInfo detailFilterInfo, Map.Entry<String, Segment> stringSegmentEntry) {
        SegmentColumnFeature columnFeature = getSegmentColumnFeature(detailFilterInfo, stringSegmentEntry);
        if (columnFeature.getDictSize() <= 1) {
            return false;
        }
        if (columnFeature.getDictSize() > MAX_LIKE_COUNT) {
            return true;
        }
        List<String> likeValue = columnFeature.getLikeValue();
        String filterValue = (String) detailFilterInfo.getFilterValue();
        for (String tempValue :
                likeValue) {
            if (tempValue.contains(filterValue)) {
                return true;
            }
        }
        return false;
    }

    private Comparator<?> getComparator(SwiftDetailFilterInfo detailFilterInfo, Segment segment) throws SwiftMetaDataException {
        String columnName = detailFilterInfo.getColumnKey().getName();
        SwiftMetaDataColumn column = segment.getMetaData().getColumn(columnName);
        return comparatorsCache.computeIfAbsent(columnName, k -> getComparator(ColumnTypeUtils.getClassType(column)));
    }

    private SegmentColumnFeature getSegmentColumnFeature(SwiftDetailFilterInfo detailFilterInfo, Map.Entry<String, Segment> stringSegmentEntry) {
        String columnName = detailFilterInfo.getColumnKey().getName();
        return columnFeatureMap.computeIfAbsent(stringSegmentEntry.getKey() + columnName,
                k -> new SegmentColumnFeature(stringSegmentEntry.getValue(), detailFilterInfo));
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
