package com.fr.swift.cloud.segment.operator.insert;

import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.beans.annotation.SwiftScope;
import com.fr.swift.cloud.cube.CubeUtil;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.segment.Segment;
import com.fr.swift.cloud.segment.operator.Inserter;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.transaction.Transactional;
import com.fr.swift.cloud.util.IoUtil;

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

    protected int cursor, lastCursor;

    /**
     * 是否追加insert
     */
    private boolean append;

    private SwiftInserter(Segment segment, List<String> fields, boolean append) {
        super(segment, fields);
        this.append = append;
        initCursors();
    }

    protected SwiftInserter(Segment segment, boolean append) {
        this(segment, segment.getMetaData().getFieldNames(), append);
    }

    public static SwiftInserter ofAppendMode(Segment seg) {
        return new SwiftInserter(seg, true);
    }

    public static SwiftInserter ofOverwriteMode(Segment seg) {
        return new SwiftInserter(seg, false);
    }

    private void initCursors() {
        if (append) {
            boolean readable = CubeUtil.isReadable(segment);
            cursor = lastCursor = readable ? segment.getRowCount() : 0;
        } else {
            lastCursor = cursor = 0;
        }
    }

    @Override
    public void insertData(Row rowData) {
        putRow(cursor, rowData);
        cursor++;
    }

    @Override
    @Transactional
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