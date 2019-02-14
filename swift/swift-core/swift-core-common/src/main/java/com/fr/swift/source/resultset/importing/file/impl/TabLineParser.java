package com.fr.swift.source.resultset.importing.file.impl;

/**
 * @author yee
 * @date 2018-12-24
 */
public class TabLineParser extends BaseSeparatorLineParser {
    public TabLineParser(boolean skipFirstLine, LineParserAdaptor adaptor) {
        super(skipFirstLine, adaptor);
    }

    public TabLineParser(boolean skipFirstLine) {
        super(skipFirstLine);
    }

    @Override
    protected String getSeparator() {
        return "\t";
    }
}
