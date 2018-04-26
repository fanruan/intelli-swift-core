package com.fr.swift.generate.segment.operator.merger;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.insert.AbstractInserter;

/**
 * This class created on 2018/4/24
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class MergerInserter extends AbstractInserter {

    public MergerInserter(Segment segment) throws Exception {
        super(segment);
    }

    @Override
    public void release() {
        for (String field : fields) {
            segment.getColumn(new ColumnKey(field)).getBitmapIndex().putNullIndex(nullMap.get(field));
            segment.getColumn(new ColumnKey(field)).getBitmapIndex().release();
            segment.getColumn(new ColumnKey(field)).getDetailColumn().release();
        }
        segment.release();
    }
}
