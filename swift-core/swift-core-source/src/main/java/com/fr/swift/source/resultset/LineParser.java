package com.fr.swift.source.resultset;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.List;

/**
 * @author yee
 * @date 2018-12-21
 */
public interface LineParser {
    void setColumns(List<SwiftMetaDataColumn> columns);

    /**
     * 将文件的单行解析成Row
     * 如用, \t 等字符分割，sql语句等
     *
     * @param line
     * @return
     */
    Row parseLine(String line);

    List<SwiftMetaDataColumn> parseColumns(String head, String firstRow);

    Row firstRow();

    boolean isSkipFirstLine();

    interface LineParserAdaptor {
        Row adapt(Row row);
    }
}
