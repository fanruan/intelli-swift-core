package com.fr.swift.transaction;


import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.transatcion.TransactionManager;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/6/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service
public abstract class AbstractTransactionManager implements TransactionManager<Segment> {

    private Segment realSegment;

    private SwiftMetaData metaData;

    private int oldRowCount;

    @Override
    public void setOldAttatch(Segment realSegment) {
        this.realSegment = realSegment;
        this.metaData = realSegment.getMetaData();
    }

    @Override
    public void start() {
        try {
            oldRowCount = realSegment.getRowCount();
        } catch (Exception e) {
            oldRowCount = 0;
        }
    }

    @Override
    public void commit() {

    }

    @Override
    public void rollback() {
        realSegment.putRowCount(oldRowCount);
        if (oldRowCount == 0) {
            realSegment.putAllShowIndex(BitMaps.newAllShowBitMap(oldRowCount));
            for (String column : metaData.getFieldNames()) {
                int size = realSegment.getColumn(new ColumnKey(column)).getDictionaryEncodedColumn().size();
                for (int i = 0; i < size; i++) {
                    realSegment.getColumn(new ColumnKey(column)).getBitmapIndex().putBitMapIndex(i, BitMaps.newAllShowBitMap(oldRowCount));
                }
            }
        } else {
            realSegment.putAllShowIndex(realSegment.getAllShowIndex().getAnd(BitMaps.newRangeBitmap(0, oldRowCount)));
            for (String column : metaData.getFieldNames()) {
                int size = realSegment.getColumn(new ColumnKey(column)).getDictionaryEncodedColumn().size();
                for (int i = 0; i < size; i++) {
                    ImmutableBitMap newBitMap = realSegment.getColumn(new ColumnKey(column)).getBitmapIndex().getBitMapIndex(i);
                    ImmutableBitMap bitMap = newBitMap.getAnd(BitMaps.newRangeBitmap(0, oldRowCount));
                    MutableBitMap mutableBitMap = BitMaps.newRoaringMutable();
                    mutableBitMap.or(bitMap);
                    realSegment.getColumn(new ColumnKey(column)).getBitmapIndex().putBitMapIndex(i, mutableBitMap);
                }
            }
        }
    }

    @Override
    public void close() {

    }
}
