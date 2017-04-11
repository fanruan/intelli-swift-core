package com.fr.bi.cal.analyze.cal.sssecret.mergeiter;

import com.fr.bi.field.filtervalue.string.onevaluefilter.StringOneValueFilterValue;
import com.fr.bi.stable.engine.cal.DimensionIterator;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;

/**
 * Created by 小灰灰 on 2017/1/9.
 */
public class FilterMergeIterator extends MergeIterator {
    private StringOneValueFilterValue filterValue;

    public FilterMergeIterator(DimensionIterator[] iterators, GroupValueIndex[] gvis, Comparator c, int sumLength, StringOneValueFilterValue filterValue) {
        super(iterators, gvis, c, sumLength);
        this.filterValue = filterValue;
    }

    @Override
    protected void moveNext() {
        super.moveNext();
        if (next != null && !show(next.getData())) {
            moveNext();
        }
    }

    private boolean show(Object key) {
        if (filterValue == null) {
            return true;
        }
        return filterValue.showNode(key);
    }
}
