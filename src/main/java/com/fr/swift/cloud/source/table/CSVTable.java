package com.fr.swift.cloud.source.table;

import com.fr.swift.cloud.source.load.LineParser;

/**
 * Created by lyon on 2019/2/28.
 */
public interface CSVTable extends CloudTable {

    LineParser getParser();
}