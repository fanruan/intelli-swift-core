package com.fr.swift.query.info.element.target.cal;

import com.fr.swift.query.info.bean.type.cal.CalTargetType;
import com.fr.swift.query.info.element.dimension.AbstractQueryColumn;
import com.fr.swift.query.info.element.target.GroupTarget;

import java.util.Arrays;

/**
 * Created by Lyon on 2018/4/19.
 */
public class GroupTargetImpl extends AbstractQueryColumn implements GroupTarget {

    // resultIndex这个字段不参与equals和hashCode比较
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GroupTargetImpl that = (GroupTargetImpl) o;

        if (!Arrays.equals(paramIndexes, that.paramIndexes)) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(paramIndexes);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
