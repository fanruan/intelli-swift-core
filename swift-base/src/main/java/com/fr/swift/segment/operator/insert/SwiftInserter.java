package com.fr.swift.segment.operator.insert;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.resultset.ListResultSet;

import java.util.List;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description 指定数据和块insert
 * todo 支持断点输入流式数据，也支持实时导入数据、实时查询数据功能。
 * @since Advanced FineBI Analysis 1.0
 */
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
        insertData(new ListResultSet(segment.getMetaData(), rowList));
    }

    @Override
    protected void putRow(int cursor, Row rowData) {
        super.putRow(cursor, rowData);
        segment.putRowCount(cursor + 1);
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

        while (swiftResultSet.hasNext()) {
            Row rowData = swiftResultSet.getNextRow();
            putRow(cursor, rowData);
            cursor++;
        }

        putNullIndex();

        putSegmentInfo(lastCursor, cursor);

        release();
    }
}