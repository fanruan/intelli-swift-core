package com.fr.bi.cal.analyze.executor.detail.execute;

import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.structure.collection.CubeIndexGetterWithNullValue;

import java.util.Iterator;
import java.util.Map;


/**
 * Created by 小灰灰 on 2015/10/10.
 */
public class DetailSortGviIndex {
    private Object[] value;
    private GroupValueIndex[] gvis;
    private int currentIndex;
    private int lastIndex;
    private CubeIndexGetterWithNullValue[] getters;
    private GroupValueIndex filterGVI;

    private transient boolean allSort;

    private boolean[] asc;

    private Iterator<Map.Entry<Object, GroupValueIndex>>[] iters;

    public DetailSortGviIndex(Object[] value, CubeIndexGetterWithNullValue[] getters, boolean[] asc, GroupValueIndex filterGVI) {
        this.value = value;
        if (getters == null){
            getters = new CubeIndexGetterWithNullValue[0];
        }
        this.getters = getters;
        this.asc = asc;
        this.filterGVI = filterGVI;
        iters = new Iterator[getters.length];
        gvis = new GroupValueIndex[getters.length + 1];
        gvis[0] = filterGVI;
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
                iters[i] = asc[i] ? getters[i].iterator(gvis[i]) : getters[i].previousIterator(gvis[i]);
                Map.Entry<Object, GroupValueIndex> entry = iters[i].next();
                value[i] = entry.getKey();
                gvis[i + 1] = gvis[i].AND(entry.getValue());
                iters[i] = asc[i] ? getters[i].iterator(gvis[i]) : getters[i].previousIterator(gvis[i]);
            }
        } else {
            for (int i = 0; i < getters.length; i ++){
                iters[i] = asc[i] ? getters[i].iterator(value[i], gvis[i]) : getters[i].previousIterator(value[i], gvis[i]);
                gvis[i + 1] = gvis[i].AND(getters[i].getIndex(value[i]));
            }
        }
    }

    public GroupValueIndex createSortedGVI(GroupValueIndex filterGvi, int leftCount){
        if (filterGvi.getRowsCountWithData() == 0) {
            return null;
        }
        allSort = true;
        if (getters.length == 0) {
            return filterGvi;
        }
        if (getters[0] == null) {
            return filterGvi;
        }
        filterGvi = gvis[lastIndex];
        for (int i = lastIndex; i < iters.length; i++) {
            //少于剩下的行数就直接返回，迭代器当前列移位
            if (filterGvi.getRowsCountWithData() <= leftCount) {
                allSort = false;
                return filterGvi;
            }
            Map.Entry<Object, GroupValueIndex> entry;
            if (iters[i].hasNext()) {
                entry = iters[i].next();
                currentIndex = i;
                value[i] = entry.getKey();
                filterGvi = filterGvi.AND(entry.getValue());
            } else {
                return null;
            }
        }
        currentIndex = value.length - 1;
        return filterGvi;
    }

    public void next(){
        moveNext(currentIndex);
    }

    private void moveNext(int index){
        if (index >= 0 && iters.length > index){
            if (!iters[index].hasNext()){
                moveNext(index - 1);
            } else {
                lastIndex = index + 1;
                Map.Entry<Object, GroupValueIndex> entry = iters[index].next();
                value[index] = entry.getKey();
                gvis[index + 1] = gvis[index].AND(entry.getValue());
                for (int i = index + 1; i< iters.length; i++){
                    iters[i] = asc[i] ? getters[i].iterator(gvis[i]) : getters[i].previousIterator(gvis[i]);
                    Map.Entry<Object, GroupValueIndex> entry1 = iters[i].next();
                    value[i] = entry1.getKey();
                    gvis[i + 1] = gvis[i].AND(entry1.getValue());
                    iters[i] = asc[i] ? getters[i].iterator(gvis[i]) : getters[i].previousIterator(gvis[i]);
                }

            }
        }
    }

}