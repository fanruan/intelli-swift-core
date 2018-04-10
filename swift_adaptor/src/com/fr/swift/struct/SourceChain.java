package com.fr.swift.struct;

import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * This class created on 2018/4/9
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SourceChain {
    private DataSource current;

    private List<SourceChain> previous;

    private List<SourceChain> next;

    public DataSource getCurrent() {
        return current;
    }

    public List<SourceChain> next() {
        return next;
    }

    public boolean hasNext() {
        return next != null && !next.isEmpty();
    }
}
