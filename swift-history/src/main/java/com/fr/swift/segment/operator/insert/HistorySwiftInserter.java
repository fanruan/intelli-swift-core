package com.fr.swift.segment.operator.insert;

import com.fr.swift.segment.Segment;

import java.util.List;

/**
 * This class created on 2018/3/23
 *
 * @author Lucifer
 * @description 指定数据和块insert，块必须是新块。
 * @since Advanced FineBI Analysis 1.0
 */
@Deprecated
public class HistorySwiftInserter extends SwiftInserter {

    public HistorySwiftInserter(Segment segment) {
        super(segment);
    }

    public HistorySwiftInserter(Segment segment, List<String> fields) {
        super(segment, fields);
    }
}
