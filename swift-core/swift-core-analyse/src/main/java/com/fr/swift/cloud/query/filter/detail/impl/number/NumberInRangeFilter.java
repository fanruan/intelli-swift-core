package com.fr.swift.cloud.query.filter.detail.impl.number;

import com.fr.swift.cloud.bitmap.BitMaps;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.MutableBitMap;
import com.fr.swift.cloud.bitmap.impl.FasterAggregation;
import com.fr.swift.cloud.compare.Comparators;
import com.fr.swift.cloud.query.filter.detail.impl.AbstractDetailFilter;
import com.fr.swift.cloud.query.filter.detail.impl.util.LookupFactory;
import com.fr.swift.cloud.query.filter.match.MatchConverter;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.segment.column.BitmapIndexedColumn;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.structure.array.IntListFactory;
import com.fr.swift.cloud.structure.iterator.IntListRowTraversal;
import com.fr.swift.cloud.structure.iterator.RowTraversal;
import com.fr.swift.cloud.util.ArrayLookupHelper;
import com.fr.swift.cloud.util.MatchAndIndex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2017/11/27.
 */
public class NumberInRangeFilter extends AbstractDetailFilter<Number> {

    private final static int START_INDEX = DictionaryEncodedColumn.NOT_NULL_START_INDEX;
    private final static int SMALL_GROUP_COLUMN = 10;

    protected final Number min;
    protected final Number max;
    protected final boolean minIncluded;
    protected final boolean maxIncluded;
    private int rowCount;

    public NumberInRangeFilter(Number min, Number max, boolean minIncluded, boolean maxIncluded,
                               Column<Number> column, int rowCount) {
        this.min = min;
        this.max = max;
        this.minIncluded = minIncluded;
        this.maxIncluded = maxIncluded;
        this.column = column;
        this.rowCount = rowCount;
    }

    protected NumberInRangeFilter(NumberInRangeFilter filter) {
        this.max = filter.max;
        this.min = filter.min;
        this.minIncluded = filter.minIncluded;
        this.maxIncluded = filter.maxIncluded;
        this.column = filter.column;
    }

    @Override
    protected RowTraversal getIntIterator(final DictionaryEncodedColumn<Number> dict) {
        ArrayLookupHelper.Lookup<Number> lookup = LookupFactory.create(dict, Comparators.NUMBER_ASC);
        // 获取过滤条件对应的RangeIntList区间
        int start = min.doubleValue() == Double.NEGATIVE_INFINITY ? START_INDEX : getStart(ArrayLookupHelper.binarySearch(lookup, min));
        int end = max.doubleValue() == Double.POSITIVE_INFINITY ? dict.size() - 1 : getEnd(ArrayLookupHelper.binarySearch(lookup, max));
        start = Math.max(start, START_INDEX);
        if (start >= dict.size() || end < START_INDEX || start > end) {
            return new IntListRowTraversal(IntListFactory.createEmptyIntList());
        }
        return new IntListRowTraversal(IntListFactory.createRangeIntList(start, end));
    }

    @Override
    public ImmutableBitMap createFilterIndex() {
        int dictSize = column.getDictionaryEncodedColumn().size();
        DictionaryEncodedColumn<Number> dict = column.getDictionaryEncodedColumn();
        ArrayLookupHelper.Lookup<Number> lookup = LookupFactory.create(dict, Comparators.NUMBER_ASC);
        int start = min.doubleValue() == Double.NEGATIVE_INFINITY ? START_INDEX : getStart(ArrayLookupHelper.binarySearch(lookup, min));
        int end = max.doubleValue() == Double.POSITIVE_INFINITY ? dict.size() - 1 : getEnd(ArrayLookupHelper.binarySearch(lookup, max));
        if (rowCount / dictSize > SMALL_GROUP_COLUMN) {
            return super.createFilterIndex();
        }
        if (1.0 * Math.max(1, end - start) / dictSize < 0.05) {
            // bitmap不多的场景
            List<ImmutableBitMap> bitMapList = new ArrayList<ImmutableBitMap>();
            BitmapIndexedColumn indexedColumn = column.getBitmapIndex();
            for (int i = start; i <= end; i++) {
                bitMapList.add(indexedColumn.getBitMapIndex(i));
            }
            return FasterAggregation.or(bitMapList);
        }
        MutableBitMap bitMap = BitMaps.newRoaringMutable();
        DetailColumn detail = column.getDetailColumn();
        for (int i = 0; i < rowCount; i++) {
            Number value = (Number) detail.get(i);
            if (null != value && match(value.doubleValue())) {
                bitMap.add(i);
            }
        }
        return bitMap;
    }

    private boolean match(double value) {
        double minValue = min.doubleValue();
        double maxValue = max.doubleValue();
        return (minIncluded ? value >= minValue : value > minValue) &&
                (maxIncluded ? value <= maxValue : value < maxValue);
    }

    private int getEnd(MatchAndIndex maxMatchAndIndex) {
        int end;
        if (maxMatchAndIndex.isMatch()) {
            if (maxIncluded) {
                end = maxMatchAndIndex.getIndex();
            } else {
                // max这个分组值存在但是不包含该分组值的条件下，索引值减1
                end = maxMatchAndIndex.getIndex() - 1;
            }
        } else {
            // max这个分组值不存在的条件下，取当前索引值
            end = maxMatchAndIndex.getIndex();
        }
        return end;
    }

    private int getStart(MatchAndIndex minMatchAndIndex) {
        int start;
        if (minMatchAndIndex.isMatch()) {
            if (minIncluded) {
                start = minMatchAndIndex.getIndex();
            } else {
                // min这个分组值存在但是不包含该分组值的条件下，索引值加1
                start = minMatchAndIndex.getIndex() + 1;
            }
        } else {
            // min这个分组值不存在的条件下，索引值要加1
            start = minMatchAndIndex.getIndex() + 1;
        }
        return start;
    }

    @Override
    public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
        Object data = node.getAggregatorValue(targetIndex).calculateValue();
        if (data == null) {
            return false;
        }
        double value = ((Number) data).doubleValue();
        return match(value);
    }
}

