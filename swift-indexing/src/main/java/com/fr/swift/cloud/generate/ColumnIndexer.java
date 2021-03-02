package com.fr.swift.cloud.generate;

import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.beans.annotation.SwiftScope;
import com.fr.swift.cloud.bitmap.BitMaps;
import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.bitmap.MutableBitMap;
import com.fr.swift.cloud.bitmap.traversal.TraversalAction;
import com.fr.swift.cloud.cube.io.Types.StoreType;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.exception.meta.SwiftMetaDataException;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.SegmentUtils;
import com.fr.swift.cloud.segment.column.BitmapIndexedColumn;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.segment.column.DetailColumn;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.segment.column.impl.base.FakeStringDetailColumn;
import com.fr.swift.cloud.segment.operator.column.SwiftColumnIndexer;
import com.fr.swift.cloud.setting.PerformancePlugManager;
import com.fr.swift.cloud.source.ColumnTypeConstants.ClassType;
import com.fr.swift.cloud.source.ColumnTypeUtils;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.source.SwiftMetaDataColumn;
import com.fr.swift.cloud.structure.array.IntList;
import com.fr.swift.cloud.structure.array.IntListFactory;
import com.fr.swift.cloud.structure.external.map.ExternalMap;
import com.fr.swift.cloud.structure.external.map.intlist.BaseIntListExternalMap;
import com.fr.swift.cloud.structure.external.map.intlist.IntListExternalMapFactory;
import com.fr.swift.cloud.util.Assert;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author anchore
 * @date 2018/2/26
 */
@SwiftBean(name = "columnIndexer")
@SwiftScope("prototype")
public class ColumnIndexer<T> implements SwiftColumnIndexer {

    private SwiftMetaData meta;
    private ColumnKey key;
    private List<Segment> segments;

    /**
     * segments通过外部传入
     *
     * @param key      column key
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
    public void buildIndex() throws Exception {
        for (Segment segment : segments) {
            try {
                Column<T> column = getColumn(segment);
                buildColumnIndex(column, segment.getRowCount());
            } finally {
                SegmentUtils.releaseHisSeg(segment);
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
                        location.buildChildLocation(FakeStringDetailColumn.EXTERNAL_STRING).getAbsolutePath());
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
            indexColumn.putBitMapIndex(pos, convertIfNeeded(bitmap));

            pos++;
        }

        dictColumn.putter().putSize(pos);

        for (int row = 0, len = rowToIndex.length; row < len; row++) {
            dictColumn.putter().putIndex(row, rowToIndex[row]);
        }
    }

    private static ImmutableBitMap convertIfNeeded(ImmutableBitMap bitMap) {
        // 基数为1的情况下使用IdBitmap，反序列快点
        if (bitMap.getCardinality() != 1) {
            return bitMap;
        }
        final int[] id = new int[1];
        bitMap.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                id[0] = row;
            }
        });
        return BitMaps.newIdBitMap(id[0]);
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