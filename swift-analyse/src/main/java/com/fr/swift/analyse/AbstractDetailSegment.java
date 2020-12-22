package com.fr.swift.analyse;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.limit.Limit;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.IntIterable;
import com.fr.swift.structure.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/16
 */
public abstract class AbstractDetailSegment implements CalcSegment {
    protected List<Dimension> dimensions;
    protected List<FilterInfo> filters;
    protected Limit limit;
    protected SwiftMetaData metaData;

    List<ColumnKey> columnKeys;
    List<Pair<Segment, ImmutableBitMap>> filteredList = new ArrayList<>();
    int segIndex = 0;
    Pair<Segment, ImmutableBitMap> currentSeg;
    IntIterable.IntIterator currentRowItr;
    int rowCount;
    SwiftMetaData queriedMetadata;
    private SegmentContainer segmentContainer;

    public AbstractDetailSegment(SegmentContainer segmentContainer, List<Dimension> dimensions, List<FilterInfo> filters, Limit limit, SwiftMetaData metaData, SwiftMetaData queriedMetadata) {
        this.segmentContainer = segmentContainer;
        this.dimensions = dimensions;
        this.filters = filters;
        this.limit = limit;
        this.metaData = metaData;
        this.queriedMetadata = queriedMetadata;
        checkDimesions();
        filter(segmentContainer.getNextBatchSegments());
    }

    private void checkDimesions() {
        columnKeys = dimensions.stream().map(dimension -> dimension.getColumnKey()).collect(Collectors.toList());
        for (ColumnKey columnKey : columnKeys) {
            assert metaData.getFieldNames().contains(columnKey.getName());
        }
    }

    private void filter(List<Segment> batchSegments) {
        this.filteredList.clear();
        segIndex = 0;
        currentSeg = null;
        currentRowItr = null;
        if (!batchSegments.isEmpty()) {
            List<Pair<Segment, DetailFilter>> collect = batchSegments.stream()
                    .map(seg -> Pair.of(seg, FilterBuilder.buildDetailFilter(seg, new GeneralFilterInfo(filters, GeneralFilterInfo.AND))))
                    .collect(Collectors.toList());

            collect.forEach((pair) -> {
                ImmutableBitMap filteredIndex = pair.getValue().createFilterIndex();
                if (!filteredIndex.isEmpty()) {
                    filteredList.add(Pair.of(pair.getKey(), filteredIndex));
                }
            });
            rowCount = filteredList.stream().mapToInt(bitMapPair -> bitMapPair.getValue().getCardinality()).sum();
            if (!filteredList.isEmpty()) {
                currentSeg = filteredList.get(segIndex);
                currentRowItr = currentSeg.getValue().intIterator();
            } else if (!segmentContainer.isEmpty()) {
                filter(segmentContainer.getNextBatchSegments());
            }
        }
    }


    /**
     * 这里进行块更新操作并返回判断结果
     *
     * @return 是否有更多的数据
     */
    @Override
    public boolean hasNext() {
        if (currentRowItr != null && currentRowItr.hasNext()) {
            return true;
        } else if (!segmentContainer.isEmpty()) {
            filter(segmentContainer.getNextBatchSegments());
            return hasNext();
        }
        return false;
    }

    @Override
    public int rowCount() {
        return rowCount;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return queriedMetadata;
    }

    @Override
    public void close() {
        segmentContainer.releaseSegments();
    }
}
