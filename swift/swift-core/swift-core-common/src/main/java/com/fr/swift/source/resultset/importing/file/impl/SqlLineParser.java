package com.fr.swift.source.resultset.importing.file.impl;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.resultset.importing.file.FileLineParser;

import java.util.List;

/**
 * TODO 实现
 *
 * @author yee
 * @date 2018-12-24
 */
public class SqlLineParser implements FileLineParser {
    private Row firstRow;

    @Override
    public Row parseLine(String line) {
        return null;
    }

    @Override
    public List<SwiftMetaDataColumn> parseColumns(String head, String firstRow) {
        return null;
    }

    @Override
    public Row firstRow() {
        return firstRow;
    }

    @Override
    public boolean isSkipFirstLine() {
        return false;
    }

    @Override
    public void setColumns(List<SwiftMetaDataColumn> columns) {

    }
}
