package com.fr.swift.data.importing.file;

import com.fr.swift.source.Row;

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
}
