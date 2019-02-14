package com.fr.swift.segment.backup;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.operator.insert.BaseInserter;
import com.fr.swift.source.Row;
import com.fr.swift.transaction.TransactionManager;

import java.util.List;

/**
 * This class created on 2018/6/22
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "segmentBackup")
@SwiftScope("prototype")
public class FileSegmentBackup extends BaseInserter implements SwiftSegmentBackup {

    protected TransactionManager transactionManager;

    public FileSegmentBackup(Segment segment, Segment currentSegment, List<String> fields) {
        super(segment, fields);
        transactionManager = SwiftContext.get().getBean("transactionManager", TransactionManager.class, segment);
        transactionManager.setOldAttach(currentSegment);
    }

    @Override
    public void backupRowData(int cursor, Row rowData) {
        putRow(cursor, rowData);
    }

    @Override
    public void backupNullIndex() {
        putNullIndex();
    }

    @Override
    protected void putNullIndex() {
        boolean readable = CubeUtil.isReadable(segment);

        for (int i = 0; i < columns.size(); i++) {
            BitmapIndexedColumn bitmapIndex = columns.get(i).getBitmapIndex();
            ImmutableBitMap nullIndex;
            if (bitmapIndex.isReadable()) {
                try {
                    nullIndex = bitmapIndex.getNullIndex();
                } catch (Exception e) {
                    nullIndex = BitMaps.newRoaringMutable();
                }
            } else if (readable) {
                nullIndex = BitMaps.newRangeBitmap(0, segment.getRowCount());
            } else {
                nullIndex = BitMaps.newRoaringMutable();
            }
            MutableBitMap newNullIndex = nullIndices.get(i);
            newNullIndex.or(nullIndex);
            bitmapIndex.putNullIndex(newNullIndex);
        }
    }

    @Override
    public void backupSegmentInfo(int lastCursor, int cursor) {
        putSegmentInfo(lastCursor, cursor);
    }

    @Override
    public void release() {
        super.release();
    }

    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }
}