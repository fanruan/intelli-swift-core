package com.fr.swift.source.resultset.importing.file.impl;

import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;

import java.util.Arrays;

/**
 * @author yee
 * @date 2018-12-20
 */
public abstract class BaseSeparatorLineParser extends BaseFileLineParser {
    private boolean skipFirstLine;
    private String replaceReg;
    private String replacement;

    public BaseSeparatorLineParser(boolean skipFirstLine, LineParserAdaptor adaptor) {
        super(adaptor);
        this.skipFirstLine = skipFirstLine;
        String separator = getSeparator();
        this.replaceReg = String.format("%s%s", separator, separator);
        this.replacement = String.format("%s %s", separator, separator);
    }

    public BaseSeparatorLineParser(boolean skipFirstLine) {
        this(skipFirstLine, null);
    }

    @Override
    protected Row split(String line) {
        String separator = getSeparator();
        String calLine = line;
        String tmp;
        while (!calLine.equals((tmp = calLine.replaceAll(replaceReg, replacement)))) {
            calLine = tmp;
        }
        String[] strings = calLine.split(separator);
        return new ListBasedRow(Arrays.asList(strings));
    }

    @Override
    public boolean isSkipFirstLine() {
        return skipFirstLine;
    }

    protected abstract String getSeparator();
}
