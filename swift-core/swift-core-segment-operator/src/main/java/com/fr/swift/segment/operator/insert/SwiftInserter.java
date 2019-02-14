package com.fr.swift.segment.operator.insert;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.Row;
import com.fr.swift.util.IoUtil;

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

    public SwiftInserter(Segment segment, List<String> fields) {
        super(segment, fields);
        initCursors();
    }

    void initCursors() {
        lastCursor = cursor = 0;
    }

    public SwiftInserter(Segment segment) {
        this(segment, segment.getMetaData().getFieldNames());
    }

    @Override
    public void insertData(Row rowData) {
        putRow(cursor++, rowData);
    }

    @Override
    public void insertData(SwiftResultSet resultSet) throws Exception {
        try {
            while (resultSet.hasNext()) {
                insertData(resultSet.getNextRow());
            }
        } finally {
            IoUtil.close(resultSet);
            IoUtil.release(this);
        }
    }

    @Override
    public void release() {
        try {
            putNullIndex();
            putSegmentInfo(lastCursor, cursor);
        } finally {
            super.release();
        }
    }
}