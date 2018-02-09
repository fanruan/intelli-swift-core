package com.fr.swift.generate.history;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.task.Task.Result;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.exception.meta.SwiftMetaDataException;
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
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.external.map.ExternalMap;
import com.fr.swift.structure.external.map.intlist.map2.ExternalIntArrayMapFactory;
import com.fr.swift.util.Crasher;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import static com.fr.swift.cube.io.IOConstant.NULL_DOUBLE;
import static com.fr.swift.cube.io.IOConstant.NULL_INT;
import static com.fr.swift.cube.io.IOConstant.NULL_LONG;
import static com.fr.swift.cube.io.IOConstant.NULL_STRING;
import static com.fr.swift.source.ColumnTypeConstants.ClassType.STRING;


/**
 * This class created on 2017-12-28 10:54:47
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ColumnIndexer<T extends Comparable<T>> extends BaseWorker {
    private DataSource dataSource;
    private ColumnKey key;
    private List<Segment> segments;

    public ColumnIndexer(DataSource dataSource, ColumnKey key) {
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
        segments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        for (Segment segment : segments) {
            Column<T> column = segment.getColumn(key);
            buildColumnIndex(column, segment.getRowCount());
        }
    }

    private void buildColumnIndex(Column<T> column, int rowCount) {
        Map<T, IntList> map;
        Iterator<Entry<T, IntList>> itr;

        if (getClassType() == STRING) {
            // String类型的没写明细，数据写到外排map里了，所以这里可以直接开始索引了
            ExternalMap<T, IntList> extMap = newIntListExternalMap((Comparator<T>) Comparators.PINYIN_ASC);
            extMap.readExternal();
            map = extMap;
            itr = extMap.iterator();
        } else {
            map = mapDictValueToRows(column, rowCount);
            itr = getItr(map);
        }

        iterateBuildIndex(itr, column);

        // ext map用完release，不然线程爆炸
        if (map instanceof ExternalMap) {
            ((ExternalMap) map).release();
        }
    }

    private Map<T, IntList> mapDictValueToRows(Column<T> column, int rowCount) {
        DetailColumn<T> detailColumn = column.getDetailColumn();
        ImmutableBitMap nullIndex = column.getBitmapIndex().getNullIndex();
        // 字典值 -> 值对应的所有行号
        Map<T, IntList> map = newIntListSortedMap(Comparators.<T>asc());
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

        detailColumn.release();

        return map;
    }

    private void iterateBuildIndex(Iterator<Entry<T, IntList>> itr, Column<T> column) {
        DictionaryEncodedColumn<T> dictColumn = column.getDictionaryEncodedColumn();
        BitmapIndexedColumn indexColumn = column.getBitmapIndex();

        int pos = 0;
        while (itr.hasNext()) {
            Entry<T, IntList> entry = itr.next();
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

        dictColumn.release();
        indexColumn.release();
    }

    private void mergeDict() {
        new ColumnDictMerger<T>(dataSource, key).work();
    }

    private Map<T, IntList> newIntListSortedMap(Comparator<T> c) {
        return PerformancePlugManager.getInstance().isDiskSort() ?
                newIntListExternalMap(c) :
                new TreeMap<T, IntList>(c);
    }

    private ExternalMap<T, IntList> newIntListExternalMap(Comparator<T> c) {
        return ExternalIntArrayMapFactory.getIntListExternalMap(getClassType(), c, calExternalLocation(), true);
    }

    /**
     * 计算对应的外排数据存放位置
     * Column数据位置：.../table/segment/column/...
     * 对应的字典External数据位置 ：.../table/segment/column/external/...
     *
     * @return extMap位置
     */
    private String calExternalLocation() {
        // fixme 位置拿的不对， 要针对每个segment拿
        return segments.get(0).getLocation()
                .buildChildLocation(key.getName())
                .buildChildLocation("external")
                .getPath();
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

    private Iterator<Entry<T, IntList>> getItr(Map<T, IntList> map) {
        return PerformancePlugManager.getInstance().isDiskSort() ?
                ((ExternalMap<T, IntList>) map).iterator() :
                map.entrySet().iterator();
    }
}