package com.fr.swift.segment.operator.insert;

import com.fr.swift.cube.CubeUtil;
import com.fr.swift.exception.RealtimeInsertException;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.ListResultSet;
import com.fr.swift.transatcion.Transactional;

import java.sql.SQLException;
import java.util.Collections;
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
    public SwiftInserter(Segment segment) {
        super(segment);
    }

    public SwiftInserter(Segment segment, List<String> fields) {
        super(segment, fields);
    }

    @Override
    @Transactional(value = RealtimeInsertException.class)
    public List<Segment> insertData(List<Row> rowList) throws RealtimeInsertException {
        return insertData(new ListResultSet(segment.getMetaData(), rowList));
    }

    @Override
    @Transactional(value = RealtimeInsertException.class)
    public List<Segment> insertData(SwiftResultSet swiftResultSet) throws RealtimeInsertException {
        try {
            // fixme 要从配置里判断，这里有可能读的recorder备份的数据
            boolean readable = CubeUtil.isReadable(segment);
            int lastCursor = readable ? segment.getRowCount() : 0,
                    cursor = lastCursor;

            while (swiftResultSet.next()) {
                Row rowData = swiftResultSet.getRowData();
                putRow(cursor, rowData);
                cursor++;
            }

            putNullIndex();

            putSegmentInfo(lastCursor, cursor);

            release();

            return Collections.singletonList(segment);
        } catch (Exception e) {
            throw new RealtimeInsertException(e);
        }
    }
}