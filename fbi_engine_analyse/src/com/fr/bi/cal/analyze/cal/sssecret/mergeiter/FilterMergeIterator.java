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
    private int n = 0;
    private int count = 0;
    public FilterMergeIterator(Iterator<Map.Entry<Object, GroupValueIndex>>[] iterators, Comparator c, StringOneValueFilterValue filterValue, int n) {
        super(iterators, c);
        this.filterValue = filterValue;
        this.n = n;
    }

    @Override
    protected void moveNext() {
        if (count >= n) {
            next = null;
            return;
        }
        super.moveNext();
        if (next != null && !filterValue.showNode(next.getKey())){
            moveNext();
        }
        count++;
    }
}
