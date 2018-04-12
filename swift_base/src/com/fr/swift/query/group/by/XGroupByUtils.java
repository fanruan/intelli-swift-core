package com.fr.swift.query.group.by;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.by.paging.Filter;
import com.fr.swift.query.group.by.paging.FilteredIterator;
import com.fr.swift.query.group.by.paging.GroupByPagingIterator;
import com.fr.swift.query.group.by.paging.XGroupByIterator;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.result.XGroupByResultSet;
import com.fr.swift.result.XGroupByResultSetImpl;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/2.
 */
public class XGroupByUtils {

    public static XGroupByResultSet<int[]> query(List<Column> rowDimensions, List<Column> colDimensions,
                                                 List<Column> metrics, List<Aggregator> aggregators, DetailFilter filter,
                                                 List<Sort> sorts, List<Sort> colSorts, int[] cursor, int[] xCursor, int pageSize,
                                                 int xPageSize) {
        Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> rowIt = createIterator(rowDimensions, filter, cursor, sorts);
        Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> colIt = createIterator(colDimensions, filter, xCursor, colSorts);
        if (pageSize != -1) {
            rowIt = new GroupByPagingIterator(pageSize, rowIt);
        }
        if (xPageSize != -1) {
            colIt = new GroupByPagingIterator(xPageSize, colIt);
        }
        List<KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>> resultList = new ArrayList<KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>>();
        // 行表头的全局字典和字典列
        List<Map<Integer, Object>> globalDictionaries= GroupByUtils.createGlobalDictionaries(rowDimensions.size());
        List<DictionaryEncodedColumn> dictionaries = GroupByUtils.getDictionaries(rowDimensions);
        // 列表头的全局字典和字典列
        List<Map<Integer, Object>> xGlobalDictionaries= GroupByUtils.createGlobalDictionaries(colDimensions.size());
        List<DictionaryEncodedColumn> xDictionaries = GroupByUtils.getDictionaries(colDimensions);
        Iterator<KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, RowTraversal>>>> xIt =
                new XGroupByIterator(rowIt, new CacheIterable(colIt), colDimensions.size());
        while (xIt.hasNext()) {
            KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, RowTraversal>>> kv = xIt.next();
            List<KeyValue<RowIndexKey<int[]>, RowTraversal>> keyValues = kv.getValue();
            List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> list = new ArrayList<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>();
            for (KeyValue<RowIndexKey<int[]>, RowTraversal> keyValue : keyValues) {
                list.add(new KeyValue<RowIndexKey<int[]>, AggregatorValue[]>(
                        GroupByUtils.toGlobalIndex(keyValue.getKey().getKey(), xDictionaries),
                        GroupByUtils.aggregateRow(keyValue.getValue(), metrics, aggregators)));
                // 更新列表头的全局字典
                GroupByUtils.updateGlobalDictionaries(keyValue.getKey().getKey(), xGlobalDictionaries, xDictionaries);
            }
            resultList.add(new KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>(
                    GroupByUtils.toGlobalIndex(kv.getKey().getKey(), dictionaries), list));
            // 更新行表头的全局字典
            GroupByUtils.updateGlobalDictionaries(kv.getKey().getKey(), globalDictionaries, dictionaries);
        }
        return new XGroupByResultSetImpl(resultList, globalDictionaries, xGlobalDictionaries,
                rowDimensions.size(), colDimensions.size());
    }

    private static Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> createIterator(List<Column> dimensions,
                                                                                       DetailFilter filter,
                                                                                       int[] cursor,
                                                                                       List<Sort> sorts) {
        Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> iterator =
                new MultiDimensionGroupBy(dimensions, filter, cursor, GroupByUtils.getSorts(sorts, dimensions.size()));
        iterator = new FilteredIterator<KeyValue<RowIndexKey<int[]>, RowTraversal>>(iterator, new Filter<KeyValue<RowIndexKey<int[]>, RowTraversal>>() {
            @Override
            public boolean accept(KeyValue<RowIndexKey<int[]>, RowTraversal> kv) {
                int[] index = kv.getKey().getKey();
                // 过滤掉除了根节点的汇总行
                if (index.length > 1 && index[0] != -1 && index[index.length -1] == -1) {
                    return false;
                }
                return true;
            }
        });
        return iterator;
    }

    /**
     * 通过代理实现缓存
     */
    private static class CacheIterable implements Iterable<KeyValue<RowIndexKey<int[]>, RowTraversal>> {

        private Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> colIt;
        private final List<KeyValue<RowIndexKey<int[]>, RowTraversal>> cacheList = new ArrayList<KeyValue<RowIndexKey<int[]>, RowTraversal>>();

        public CacheIterable(Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> colIt) {
            this.colIt = colIt;
        }

        @Override
        public Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> iterator() {
            // 通过代理的方式缓存索引
            return cacheList.isEmpty() ? new ProxyIterator() : cacheList.iterator();
        }

        private class ProxyIterator implements Iterator<KeyValue<RowIndexKey<int[]>, RowTraversal>> {

            @Override
            public boolean hasNext() {
                return colIt.hasNext();
            }

            @Override
            public KeyValue<RowIndexKey<int[]>, RowTraversal> next() {
                KeyValue<RowIndexKey<int[]>, RowTraversal> kv = colIt.next();
                cacheList.add(kv);
                return kv;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }
}
