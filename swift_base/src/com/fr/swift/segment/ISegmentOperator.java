package com.fr.swift.segment;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;


/**
 * This class created on 2018-1-10 10:51:13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface  ISegmentOperator {

    void transport(SwiftResultSet swiftResultSet) throws Exception;

    void transportRow(long row, String allotColumn, Row data) throws Exception;

    void finishTransport();

    int getSegmentCount();
}
