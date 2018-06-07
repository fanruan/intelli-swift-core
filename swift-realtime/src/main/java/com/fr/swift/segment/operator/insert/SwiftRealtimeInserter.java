package com.fr.swift.segment.operator.insert;

import com.fr.swift.segment.Segment;

import java.util.List;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description 指定数据和块insert，块必须是新块。
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftRealtimeInserter extends SwiftInserter {
    public SwiftRealtimeInserter(Segment segment) {
        super(segment);
    }

    public SwiftRealtimeInserter(Segment segment, List<String> fields) {
        super(segment, fields);
    }
}