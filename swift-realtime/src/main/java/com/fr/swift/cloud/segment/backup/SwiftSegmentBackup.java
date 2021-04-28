package com.fr.swift.cloud.segment.backup;

import com.fr.swift.cloud.cube.io.Releasable;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.transaction.TransactionManager;

/**
 * This class created on 2018/6/22
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftSegmentBackup extends Releasable {

    void backupRowData(int cursor, Row rowData);

    void backupNullIndex();

    void backupSegmentInfo(int lastCursor, int cursor);

    TransactionManager getTransactionManager();
}
