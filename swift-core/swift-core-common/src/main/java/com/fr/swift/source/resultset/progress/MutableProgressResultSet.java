package com.fr.swift.source.resultset.progress;

import com.fr.swift.result.MutableResultSet;

/**
 * @author lucifer
 * @date 2019/9/19
 * @description
 * @since swift 1.1
 */
public class MutableProgressResultSet extends ProgressResultSet implements MutableResultSet {

    private MutableResultSet mutableResultSet;

    public MutableProgressResultSet(MutableResultSet mutableResultSet, String source) {
        super(mutableResultSet, source);
        this.mutableResultSet = mutableResultSet;
    }

    @Override
    public boolean hasNewSubfields() {
        return mutableResultSet.hasNewSubfields();
    }
}