package com.fr.swift.source.resultset.importing.file;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.List;

/**
 * @author yee
 * @date 2018-12-10
 */
public interface FileLineParser {
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

    void setColumns(List<SwiftMetaDataColumn> columns);

    interface LineParserAdaptor {
        Row adapt(Row row);
    }
}
