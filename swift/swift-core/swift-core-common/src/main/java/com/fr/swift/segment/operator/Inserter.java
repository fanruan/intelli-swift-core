package com.fr.swift.segment.operator;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;

import java.util.List;

/**
 * This class created on 2018/3/23
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface Inserter {
    void insertData(List<Row> rowList) throws Exception;

    void insertData(SwiftResultSet swiftResultSet) throws Exception;

    List<String> getFields();
}