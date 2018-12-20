package com.fr.swift.source.resultset.impl;

import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;

import java.util.Arrays;

/**
 * @author yee
 * @date 2018-12-20
 */
public abstract class BaseSeparatorLineParser extends BaseFileLineParser {
    private boolean skipFirstLine;


    public BaseSeparatorLineParser(boolean skipFirstLine) {
        this.skipFirstLine = skipFirstLine;
    }

    @Override
    protected Row split(String line) {
        if (line.trim().endsWith(getSeparator())) {
            line = line.substring(0, line.lastIndexOf(getSeparator()));
        }
        String[] strings = line.split(getSeparator());
        return new ListBasedRow(Arrays.asList(strings));
    }

    @Override
    public boolean isSkipFirstLine() {
        return skipFirstLine;
    }

    protected abstract String getSeparator();
}
