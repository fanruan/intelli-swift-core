package com.fr.swift.segment.operator.delete;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.util.Interval;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author anchore
 * @date 2018/6/19
 */
public class RangeDeleter implements ValueDeleter {
    protected Segment segment;
    private Map<String, Interval> intervals;

    public RangeDeleter(Segment segment, Map<String, Interval> intervals) {
        this.segment = segment;
        this.intervals = intervals;
    }

    @Override
    public void delete() {
        MutableBitMap toBeDeleted = BitMaps.newRoaringMutable();

        for (Entry<String, Interval> entry : intervals.entrySet()) {
            extractDeleteBitmap(toBeDeleted, entry);
        }

        putAllShowIndex(toBeDeleted);

        releaseIfNeed(segment);
    }

    protected void putAllShowIndex(MutableBitMap toBeDeleted) {
        segment.putAllShowIndex(segment.getAllShowIndex().getAndNot(toBeDeleted));
    }

    private void extractDeleteBitmap(MutableBitMap toBeDeleted, Entry<String, Interval> entry) {
        Column<Object> column = segment.getColumn(new ColumnKey(entry.getKey()));

        DictionaryEncodedColumn<Object> dict = column.getDictionaryEncodedColumn();
        Interval interval = entry.getValue();

        // todo 多次or可以优化下
        BitmapIndexedColumn bitmap = column.getBitmapIndex();
        for (int i = 0; i < dict.size(); i++) {
            if (interval.contains(dict.getValue(i), dict.getComparator())) {
                toBeDeleted.or(bitmap.getBitMapIndex(i));
            }
        }
    }

    protected void releaseIfNeed(Segment segment) {
        if (segment.getLocation().getStoreType() != StoreType.MEMORY) {
            segment.release();
        }
    }
}