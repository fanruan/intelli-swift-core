package com.fr.swift.generate.preview.operator;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.operator.insert.RealtimeSwiftInserter;

import java.util.List;

/**
 * This class created on 2018/3/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
@Deprecated
public class MinorInserter extends RealtimeSwiftInserter {

    public MinorInserter(Segment segment) throws Exception {
        super(segment);
    }

    public MinorInserter(Segment segment, List<String> fields) throws Exception {
        super(segment, fields);
    }
}
