package com.fr.bi.cal.analyze.cal.sssecret.mergeiter;

import com.fr.bi.field.filtervalue.string.onevaluefilter.StringOneValueFilterValue;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/1/9.
 */
public class FilterMergeIterator extends MergeIterator {
    private StringOneValueFilterValue filterValue;
    public FilterMergeIterator(Iterator<Map.Entry<Object, GroupValueIndex>>[] iterators, Comparator c, StringOneValueFilterValue filterValue) {
        super(iterators, c);
        this.filterValue = filterValue;
    }

    @Override
    protected void moveNext() {
        super.moveNext();
        if (next != null && !filterValue.showNode(next.getKey())){
            moveNext();
        }
    }
}
