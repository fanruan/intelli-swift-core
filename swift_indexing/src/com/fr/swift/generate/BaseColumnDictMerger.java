package com.fr.swift.generate;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.task.Task.Result;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.DataSource;
import com.fr.swift.structure.IntPair;
import com.fr.swift.structure.external.map.ExternalMap;
import com.fr.swift.structure.external.map.intpairs.IntPairsExtMaps;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author anchore
 * @date 2018/2/26
 */
public abstract class BaseColumnDictMerger<T> extends BaseWorker {
    protected DataSource dataSource;
    protected ColumnKey key;

    public BaseColumnDictMerger(DataSource dataSource, ColumnKey key) {
        this.key = key;
        this.dataSource = dataSource;
    }

    @Override
    public void work() {
        try {
            mergeDict();
            workOver(Result.SUCCEEDED);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            workOver(Result.FAILED);
        }
    }

    private void mergeDict() {
        List<Segment> segments = getSegments();

        if (segments.isEmpty()) {
            return;
        }

        // 值 -> (块号, 值在这块里的序号)
        Map<T, List<IntPair>> map = newIntPairsSortedMap(segments.get(0));

        List<DictionaryEncodedColumn<T>> dictColumns = new ArrayList<DictionaryEncodedColumn<T>>(segments.size());
        for (int segOrder = 0, size = segments.size(); segOrder < size; segOrder++) {
            DictionaryEncodedColumn<T> dictColumn = getColumn(segments.get(segOrder)).getDictionaryEncodedColumn();
            dictColumns.add(dictColumn);
            extractDictOf(dictColumn, segOrder, map);
        }

        // 有效值序号从1开始
        int globalIndex = DictionaryEncodedColumn.NOT_NULL_START_INDEX;
        for (Entry<T, List<IntPair>> entry : toIterable(map)) {
            List<IntPair> pairs = entry.getValue();
            for (IntPair pair : pairs) {
                // 拿对应块的字典列
                DictionaryEncodedColumn<T> dictColumn = dictColumns.get(pair.getKey());
                // 写入 字典序号 -> 全局序号
                dictColumn.putGlobalIndex(pair.getValue(), globalIndex);
            }
            globalIndex++;
        }
        for (DictionaryEncodedColumn<T> dictColumn : dictColumns) {
            dictColumn.putGlobalIndex(0, 0);
            dictColumn.putGlobalSize(globalIndex);
            releaseIfNeed(dictColumn);
        }

        // 外排map释放并清除文件
        map.clear();
    }

    /**
     * merger会对返回的segments的对应列进行字典合并
     *
     * @return 要字典合并的segments
     */
    protected abstract List<Segment> getSegments();

    protected Column<T> getColumn(Segment segment) {
        return segment.getColumn(key);
    }

    private static <V> void extractDictOf(DictionaryEncodedColumn<V> dictColumn, int segOrder, Map<V, List<IntPair>> map) {
        for (int j = 1, size = dictColumn.size(); j < size; j++) {
            // 值
            V val = dictColumn.getValue(j);
            // (块号, 值在这块里的序号)
            IntPair pair = IntPair.of(segOrder, j);
            if (map.containsKey(val)) {
                map.get(val).add(pair);
            } else {
                List<IntPair> pairs = new ArrayList<IntPair>(1);
                pairs.add(pair);
                map.put(val, pairs);
            }
        }
    }

    protected abstract void releaseIfNeed(Releasable baseColumn);

    private static <V> Iterable<Entry<V, List<IntPair>>> toIterable(Map<V, List<IntPair>> map) {
        if (map instanceof ExternalMap) {
            return (ExternalMap<V, List<IntPair>>) map;
        }
        return map.entrySet();
    }

    /**
     * 计算对应的外排数据存放位置
     * Column数据位置：.../table/segment/column/...
     * 对应的全局字典External数据位置 ：.../table/external_global_dict/column/...
     *
     * @param oneOfSegments 其中一块segment
     * @return extMap位置
     */
    private IResourceLocation calExternalLocation(Segment oneOfSegments) {
        return oneOfSegments.getLocation().getParent().
                buildChildLocation("external_global_dict").
                buildChildLocation(key.getName());
    }

    private Map<T, List<IntPair>> newIntPairsSortedMap(Segment oneOfSegments) {
        IResourceLocation path = calExternalLocation(oneOfSegments);

        Comparator<T> c = oneOfSegments.<T>getColumn(key).getDictionaryEncodedColumn().getComparator();

        if (path.getStoreType() == StoreType.MEMORY) {
            return new TreeMap<T, List<IntPair>>(c);
        }

        ClassType classType = Util.getClassType(dataSource, key);
        return IntPairsExtMaps.newExternalMap(classType, c, path.getPath());
    }
}