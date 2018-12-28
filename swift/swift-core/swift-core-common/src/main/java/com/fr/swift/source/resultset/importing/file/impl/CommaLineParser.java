package com.fr.swift.source.resultset.importing.file.impl;

/**
 * @author yee
 * @date 2018-12-20
 */
public class CommaLineParser extends BaseSeparatorLineParser {

    public CommaLineParser(boolean skipFirstLine) {
        super(skipFirstLine);
    }

    @Override
    protected String getSeparator() {
        return ",";
    }
}
