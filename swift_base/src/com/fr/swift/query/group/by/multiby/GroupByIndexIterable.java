package com.fr.swift.query.group.by.multiby;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.mapreduce.InCollector;
import com.fr.swift.mapreduce.KeyValue;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.by.GroupBy;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.by.GroupByResult;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 计算分组表一行汇总索引的迭代器
 * 当前可以实现全部计算、分页计算、计算汇总值、不计算汇总值
 * 但是不能处理展开的情况。要处理展开的情况要把iteratorCursor抽象出来。
 * <p>
 * Created by Lyon on 2017/12/27.
 */
public class GroupByIndexIterable implements InCollector<RowIndexKey, RowTraversal> {

    private RecursiveIterator iterator;

    private List<Column> dimensions;
    private DetailFilter detailFilter;
    private int[] cursor;
    private boolean[] asc;
    private boolean showSum = false;
    // 默认-1不分页
    private int pageSize = -1;

    public GroupByIndexIterable(List<Column> dimensions, DetailFilter detailFilter, int pageSize) {
        this.dimensions = dimensions;
        this.detailFilter = detailFilter;
        this.pageSize = pageSize;
    }

    public GroupByIndexIterable(List<Column> dimensions, DetailFilter detailFilter) {
        this.dimensions = dimensions;
        this.detailFilter = detailFilter;
    }

    public void setCursor(int[] cursor) {
        this.cursor = cursor;
    }

    public void setAsc(boolean[] asc) {
        this.asc = asc;
    }

    public void setShowSum(boolean showSum) {
        this.showSum = showSum;
    }

    @Override
    public Iterator<KeyValue<RowIndexKey, RowTraversal>> iterator() {
        this.iterator = new RecursiveIterator(dimensions, detailFilter, cursor, asc, pageSize);
        if (!showSum) {
            return new FilteredIterator<KeyValue<RowIndexKey, RowTraversal>>(iterator, new Filter<KeyValue<RowIndexKey, RowTraversal>>() {
                @Override
                public boolean matches(KeyValue<RowIndexKey, RowTraversal> element) {
                    return !element.getKey().isSum();
                }
            });
        }
        return iterator;
    }

    private static class RecursiveIterator implements Iterator<KeyValue<RowIndexKey, RowTraversal>> {

        private List<Column> dimensions;
        private DetailFilter filter;
        private int[] cursor;
        private boolean[] asc;
        private int pageSize;
        private GroupByResult[] iterators;
        private int lastDimensionIndex;

        private KeyValue<RowIndexKey, RowTraversal> total;
        private int[] groupIndex;
        private ImmutableBitMap filterBitMap = BitMaps.newAllShowBitMap(0);
        private int iteratorCursor;

        public RecursiveIterator(List<Column> dimensions, DetailFilter filter, int[] cursor,
                                 boolean[] asc, int pageSize) {

            this.dimensions = dimensions;
            this.filter = filter;
            this.cursor = cursor;
            this.asc = asc;
            this.pageSize = pageSize;
            this.iterators = new GroupByResult[dimensions.size()];
            this.groupIndex = new int[dimensions.size()];
            this.lastDimensionIndex = dimensions.size() - 1;
            init();
        }

        private void init() {
            iteratorCursor = lastDimensionIndex;
            Arrays.fill(groupIndex, -1);
            if (filter != null) {
                filterBitMap = filter.createFilterIndex();
            }
            RowIndexKey container = new RowIndexKey(groupIndex.length, true);
            container.setValues(groupIndex);
            total = new KeyValue<RowIndexKey, RowTraversal>(container, getAllShow().getAnd(filterBitMap));
            if (asc == null) {
                asc = new boolean[dimensions.size()];
                Arrays.fill(asc, true);
            }
            if (cursor == null) {
                cursor = new int[dimensions.size()];
                Arrays.fill(cursor, 0);
            }
        }

        private ImmutableBitMap getAllShow() {
            assert dimensions.size() != 0;
            ImmutableBitMap bitMap = BitMaps.newRoaringMutable();
            BitmapIndexedColumn column = dimensions.get(0).getBitmapIndex();
            for (int i = 0, size = dimensions.get(0).getDictionaryEncodedColumn().size(); i < size; i++) {
                bitMap = bitMap.getOr(column.getBitMapIndex(i));
            }
            return bitMap;
        }

        private int getIndex(int iteratorIndex) {
            // 0表示全部展开，-1为不展开
            if (cursor[iteratorIndex] == 0) {
                return 0;
            }
            int index = cursor[iteratorIndex];
            // 分页的游标只能用一次
            cursor[iteratorIndex] = 0;
            return index;
        }

        private boolean updatePreviousIterator() {
            if (iteratorCursor < 0) {
                return false;
            }
            if (iteratorCursor == 0 && iterators[0] == null) {
                iterators[0] = GroupBy.createGroupByResult(dimensions.get(0), filterBitMap, getIndex(0), asc[0]);
            }
            if (iterators[iteratorCursor] == null) {
                // 当前为空且不是第一个维度，先更新前一个迭代器
                groupIndex[iteratorCursor] = -1;
                iteratorCursor--;
                return updatePreviousIterator();
            }
            if (iteratorCursor == lastDimensionIndex) {
                GroupByResult it = iterators[lastDimensionIndex];
                if (it.hasNext()) {
                    return true;
                }
                iterators[lastDimensionIndex] = null;
                // 最后一个迭代器迭代完了，更新前一个维度的迭代器
                groupIndex[iteratorCursor] = -1;
                iteratorCursor--;
                return updatePreviousIterator();
            }
            if (iterators[iteratorCursor].hasNext()) {
                // 这边next()返回的是汇总行索引
                return true;
            }
            iterators[iteratorCursor] = null;
            groupIndex[iteratorCursor] = -1;
            // 当前迭代器（不是最后维度的迭代器）迭代完了，继续更新前一个迭代器
            iteratorCursor--;
            return updatePreviousIterator();
        }

        private void updateNextIterator(RowTraversal traversal) {
            if (iteratorCursor == lastDimensionIndex) {
                // 全部展开，个别不展开也在这里处理
                return;
            }
            // 初始化下一个维度的迭代器
            iteratorCursor++;
            iterators[iteratorCursor] = GroupBy.createGroupByResult(dimensions.get(iteratorCursor), traversal,
                    getIndex(iteratorCursor), asc[iteratorCursor]);
        }

        private int[] getIndex() {
            int[] indexes = new int[groupIndex.length];
            Arrays.fill(indexes, -1);
            for (int i = 0; i < groupIndex.length; i++) {
                if (groupIndex[i] != -1) {
                    indexes[i] = groupIndex[i];
                }
            }
            return indexes;
        }

        @Override
        public boolean hasNext() {
            if (pageSize == 0) {
                return false;
            }
            return updatePreviousIterator();
        }

        @Override
        public KeyValue<RowIndexKey, RowTraversal> next() {
            if (total != null) {
                KeyValue<RowIndexKey, RowTraversal> tmp = total;
                total = null;
                return tmp;
            }
            GroupByEntry entry = iterators[iteratorCursor].next();
            RowTraversal traversal = entry.getTraversal();
            groupIndex[iteratorCursor] = entry.getIndex();
            boolean isSum = false;
            pageSize--;
            if (iteratorCursor != lastDimensionIndex && dimensions.size() > 1) {
                // 如果这里要处理展开，只需要独立出iteratorCursor，不要简单的iteratorCursor++、--
                pageSize++;
                isSum = true;
            }
            RowIndexKey values = new RowIndexKey(dimensions.size(), isSum);
            values.setValues(getIndex());

            // 更新下一个迭代器
            updateNextIterator(traversal);
            return new KeyValue<RowIndexKey, RowTraversal>(values, traversal);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
