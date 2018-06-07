package com.fr.swift.generate.preview.operator;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.operator.insert.SwiftRealtimeInserter;

import java.util.List;

/**
 * This class created on 2018/3/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
@Deprecated
public class MinorInserter extends SwiftRealtimeInserter {

    public MinorInserter(Segment segment) {
        super(segment);
    }

    public MinorInserter(Segment segment, List<String> fields) {
        super(segment, fields);
    }
}
