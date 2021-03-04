package com.fr.swift.cloud.segment.operator.collate.segment;

import com.fr.swift.cloud.bitmap.BitMaps;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.impl.FasterAggregation;
import com.fr.swift.cloud.bitmap.traversal.TraversalAction;
import com.fr.swift.cloud.compare.Comparators;
import com.fr.swift.cloud.query.filter.detail.DetailFilter;
import com.fr.swift.cloud.query.filter.match.MatchConverter;
import com.fr.swift.cloud.query.group.by.MergerGroupByValues;
import com.fr.swift.cloud.query.group.by.MultiGroupByValues;
import com.fr.swift.cloud.query.group.info.GroupByInfoImpl;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.segment.column.BitmapIndexedColumn;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.segment.operator.collate.segment.bitmap.BitMapShifter;
import com.fr.swift.cloud.structure.Pair;
import com.fr.swift.cloud.structure.iterator.RowTraversal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lyon on 2019/2/21.
 */
public class IndexedColumnBuilder implements Builder {

    private int rowCount;
    private DictionaryEncodedColumn dictColumn;
    private BitmapIndexedColumn indexedColumn;
    private List<Column> subColumns;
    private List<ImmutableBitMap> allShows;
    private List<BitMapShifter> shifters;

    private Iterator<Pair<Object[], RowTraversal[][]>> iterator;

    /**
     * 根据碎片块的columns构建新块的字典列和索引列
     * 这边实现的前提是要对碎片块进行划分{@link Partitioner}。首先通过对多个碎片块的column分别做groupBy，
     * 然后根据字典值进行合并，再根据合并结果写新column的index2bitmap和index2dictValue。groupBy迭代器遍历完之后，
     * 最后写row2index，这样保证了所有写入过程都是顺序写。
     *
     * @param rowCount      新块的总行数
     * @param dictColumn    新块的字典列
     * @param indexedColumn 新块的索引列
     * @param subColumns    当前新块column对应的所有碎片块的column
     * @param allShows      每个碎片块对应的allShow
     * @param shifters      每个碎片块对应的bitmap偏移器
     */
    public IndexedColumnBuilder(int rowCount, DictionaryEncodedColumn dictColumn, BitmapIndexedColumn indexedColumn,
                                List<Column> subColumns, List<ImmutableBitMap> allShows, List<BitMapShifter> shifters) {
        this.rowCount = rowCount;
        this.dictColumn = dictColumn;
        this.indexedColumn = indexedColumn;
        this.subColumns = subColumns;
        this.allShows = allShows;
        this.shifters = shifters;
        init();
    }

    private void init() {
        MultiGroupByValues[] iterators = new MultiGroupByValues[subColumns.size()];
        for (int i = 0; i < subColumns.size(); i++) {
            final ImmutableBitMap allShow = allShows.get(i);
            DetailFilter filter = new DetailFilter() {
                @Override
                public ImmutableBitMap createFilterIndex() {
                    return allShow;
                }

                @Override
                public boolean matches(SwiftNode node, int targetIndex, MatchConverter converter) {
                    return false;
                }
            };
            iterators[i] = new MultiGroupByValues(new GroupByInfoImpl(Collections.singletonList(subColumns.get(i)), filter));
        }
        this.iterator = new MergerGroupByValues(iterators, new Comparator[]{Comparators.<String>asc()}, new boolean[]{true});
    }

    @Override
    public void build() {
        final int[] row2Index = new int[rowCount];
        DictionaryEncodedColumn.Putter putter = dictColumn.putter();
        int dictSize = 0;
        int index = 0;
        while (iterator.hasNext()) {
            Pair<Object[], RowTraversal[][]> pair = iterator.next();
            // put index to value
            Object value = pair.getKey()[0];
            if (index == 0 && value != null) {
                // 如果不存在空值，也要先写空值再写后续的值
                putter.putValue(0, null);
                indexedColumn.putBitMapIndex(0, BitMaps.EMPTY_IMMUTABLE);
                index++;
            }
            putter.putValue(index, value);
            if (value != null) {
                dictSize++;
            }
            RowTraversal[][] traversals = pair.getValue();
            List<ImmutableBitMap> bitMaps = new ArrayList<ImmutableBitMap>();
            for (int i = 0; i < traversals.length; i++) {
                RowTraversal[] traversal = traversals[i];
                if (traversal == null) {
                    continue;
                }
                bitMaps.add(shifters.get(i).shift(traversal[0]));
            }
            ImmutableBitMap bitMap = FasterAggregation.or(bitMaps);
            // put index to bitmap
            indexedColumn.putBitMapIndex(index, bitMap);
            final int finalIndex = index;
            bitMap.traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    row2Index[row] = finalIndex;
                }
            });
            index++;
        }
        // put row to index
        for (int i = 0; i < row2Index.length; i++) {
            putter.putIndex(i, row2Index[i]);
        }
        // put dict size，+1表示必定存在的空值字典值
        putter.putSize(dictSize + 1);
    }
}
