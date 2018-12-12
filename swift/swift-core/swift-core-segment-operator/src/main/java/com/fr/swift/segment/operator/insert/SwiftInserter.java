package com.fr.swift.segment.operator.insert;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.Row;
import com.fr.swift.source.resultset.IterableResultSet;

import java.util.List;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description 指定数据和块insert
 * @since Advanced FineBI Analysis 1.0
 */
@SwiftBean(name = "inserter")
@SwiftScope("prototype")
public class SwiftInserter extends BaseInserter implements Inserter {
    int cursor, lastCursor;

    public SwiftInserter(Segment segment) {
        super(segment);
    }

    public SwiftInserter(Segment segment, List<String> fields) {
        super(segment, fields);
    }

    @Override
    public void insertData(List<Row> rowList) throws Exception {
        insertData(new IterableResultSet(rowList, segment.getMetaData()));
    }

    /**
     * 默认从头开始
     */
    void initCursors() {
        cursor = lastCursor = 0;
    }

    @Override
    public void insertData(SwiftResultSet swiftResultSet) throws Exception {
        initCursors();
        try {
            while (swiftResultSet.hasNext()) {
                Row rowData = swiftResultSet.getNextRow();
                putRow(cursor, rowData);
                cursor++;
            }

            putNullIndex();

            putSegmentInfo(lastCursor, cursor);
        } finally {
            release();
        }

    }
}