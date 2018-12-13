package com.fr.swift.segment.operator.collate;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.operator.Collater;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.insert.SwiftInserter;

/**
 * This class created on 2018/7/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractCollater implements Collater {

    protected Segment segment;

    public AbstractCollater(Segment segment) {
        this.segment = segment;
    }

    @Override
    public void collate(SwiftResultSet swiftResultSet) throws Exception {
        Inserter inserter = new SwiftInserter(segment);
        try {
            inserter.insertData(swiftResultSet);
        } finally {
            swiftResultSet.close();
        }
    }
}
