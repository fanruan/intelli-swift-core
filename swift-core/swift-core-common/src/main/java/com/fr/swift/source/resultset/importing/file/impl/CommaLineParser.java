package com.fr.swift.source.resultset.importing.file.impl;

/**
 * @author yee
 * @date 2018-12-20
 */
public class CommaLineParser extends BaseSeparatorLineParser {

    public CommaLineParser(boolean skipFirstLine) {
        super(skipFirstLine);
    }

    public CommaLineParser(boolean skipFirstLine, LineParserAdaptor adaptor) {
        super(skipFirstLine, adaptor);
    }

    @Override
    protected String getSeparator() {
        return ",";
    }
}
