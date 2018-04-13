package com.fr.swift.cal.result.group;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.Combiner;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.result.GroupByResultSetImpl;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.RowIndexKey;
import com.fr.swift.structure.queue.SortedListMergingUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/1.
 */
public class GroupByResultSetMergingUtils {

    /**
     * 这边一开始欠考虑使用hashSet来合并，后来经pony提醒计算过程中最好不要破坏底层索引提供的有序结构
     * 一语惊醒，swift计算很大部分优势都建立在底层数据的索引结构之上，比如随机读取、过滤用到的二分查找、排序等
     * 就拿这个结果集的合并来说，如果合并过程中乱序了（比如一开始使用hashSet来合并），那么在适配层还是得转为有序结构，这个过程
     * 中无疑是有性能损耗的。这个性能损耗怎么来的呢？首先功能的node结构可以理解为一种广义上的搜索树（有序的），所以肯定要为排序
     * 多做一些操作。举个容易理解的例子，有序数组构造平衡二叉树搜索树的复杂度是O(n)，乱序数组是O(n*log(n))
     * 如何利用这边提供的有序序列构造我们这边比较特殊的搜索树则是另外的优化内容
     *
     * @param groupByResultSets
     * @param aggregators
     * @param indexSorts
     * @return
     */
    @SuppressWarnings("unchecked")
    public static GroupByResultSet merge(List<GroupByResultSet> groupByResultSets,
                                         List<Aggregator> aggregators, List<Sort> indexSorts) {
        List<Map<Integer, Object>> globalDictionaries = new ArrayList<Map<Integer, Object>>();
        List<List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>> lists = new ArrayList<List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>();
        for (GroupByResultSet resultSet : groupByResultSets) {
            lists.add(resultSet.getResultList());
            addDictionaries(resultSet.getRowGlobalDictionaries(), globalDictionaries);
        }
        List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> mergedResult = SortedListMergingUtils.merge(lists,
                new IndexKeyComparator<AggregatorValue[]>(indexSorts), new KVCombiner<AggregatorValue>(convertType(aggregators)));
        int rowDimensionSize = globalDictionaries.isEmpty() ? 0 : groupByResultSets.get(0).rowDimensionSize();
        return new GroupByResultSetImpl(mergedResult, globalDictionaries, rowDimensionSize);
    }

    @SuppressWarnings("unchecked")
    static List<Aggregator<AggregatorValue>> convertType(List<Aggregator> aggregators) {
        List<Aggregator<AggregatorValue>> aggregatorList = new ArrayList<Aggregator<AggregatorValue>>();
        for (Aggregator aggregator : aggregators) {
            aggregatorList.add((Aggregator<AggregatorValue>) aggregator);
        }
        return aggregatorList;
    }

    static void addDictionaries(List<Map<Integer, Object>> dictionaries,
                                        List<Map<Integer, Object>> totalDictionaries) {
        if (totalDictionaries.size() == 0) {
            for (int i = 0; i < dictionaries.size(); i++) {
                totalDictionaries.add(new HashMap<Integer, Object>());
            }
        }
        for (int i = 0; i < dictionaries.size(); i++) {
            totalDictionaries.get(i).putAll(dictionaries.get(i));
        }
    }

    static class IndexKeyComparator<T> implements Comparator<KeyValue<RowIndexKey<int[]>, T>> {

        private List<Sort> sorts;

        public IndexKeyComparator(List<Sort> sorts) {
            this.sorts = sorts;
        }

        @Override
        public int compare(KeyValue<RowIndexKey<int[]>, T> o1,
                           KeyValue<RowIndexKey<int[]>, T> o2) {
            int[] index1 = o1.getKey().getKey();
            int[] index2 = o2.getKey().getKey();
            int result = 0;
            for (int i = 0; i < index1.length; i++) {
                result = compareIndex(index1[i], index2[i]);
                if (sorts != null && !sorts.isEmpty() && sorts.get(i).getTargetIndex() == i) {
                    // TODO: 2018/4/1 如果是对指标排序怎么处理呢？
                    result = sorts.get(i).getSortType() == SortType.ASC ? result : -result;
                }
                if (result != 0) {
                    break;
                }
            }
            return result;
        }

        private static int compareIndex(int x, int y) {
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    }

    /**
     * 合并KeyValue的value
     * @param <V>
     */
    static class KVCombiner<V> implements Combiner<KeyValue<RowIndexKey<int[]>, V[]>> {

        private List<? extends Combiner<V>> combiners;

        public KVCombiner(List<? extends Combiner<V>> combiners) {
            this.combiners = combiners;
        }

        @Override
        public void combine(KeyValue<RowIndexKey<int[]>, V[]> current, KeyValue<RowIndexKey<int[]>, V[]> other) {
            V[] values = current.getValue();
            V[] otherValues = other.getValue();
            for (int i = 0; i < combiners.size(); i++) {
                combiners.get(i).combine(values[i], otherValues[i]);
            }
        }
    }
}
