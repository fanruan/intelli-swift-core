package com.fr.swift.query.adapter.target.cal;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.structure.Pair;

import java.util.List;

/**
 * Created by pony on 2018/5/16.
 */
public class BrotherGroupTarget extends GroupTargetImpl {
    private List<Pair<Integer, GroupType>> brotherGroupIndex;
    public BrotherGroupTarget(int queryIndex, int resultIndex, int[] paramIndexes, CalTargetType type, List<Pair<Integer, GroupType>> brotherGroupIndex) {
        super(queryIndex, resultIndex, paramIndexes, type);
        this.brotherGroupIndex = brotherGroupIndex;
    }

    public List<Pair<Integer, GroupType>> getBrotherGroupIndex() {
        return brotherGroupIndex;
    }
}
