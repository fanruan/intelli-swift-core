package com.fr.swift.generate;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.Releasable;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.task.Task.Result;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.setting.PerformancePlugManager;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.external.map.ExternalMap;
import com.fr.swift.structure.external.map.intlist.IntListExternalMapFactory;
import com.fr.swift.util.Crasher;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import static com.fr.swift.cube.io.IOConstant.NULL_DOUBLE;
import static com.fr.swift.cube.io.IOConstant.NULL_INT;
import static com.fr.swift.cube.io.IOConstant.NULL_LONG;
import static com.fr.swift.cube.io.IOConstant.NULL_STRING;
import static com.fr.swift.segment.column.impl.base.FakeStringDetailColumn.EXTERNAL_STRING;
import static com.fr.swift.source.ColumnTypeConstants.ClassType.STRING;

/**
 * @author anchore
 * @date 2018/2/26
 */
public abstract class BaseColumnIndexer<T> extends BaseWorker {
    protected DataSource dataSource;
    protected ColumnKey key;

    public BaseColumnIndexer(DataSource dataSource, ColumnKey key) {
        this.dataSource = dataSource;
        this.key = key;
    }

    @Override
    public void work() {
        try {
            buildIndex();
            mergeDict();
            workOver(Result.SUCCEEDED);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            workOver(Result.FAILED);
        }
    }

    private void buildIndex() {
        List<Segment> segments = getSegments();
        for (Segment segment : segments) {
            Column<T> column = getColumn(segment);
            buildColumnIndex(column, segment.getRowCount());
        }
    }

    /**
     * indexer 会对返回的segments的对应列进行索引
     *
     * @return 要索引的segments
     */
    protected abstract List<Segment> getSegments();

    protected Column<T> getColumn(Segment segment) {
        return segment.getColumn(key);
    }

    /**
     * 如有必要就释放
     *
     * @param baseColumn 基础列
     */
    protected abstract void releaseIfNeed(Releasable baseColumn);

    private void buildColumnIndex(Column<T> column, int rowCount) {
        Map<T, IntList> map;
        IResourceLocation location = column.getLocation();

        if (isDetailInExternal(getClassType(), location.getStoreType())) {
            ExternalMap<T, IntList> extMap = newIntListExternalMap(
                    column.getDictionaryEncodedColumn().getComparator(),
                    location.buildChildLocation(EXTERNAL_STRING).getPath());
            extMap.readExternal();
            map = extMap;
        } else {
            map = mapDictValueToRows(column, rowCount);
        }

        iterateBuildIndex(toIterable(map), column, rowCount);

        // ext map用完release，不然线程爆炸
        map.clear();

    }

    private static boolean isDetailInExternal(ClassType klass, StoreType storeType) {
        // 非内存的String类型没写明细，数据写到外排map里了，所以这里可以直接开始索引了
        // @see FakeStringDetailColumn#calExternalLocation
        return klass == STRING && storeType != StoreType.MEMORY;
    }

    private Map<T, IntList> mapDictValueToRows(Column<T> column, int rowCount) {
        DetailColumn<T> detailColumn = column.getDetailColumn();
        ImmutableBitMap nullIndex = column.getBitmapIndex().getNullIndex();
        // 字典值 -> 值对应的所有行号
        Map<T, IntList> map = newIntListSortedMap(column);
        for (int i = 0; i < rowCount; i++) {
            T val = detailColumn.get(i);
            if (isNullValue(val) && nullIndex.contains(i)) {
                continue;
            }
            if (map.containsKey(val)) {
                map.get(val).add(i);
            } else {
                IntList list = IntListFactory.createIntList();
                list.add(i);
                map.put(val, list);
            }
        }

        releaseIfNeed(detailColumn);

        return map;
    }

    private void iterateBuildIndex(Iterable<Entry<T, IntList>> iterable, Column<T> column, int rowCount) {
        DictionaryEncodedColumn<T> dictColumn = column.getDictionaryEncodedColumn();
        BitmapIndexedColumn indexColumn = column.getBitmapIndex();

        // row -> index， 0作为null值行号的对应序号
        int[] rowToIndex = new int[rowCount];

        // 有效值序号从1开始
        int pos = 1;
        for (Entry<T, IntList> entry : iterable) {
            dictColumn.putValue(pos, entry.getKey());

            IntList rows = entry.getValue();
            MutableBitMap bitmap = BitMaps.newRoaringMutable();

            for (int i = 0, len = rows.size(), row; i < len; i++) {
                row = rows.get(i);
                rowToIndex[row] = pos;
                bitmap.add(row);
            }
            indexColumn.putBitMapIndex(pos, bitmap);

            pos++;
        }

        dictColumn.putSize(pos);

        for (int row = 0, len = rowToIndex.length; row < len; row++) {
            dictColumn.putIndex(row, rowToIndex[row]);
        }

        releaseIfNeed(dictColumn);
        releaseIfNeed(indexColumn);
    }

    /**
     * 全局字典
     */
    protected abstract void mergeDict();

    private Map<T, IntList> newIntListSortedMap(Column<T> column) {
        Comparator<T> c = column.getDictionaryEncodedColumn().getComparator();
        return PerformancePlugManager.getInstance().isDiskSort() ?
                newIntListExternalMap(c, column.getLocation().buildChildLocation("external_index").getPath()) :
                new TreeMap<T, IntList>(c);
    }

    private ExternalMap<T, IntList> newIntListExternalMap(Comparator<T> c, String path) {
        return IntListExternalMapFactory.getIntListExternalMap(getClassType(), c, path, true);
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

    private static <V> boolean isNullValue(V val) {
        return val.equals(NULL_INT) ||
                val.equals(NULL_LONG) ||
                val.equals(NULL_DOUBLE) ||
                val.equals(NULL_STRING);
    }

    private static <V> Iterable<Entry<V, IntList>> toIterable(Map<V, IntList> map) {
        if (map instanceof ExternalMap) {
            return (ExternalMap<V, IntList>) map;
        }
        return map.entrySet();
    }
}