package com.fr.swift.generate;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.operator.column.SwiftColumnDictMerger;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.IntPair;
import com.fr.swift.structure.external.map.ExternalMap;
import com.fr.swift.structure.external.map.intpairs.IntPairsExtMaps;
import com.fr.swift.task.TaskResult.Type;
import com.fr.swift.task.impl.BaseWorker;
import com.fr.swift.task.impl.TaskResultImpl;
import com.fr.swift.util.Crasher;

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
@SwiftBean(name = "columnDictMerger")
@SwiftScope("prototype")
public class ColumnDictMerger<T> extends BaseWorker implements SwiftColumnDictMerger {
    private SwiftMetaData meta;
    protected ColumnKey key;
    protected List<Segment> segments;

    public ColumnDictMerger(DataSource dataSource, ColumnKey key, List<Segment> segments) {
        this(dataSource.getMetadata(), key, segments);
    }

    public ColumnDictMerger(SwiftMetaData meta, ColumnKey key, List<Segment> segments) {
        this.meta = meta;
        this.key = key;
        this.segments = segments;
    }

    @Override
    public void work() {
        try {
            mergeDict();
            workOver(new TaskResultImpl(Type.SUCCEEDED));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            workOver(new TaskResultImpl(Type.FAILED, e));
        }
    }

    @Override
    public void mergeDict() throws Exception {
        if (segments.isEmpty()) {
            return;
        }

        // 值 -> (块号, 值在这块里的序号)
        Map<T, List<IntPair>> map = newIntPairsSortedMap(segments.get(0));

        List<DictionaryEncodedColumn<T>> dictColumns = new ArrayList<DictionaryEncodedColumn<T>>(segments.size());
        List<Column<T>> columns = new ArrayList<Column<T>>(segments.size());
        for (int segOrder = 0, size = segments.size(); segOrder < size; segOrder++) {

            Column<T> column = getColumn(segments.get(segOrder));
            columns.add(column);
            DictionaryEncodedColumn<T> dictColumn = column.getDictionaryEncodedColumn();
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
                dictColumn.putter().putGlobalIndex(pair.getValue(), globalIndex);
            }
            globalIndex++;
        }
        if (columns.size() != dictColumns.size()) {
            Crasher.crash("mergeDict error! columns size not  not equal to dictColumns size!");
            return;
        }
        for (DictionaryEncodedColumn<T> dictColumn : dictColumns) {
            dictColumn.putter().putGlobalIndex(0, 0);
            dictColumn.putter().putGlobalSize(globalIndex);
        }

        SegmentUtils.release(segments);
        SegmentUtils.releaseColumns(columns);

        // 外排map释放并清除文件
        map.clear();
    }

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
    protected IResourceLocation calExternalLocation(Segment oneOfSegments) {
        return oneOfSegments.getLocation().getParent().
                buildChildLocation("external_global_dict").
                buildChildLocation(key.getName());
    }

    private Map<T, List<IntPair>> newIntPairsSortedMap(Segment oneOfSegments) throws SwiftMetaDataException {
        IResourceLocation path = calExternalLocation(oneOfSegments);

        Comparator<T> c = oneOfSegments.<T>getColumn(key).getDictionaryEncodedColumn().getComparator();

        if (path.getStoreType().isTransient()) {
            return new TreeMap<T, List<IntPair>>(c);
        }

        SwiftMetaDataColumn columnMeta = meta.getColumn(key.getName());
        ClassType classType = ColumnTypeUtils.getClassType(columnMeta);
        return IntPairsExtMaps.newExternalMap(classType, c, path.getAbsolutePath());
    }
}