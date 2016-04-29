package com.fr.bi.cal.analyze.executor.detail.execute;

import com.fr.bi.stable.gvi.GroupValueIndex;
import com.finebi.cube.api.ICubeColumnIndexReader;

import java.util.Iterator;
import java.util.Map;


/**
 * Created by 小灰灰 on 2015/10/10.
 */
public class DetailSortGviIndex {
    private Object[] value;
    private Object currentvalue;
    private ICubeColumnIndexReader[] getters;

    private transient boolean allSort;

    private boolean[] asc;

    private Iterator<Map.Entry<Object, GroupValueIndex>>[] iters;

    public DetailSortGviIndex(Object[] value, ICubeColumnIndexReader[] getters, boolean[] asc) {
        this.value = value;
        if (getters == null){
            getters = new ICubeColumnIndexReader[0];
        }
        this.getters = getters;
        this.asc = asc;
        iters = new Iterator[getters.length];
        init();
    }

    public boolean isAllSort() {
        return allSort;
    }

    public Object[] getValue() {
        return value;
    }

    private void init(){
        if (value == null || value.length == 0){
            value = new Object[getters.length];
            for (int i = 0; i < getters.length; i ++){
                iters[i] = asc[i] ? getters[i].iterator() : getters[i].previousIterator();
                value[i] = asc[i] ? getters[i].firstKey() : getters[i].lastKey();
            }
        } else {
            for (int i = 0; i < getters.length; i ++){
                iters[i] = asc[i] ? getters[i].iterator(value[i]) : getters[i].previousIterator(value[i]);
            }
        }
    }

    public GroupValueIndex createSortedGVI(GroupValueIndex filterGvi, int leftCount){
        if (filterGvi.getRowsCountWithData() == 0) {
            return null;
        }
        if (getters.length == 0) {
            return filterGvi;
        }
        if (getters[0] == null) {
            return filterGvi;
        }
        allSort = true;
        for (int i = 0; i < iters.length; i++) {
            //少于剩下的行数就直接返回，迭代器当前列移位
            if (filterGvi.getRowsCountWithData() <= leftCount) {
                allSort = false;
                resetValueAndIter(i - 1);
                return filterGvi;
            }
            Map.Entry<Object, GroupValueIndex> entry;
            if (iters[i].hasNext()) {
                entry = iters[i].next();
                currentvalue = entry.getKey();
                filterGvi = filterGvi.AND(entry.getValue());
            } else {
                return null;
            }
        }
        //如果每一个值都and了，小于剩下的行数，则最后一个迭代器往移位，大于剩下的行数，迭代器不移位，因为已经取到了所需的行数，迭代器也不需要重置了
        if(filterGvi.getRowsCountWithData() <= leftCount){
            resetValueAndIter(value.length - 1);
        }
        return filterGvi;
    }

    private void resetValueAndIter(int index){
        for (int i = index; i < value.length; i ++){
            if (i < index){
                iters[i] = asc[i] ? getters[i].iterator(value[i]) : getters[i].previousIterator(value[i]);
            } else if (i == index){
                value[i] = currentvalue;
                moveNext(i);
            } else if (i > index) {
                value[i] = asc[i] ? getters[i].firstKey() : getters[i].lastKey();
                iters[i] = asc[i] ? getters[i].iterator() : getters[i].previousIterator();
            }
        }
    }

    private void moveNext(int index){
        if (index >= 0){
            if (!iters[index].hasNext()){
                value[index] = asc[index] ? getters[index].firstKey() : getters[index].lastKey();
                iters[index] = asc[index] ? getters[index].iterator() : getters[index].previousIterator();
                moveNext(index - 1);
            } else {
                Map.Entry<Object, GroupValueIndex> entry = iters[index].next();
                value[index] = entry.getKey();
            }
        }
    }

}