package com.fr.swift.segment.backup;

import com.fr.swift.source.Row;
import com.fr.swift.transatcion.TransactionManager;

/**
 * This class created on 2018/6/22
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftSegmentBackup {

    void backupRowData(int cursor, Row rowData);

    void backupNullIndex();

    void backupSegmentInfo(int lastCursor, int cursor);

    void release();

    TransactionManager getTransactionManager();
}
