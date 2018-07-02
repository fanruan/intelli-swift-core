package com.fr.swift.segment.backup;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.operator.insert.BaseInserter;
import com.fr.swift.source.Row;
import com.fr.swift.transatcion.TransactionManager;

import java.util.List;

/**
 * This class created on 2018/6/22
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class FileSegmentBackup extends BaseInserter implements SwiftSegmentBackup {

    protected TransactionManager transactionManager;

    private Segment currentSegment;

    public FileSegmentBackup(Segment segment, Segment currentSegment, List<String> fields) {
        super(segment, fields);
        this.currentSegment = currentSegment;
        transactionManager = (TransactionManager) SwiftContext.getInstance().getBean("transactionManager", segment);
        transactionManager.setOldAttatch(currentSegment);
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