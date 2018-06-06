package com.fr.swift.segment.operator;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * This class created on 2018/3/23
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface Inserter {
    List<Segment> insertData(List<Row> rowList) throws SQLException;

    List<Segment> insertData(SwiftResultSet swiftResultSet) throws SQLException;

    List<String> getFields();
}