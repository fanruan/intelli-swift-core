package com.fr.swift.generate;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.operator.column.SwiftColumnIndexer;
import com.fr.swift.setting.PerformancePlugManager;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.external.map.ExternalMap;
import com.fr.swift.structure.external.map.intlist.BaseIntListExternalMap;
import com.fr.swift.structure.external.map.intlist.IntListExternalMapFactory;
import com.fr.swift.task.TaskResult.Type;
import com.fr.swift.task.impl.BaseWorker;
import com.fr.swift.task.impl.TaskResultImpl;
import com.fr.swift.util.Assert;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import static com.fr.swift.segment.column.impl.base.FakeStringDetailColumn.EXTERNAL_STRING;

/**
 * @author anchore
 * @date 2018/2/26
 */
@SwiftBean(name = "columnIndexer")
@SwiftScope("prototype")
public class ColumnIndexer<T> extends BaseWorker implements SwiftColumnIndexer {

    private SwiftMetaData meta;
    private ColumnKey key;
    private List<Segment> segments;

    /**
     * segments通过外部传入
     *
     * @param key column key
     * @param segments segs
     */
    public ColumnIndexer(ColumnKey key, List<Segment> segments) {
        Assert.notNull(key);
        Assert.notEmpty(segments);

        this.meta = segments.get(0).getMetaData();
        this.key = key;
        this.segments = segments;
    }

    @Override
    public void work() {
        try {
            buildIndex();
            workOver(new TaskResultImpl(Type.SUCCEEDED));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            workOver(new TaskResultImpl(Type.FAILED, e));
        }
    }

    @Override
    public void buildIndex() throws Exception {
        for (Segment segment : segments) {
            try {
                Column<T> column = getColumn(segment);
                buildColumnIndex(column, segment.getRowCount());
            } finally {
                SegmentUtils.releaseColumnsOf(segment);
                SegmentUtils.release(segment);
            }
        }
    }

    protected Column<T> getColumn(Segment segment) {
        return segment.getColumn(key);
    }

    private void buildColumnIndex(Column<T> column, int rowCount) throws Exception {
        Map<T, IntList> map = null;
        IResourceLocation location = column.getLocation();

        SwiftMetaDataColumn columnMeta = meta.getColumn(key.getName());

        try {
            if (isDetailInExternal(ColumnTypeUtils.getClassType(columnMeta),
                    location.getStoreType())) {
                ExternalMap<T, IntList> extMap = newIntListExternalMap(
                        column.getDictionaryEncodedColumn().getComparator(),
                        location.buildChildLocation(EXTERNAL_STRING).getAbsolutePath());
                extMap.readExternal();
                map = extMap;
            } else {
                map = mapDictValueToRows(column, rowCount);
            }

            iterateBuildIndex(toIterable(map), column, rowCount);
        } finally {
            if (map != null) {
                // ext map用完release，不然线程爆炸
                map.clear();
            }
        }

    }

    private static boolean isDetailInExternal(ClassType klass, StoreType storeType) {
        // 非内存的String类型没写明细，数据写到外排map里了，所以这里可以直接开始索引了
        // @see FakeStringDetailColumn#calExternalLocation
//        return klass == STRING && storeType != StoreType.MEMORY;
        // string明细不写外排了
        return false;
    }

    private Map<T, IntList> mapDictValueToRows(Column<T> column, int rowCount) throws SwiftMetaDataException {
        DetailColumn<T> detailColumn = column.getDetailColumn();
        ImmutableBitMap nullIndex = column.getBitmapIndex().getNullIndex();
        Map<T, IntList> map;
        Comparator<T> c = column.getDictionaryEncodedColumn().getComparator();
        // 字典值 -> 值对应的所有行号
        if (PerformancePlugManager.getInstance().isDiskSort()) {
            BaseIntListExternalMap<T> extMap = newIntListExternalMap(c, column.getLocation().buildChildLocation("external_index").getAbsolutePath());
            for (int i = 0; i < rowCount; i++) {
                if (nullIndex.contains(i)) {
                    continue;
                }
                T val = detailColumn.get(i);
                extMap.put(val, i);
            }
            map = extMap;
        } else {
            map = new TreeMap<T, IntList>(c);
            for (int i = 0; i < rowCount; i++) {
                if (nullIndex.contains(i)) {
                    continue;
                }
                T val = detailColumn.get(i);
                if (map.containsKey(val)) {
                    map.get(val).add(i);
                } else {
                    IntList list = IntListFactory.createIntList();
                    list.add(i);
                    map.put(val, list);
                }
            }
        }

        return map;
    }

    private void iterateBuildIndex(Iterable<Entry<T, IntList>> iterable, Column<T> column, int rowCount) {
        DictionaryEncodedColumn<T> dictColumn = column.getDictionaryEncodedColumn();
        BitmapIndexedColumn indexColumn = column.getBitmapIndex();
        ImmutableBitMap nullIndex = indexColumn.getNullIndex();

        // row -> index， 0作为null值行号的对应序号
        int[] rowToIndex = new int[rowCount];

        // 有效值序号从1开始
        int pos = 0;
        dictColumn.putter().putValue(pos++, null);
        for (Entry<T, IntList> entry : iterable) {
            T val = entry.getKey();
            IntList rows = entry.getValue();
            // 考虑到外排map会写入NULL_VALUE，这里判断下
            if (nullIndex.contains(rows.get(0))) {
                continue;
            }

            dictColumn.putter().putValue(pos, val);

            MutableBitMap bitmap = BitMaps.newRoaringMutable();

            for (int i = 0, len = rows.size(), row; i < len; i++) {
                row = rows.get(i);
                rowToIndex[row] = pos;
                bitmap.add(row);
            }
            indexColumn.putBitMapIndex(pos, bitmap);

            pos++;
        }

        dictColumn.putter().putSize(pos);

        for (int row = 0, len = rowToIndex.length; row < len; row++) {
            dictColumn.putter().putIndex(row, rowToIndex[row]);
        }
    }

    private BaseIntListExternalMap<T> newIntListExternalMap(Comparator<T> c, String path) throws SwiftMetaDataException {
        SwiftMetaDataColumn columnMeta = meta.getColumn(key.getName());
        return (BaseIntListExternalMap<T>) IntListExternalMapFactory.getIntListExternalMap(ColumnTypeUtils.getClassType(columnMeta), c, path, true);
    }

    private static <V> Iterable<Entry<V, IntList>> toIterable(Map<V, IntList> map) {
        if (map instanceof ExternalMap) {
            return (ExternalMap<V, IntList>) map;
        }
        return map.entrySet();
    }
}