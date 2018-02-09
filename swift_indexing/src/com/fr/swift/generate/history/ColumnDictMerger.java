package com.fr.swift.generate.history;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.task.Task.Result;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.external.map.ExternalMap;
import com.fr.swift.structure.external.map.intpairs.IntPairsExtMaps;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author anchore
 * @date 2018/1/9
 * <p>
 * 合并字典
 */
public class ColumnDictMerger<T extends Comparable<T>> extends BaseWorker {
    private DataSource dataSource;
    private ColumnKey key;

    public ColumnDictMerger(DataSource dataSource, ColumnKey key) {
        this.dataSource = dataSource;
        this.key = key;
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
        List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        // 值 -> (块号, 值在这块里的序号)
        ExternalMap<T, List<Pair<Integer, Integer>>> map =
                newIntPairsExternalMap(calExternalLocation(segments.get(0)));

        List<DictionaryEncodedColumn<T>> dictColumns = new ArrayList<DictionaryEncodedColumn<T>>(segments.size());
        for (int segOrder = 0, size = segments.size(); segOrder < size; segOrder++) {
            DictionaryEncodedColumn<T> dictColumn = segments.get(segOrder).<T>getColumn(key).getDictionaryEncodedColumn();
            dictColumns.add(dictColumn);
            extractDictOf(dictColumn, segOrder, map);
        }

        int globalIndex = 0;
        for (Map.Entry<T, List<Pair<Integer, Integer>>> entry : map) {
            List<Pair<Integer, Integer>> pairs = entry.getValue();
            for (Pair<Integer, Integer> pair : pairs) {
                // 拿对应块的字典列
                DictionaryEncodedColumn<T> dictColumn = dictColumns.get(pair.key());
                // 写入 字典序号 -> 全局序号
                dictColumn.putGlobalIndex(pair.value(), globalIndex);
            }
            globalIndex++;
        }
        for (DictionaryEncodedColumn<T> dictColumn : dictColumns) {
            dictColumn.putGlobalSize(globalIndex);
            dictColumn.release();
        }

        map.release();
    }

    private static <V> void extractDictOf(DictionaryEncodedColumn<V> dictColumn, int segOrder, Map<V, List<Pair<Integer, Integer>>> map) {
        for (int j = 0, size = dictColumn.size(); j < size; j++) {
            // 值
            V val = dictColumn.getValue(j);
            // (块号, 值在这块里的序号)
            Pair<Integer, Integer> pair = Pair.of(segOrder, j);
            if (map.containsKey(val)) {
                map.get(val).add(pair);
            } else {
                List<Pair<Integer, Integer>> pairs = new ArrayList<Pair<Integer, Integer>>(1);
                pairs.add(pair);
                map.put(val, pairs);
            }
        }
    }

    /**
     * 计算对应的外排数据存放位置
     * Column数据位置：.../table/segment/column/...
     * 对应的全局字典External数据位置 ：.../table/external/column/...
     *
     * @param seg segment
     * @return extMap位置
     */
    private String calExternalLocation(Segment seg) {
        return seg.getLocation()
                .getParent()
                .buildChildLocation("external")
                .buildChildLocation(key.getName())
                .getPath();
    }

    private <V extends Comparable<V>> ExternalMap<V, List<Pair<Integer, Integer>>> newIntPairsExternalMap(String path) {
        ClassType classType = getClassType();
        switch (classType) {
            case STRING:
                return (ExternalMap<V, List<Pair<Integer, Integer>>>) IntPairsExtMaps.newExternalMap(classType, Comparators.PINYIN_ASC, path);
            default:
                return IntPairsExtMaps.newExternalMap(classType, Comparators.<V>asc(), path);
        }
    }

    private ClassType getClassType() {
        try {
            SwiftMetaDataColumn metaColumn = dataSource.getMetadata().getColumn(key.getName());
            return ColumnTypeUtils.sqlTypeToClassType(
                    metaColumn.getType(),
                    metaColumn.getPrecision(),
                    metaColumn.getScale()
            );
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
    }
}
