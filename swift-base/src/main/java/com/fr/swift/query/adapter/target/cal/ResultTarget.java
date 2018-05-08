package com.fr.swift.query.adapter.target.cal;

import com.fr.swift.query.adapter.AbstractQueryColumn;

/**
 * Created by Lyon on 2018/4/9.
 */
public class ResultTarget extends AbstractQueryColumn {

    private int resultFetchIndex;

    public ResultTarget(int queryColumnIndex, int resultFetchIndex) {
        super(queryColumnIndex);
        this.resultFetchIndex = resultFetchIndex;
    }

    public int getResultFetchIndex() {
        return resultFetchIndex;
    }
}
