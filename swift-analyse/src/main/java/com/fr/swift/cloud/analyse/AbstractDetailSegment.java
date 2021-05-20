package com.fr.swift.cloud.analyse;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.query.filter.FilterBuilder;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.filter.info.FilterInfo;
import com.fr.swift.cloud.query.filter.info.GeneralFilterInfo;
import com.fr.swift.cloud.query.info.element.dimension.Dimension;
import com.fr.swift.cloud.query.info.element.dimension.SwiftColumnProvider;
import com.fr.swift.cloud.query.limit.Limit;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.SegmentUtils;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.structure.IntIterable;
import com.fr.swift.cloud.structure.Pair;

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
    protected int limitCount;
    protected SwiftMetaData metaData;
    protected int fetchSize;

    List<ColumnKey> columnKeys;
    List<Column> currentColumns;
    List<Pair<Segment, ImmutableBitMap>> filteredList = new ArrayList<>();
    int segIndex = 0;
    Pair<Segment, ImmutableBitMap> currentSegment;
    IntIterable.IntIterator currentRowItr;
    int rowCount;
    SwiftMetaData queriedMetadata;
    private SegmentComponent segmentComponent;


    public AbstractDetailSegment(int fetchSize, SegmentComponent segmentComponent, List<Dimension> dimensions, List<FilterInfo> filters, Limit limit, SwiftMetaData metaData, SwiftMetaData queriedMetadata) {
        this.fetchSize = fetchSize;
        this.segmentComponent = segmentComponent;
        this.dimensions = dimensions;
        this.filters = filters;
        this.limit = limit;
        this.limitCount = limit == null ? -1 : limit.end();
        this.metaData = metaData;
        this.queriedMetadata = queriedMetadata;
        checkDimensions();
        filter(segmentComponent.getNextBatchSegments());
    }

    private void checkDimensions() {
        columnKeys = dimensions.stream().map(SwiftColumnProvider::getColumnKey).collect(Collectors.toList());
        for (ColumnKey columnKey : columnKeys) {
            if (columnKey == null) {
                continue;
            }
            assert metaData.getFieldNames().contains(columnKey.getName());
        }
    }

    private void filter(List<Segment> batchSegments) {
        // 释放资源，重置计数器
        close();
        segIndex = 0;
        currentSegment = null;
        currentRowItr = null;
        currentColumns = null;
        if (!batchSegments.isEmpty()) {
            List<Pair<Segment, DetailFilter>> collect = batchSegments.stream()
                    .map(seg -> Pair.of(seg, FilterBuilder.buildDetailFilter(seg, new GeneralFilterInfo(filters, GeneralFilterInfo.AND))))
                    .collect(Collectors.toList());
            collect.forEach((pair) -> {
                ImmutableBitMap filteredIndex = pair.getValue().createFilterIndex();
                if (!filteredIndex.isEmpty()) {
                    filteredList.add(Pair.of(pair.getKey(), filteredIndex));
                } else {
                    //没数据的块直接释放掉，不能遗漏这里
                    SegmentUtils.releaseHisSeg(pair.getKey());
                }
            });
            rowCount = filteredList.stream().mapToInt(bitMapPair -> bitMapPair.getValue().getCardinality()).sum();
            if (!filteredList.isEmpty()) {
                setProperties(this.segIndex);
            } else if (!segmentComponent.isEmpty()) {
                // todo::考虑极端情况下的递归风险，方法之一是增大batchsize，这样就可以很大程度降低可能的递归层数
                filter(segmentComponent.getNextBatchSegments());
            }
        }
    }


    /**
     * 这里进行当前块和所有块的更新操作并返回判断结果
     *
     * @return 是否有更多的数据
     */
    @Override
    public boolean hasNext() {
        if (limit != null && limitCount <= 0) {
            return false;
        }
        if (currentRowItr != null && currentRowItr.hasNext()) {
            return true;
        } else if (segIndex < filteredList.size() - 1) {
            this.segIndex++;
            setProperties(this.segIndex);
            return hasNext();
        } else if (!segmentComponent.isEmpty()) {
            filter(segmentComponent.getNextBatchSegments());
            return hasNext();
        }
        return false;
    }

    @Override
    public boolean hasNextPage() {
        return hasNext();
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
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
        // 释放块资源，每次开启下一个批次的块查询的时候就要释放掉，最后不用的时候也会被释放
        SegmentUtils.releaseHisSeg(filteredList.stream().map(Pair::getKey).collect(Collectors.toList()));
        filteredList.clear();
    }

    protected void setProperties(int index) {
        currentSegment = filteredList.get(index);
        currentRowItr = currentSegment.getValue().intIterator();
        currentColumns = dimensions.stream().map(dimension -> dimension.getColumn(currentSegment.getKey())).collect(Collectors.toList());
    }
}
