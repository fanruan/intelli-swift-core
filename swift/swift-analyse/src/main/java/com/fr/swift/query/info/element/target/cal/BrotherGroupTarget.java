package com.fr.swift.query.info.element.target.cal;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.info.bean.type.cal.CalTargetType;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BrotherGroupTarget that = (BrotherGroupTarget) o;

        return brotherGroupIndex != null ? brotherGroupIndex.equals(that.brotherGroupIndex) : that.brotherGroupIndex == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (brotherGroupIndex != null ? brotherGroupIndex.hashCode() : 0);
        return result;
    }
}
