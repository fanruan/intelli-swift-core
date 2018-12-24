package com.fr.swift.source.resultset.impl;

/**
 * @author yee
 * @date 2018-12-20
 */
public class TabLineParser extends BaseSeparatorLineParser {
    public TabLineParser(boolean skipFirstLine) {
        super(skipFirstLine);
    }

    public TabLineParser(boolean skipFirstLine, LineParserAdaptor adaptor) {
        super(skipFirstLine, adaptor);
    }

    @Override
    protected String getSeparator() {
        return "\t";
    }
}
