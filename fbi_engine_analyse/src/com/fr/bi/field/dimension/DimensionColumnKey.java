package com.fr.bi.field.dimension;

import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.operation.group.IGroup;
import com.fr.bi.stable.operation.sort.ISort;
import com.fr.general.ComparatorUtils;

import java.util.List;

/**
 * Created by 小灰灰 on 2015/6/24.
 */
public class DimensionColumnKey extends BIField {
    private List<BITableRelation> relations;
    private IGroup group;
    private ISort sort;
    private BIField field;

    public DimensionColumnKey(BIField define, List<BITableRelation> relations, IGroup group, ISort sort) {
        super(define);
        this.relations = relations;
        this.group = group;
        this.sort = sort;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DimensionColumnKey)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        DimensionColumnKey that = (DimensionColumnKey) o;

        if (relations != null ? !ComparatorUtils.equals(relations, that.relations) : that.relations != null) {
            return false;
        }
        if (group != null ? !ComparatorUtils.equals(group, that.group) : that.group != null) {
            return false;
        }
        return !(sort != null ? !ComparatorUtils.equals(sort, that.sort) : that.sort != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (relations != null ? relations.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (sort != null ? sort.hashCode() : 0);
        return result;
    }
}