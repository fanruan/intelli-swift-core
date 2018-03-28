package com.fr.swift.segment.operator.insert;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;

import java.util.List;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description 指定数据和块insert，块必须是新块。
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeSwiftInserter extends AbstractInserter {

    public RealtimeSwiftInserter(Segment segment) throws Exception {
        super(segment);
    }

    public RealtimeSwiftInserter(Segment segment, List<String> fields) throws Exception {
        super(segment, fields);
    }

    @Override
    public void release() {
        for (String field : fields) {
            segment.getColumn(new ColumnKey(field)).getBitmapIndex().putNullIndex(nullMap.get(field));
        }
    }
}
