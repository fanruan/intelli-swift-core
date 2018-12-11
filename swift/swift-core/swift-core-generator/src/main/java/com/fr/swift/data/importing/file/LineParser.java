package com.fr.swift.data.importing.file;

import com.fr.swift.source.Row;

/**
 * @author yee
 * @date 2018-12-10
 */
public interface LineParser {
    Row parseLine(String line);
}
