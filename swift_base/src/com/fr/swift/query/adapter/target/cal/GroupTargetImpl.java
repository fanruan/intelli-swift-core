package com.fr.swift.query.adapter.target.cal;

import com.fr.swift.query.adapter.AbstractQueryColumn;
import com.fr.swift.query.adapter.target.GroupTarget;

/**
 * Created by Lyon on 2018/4/19.
 */
public class GroupTargetImpl extends AbstractQueryColumn implements GroupTarget {

    private int resultIndex;
    private int[] paramIndexes;
    private CalTargetType type;

    public GroupTargetImpl(int queryIndex, int resultIndex, int[] paramIndexes, CalTargetType type) {
        super(queryIndex);
        this.resultIndex = resultIndex;
        this.paramIndexes = paramIndexes;
        this.type = type;
    }

    @Override
    public int[] paramIndexes() {
        return paramIndexes;
    }

    @Override
    public int resultIndex() {
        return resultIndex;
    }

    @Override
    public CalTargetType type() {
        return type;
    }
}
