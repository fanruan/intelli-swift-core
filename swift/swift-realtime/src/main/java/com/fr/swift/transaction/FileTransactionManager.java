package com.fr.swift.transaction;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.AllShowBitMap;
import com.fr.swift.bitmap.impl.EmptyBitmap;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;

import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2018/6/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "transactionManager")
@SwiftScope("prototype")
public class FileTransactionManager extends AbstractTransactionManager {

    private Segment hisSegment;

    private int oldRowCount;
    private ImmutableBitMap oldAllShowIndex;
    private Map<String, ImmutableBitMap> oldNullIndexMap = new HashMap<String, ImmutableBitMap>();

    public FileTransactionManager(Segment hisSegment) {
        this.hisSegment = hisSegment;
    }

    @Override
    public void start() {
        super.start();
        if (hisSegment.isReadable()) {
            this.oldRowCount = hisSegment.getRowCount();

            try {
                this.oldAllShowIndex = hisSegment.getAllShowIndex();
            } catch (Exception e) {
                this.oldAllShowIndex = AllShowBitMap.of(oldRowCount);
            }

            for (String fieldName : hisSegment.getMetaData().getFieldNames()) {
                Column column = hisSegment.getColumn(new ColumnKey(fieldName));
                BitmapIndexedColumn bitmapIndex = column.getBitmapIndex();

                ImmutableBitMap nullIndex;
                try {
                    nullIndex = bitmapIndex.isReadable() ? bitmapIndex.getNullIndex() : new EmptyBitmap();
                } catch (Exception e) {
                    nullIndex = new EmptyBitmap();
                }

                oldNullIndexMap.put(fieldName, nullIndex);
            }
        } else {
            this.oldRowCount = 0;
            this.oldAllShowIndex = BitMaps.newAllShowBitMap(0);
            for (String fieldName : hisSegment.getMetaData().getFieldNames()) {
                oldNullIndexMap.put(fieldName, BitMaps.newRoaringMutable());
            }
        }
    }

    @Override
    public void commit() {
    }

    @Override
    public void rollback() {
        super.rollback();
        hisSegment.putRowCount(oldRowCount);
        hisSegment.putAllShowIndex(oldAllShowIndex);
        for (String fieldName : hisSegment.getMetaData().getFieldNames()) {
            hisSegment.getColumn(new ColumnKey(fieldName)).getBitmapIndex().putNullIndex(oldNullIndexMap.get(fieldName));
        }

    }

    @Override
    public void close() {
        SegmentUtils.release(hisSegment);
        SegmentUtils.releaseColumnsOf(hisSegment);
    }
}
