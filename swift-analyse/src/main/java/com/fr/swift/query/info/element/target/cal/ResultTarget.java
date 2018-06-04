package com.fr.swift.query.info.element.target.cal;

import com.fr.swift.query.info.element.dimension.AbstractQueryColumn;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResultTarget that = (ResultTarget) o;

        return resultFetchIndex == that.resultFetchIndex;
    }

    @Override
    public int hashCode() {
        return resultFetchIndex;
    }
}
