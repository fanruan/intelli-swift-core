package com.fr.swift.generate;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.Releasable;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.task.Task.Result;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.generate.history.index.ColumnDictMerger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.setting.PerformancePlugManager;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.DataSource;
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
public abstract class BaseColumnIndexer<T extends Comparable<T>> extends BaseWorker {
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
            Column<T> column = segment.getColumn(key);
            buildColumnIndex(column, segment.getRowCount());
        }
    }

    protected List<Segment> getSegments() {
        return LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
    }

    protected abstract void releaseIfNeed(Releasable baseColumn);

    private void buildColumnIndex(Column<T> column, int rowCount) {
        Map<T, IntList> map;
        IResourceLocation location = column.getLocation();

        if (getClassType() == STRING) {
            // String类型的没写明细，数据写到外排map里了，所以这里可以直接开始索引了
            // @see FakeStringDetailColumn#calExternalLocation
            ExternalMap<T, IntList> extMap = newIntListExternalMap(
                    column.getDictionaryEncodedColumn().getComparator(),
                    location.buildChildLocation(EXTERNAL_STRING).getPath());
            extMap.readExternal();
            map = extMap;
        } else {
            map = mapDictValueToRows(column, rowCount);
        }

        iterateBuildIndex(toIterable(map), column);

        // ext map用完release，不然线程爆炸
        if (map instanceof ExternalMap) {
            ((ExternalMap) map).release();
        }
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

    private void iterateBuildIndex(Iterable<Entry<T, IntList>> iterable, Column<T> column) {
        DictionaryEncodedColumn<T> dictColumn = column.getDictionaryEncodedColumn();
        BitmapIndexedColumn indexColumn = column.getBitmapIndex();

        int pos = 0;
        for (Entry<T, IntList> entry : iterable) {
            dictColumn.putValue(pos, entry.getKey());

            IntList rows = entry.getValue();
            MutableBitMap bitmap = BitMaps.newRoaringMutable();

            for (int i = 0, len = rows.size(), row; i < len; i++) {
                row = rows.get(i);
                dictColumn.putIndex(row, pos);
                bitmap.add(row);
            }
            indexColumn.putBitMapIndex(pos, bitmap);

            pos++;
        }
        dictColumn.putSize(pos);

        releaseIfNeed(dictColumn);
        releaseIfNeed(indexColumn);
    }

    protected void mergeDict() {
        new ColumnDictMerger<T>(dataSource, key).work();
    }

    private Map<T, IntList> newIntListSortedMap(Column<T> column) {
        Comparator<T> c = column.getDictionaryEncodedColumn().getComparator();
        return PerformancePlugManager.getInstance().isDiskSort() ?
                newIntListExternalMap(c, column.getLocation().buildChildLocation("external").getPath()) :
                new TreeMap<T, IntList>(c);
    }

    private ExternalMap<T, IntList> newIntListExternalMap(Comparator<T> c, String path) {
        return IntListExternalMapFactory.getIntListExternalMap(getClassType(), c, path, true);
    }

    private ClassType getClassType() {
        try {
            return PrivateUtil.getClassType(dataSource, key);
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

    private static <V extends Comparable<V>> Iterable<Entry<V, IntList>> toIterable(Map<V, IntList> map) {
        if (map instanceof ExternalMap) {
            return (ExternalMap<V, IntList>) map;
        }
        return map.entrySet();
    }
}