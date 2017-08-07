package com.fr.bi.cal.analyze.cal.index.loader.cache;

import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;

/**
 * Created by 小灰灰 on 2017/8/1.
 */
public class WidgetCache<T> {
    private T widgetData;
    private NodeDimensionIterator rowIterator;
    private NodeDimensionIterator columnIterator;
    private int[] pageSpinner;

    public WidgetCache(T widgetData, NodeDimensionIterator rowIterator, NodeDimensionIterator columnIterator, int[] pageSpinner) {
        this.widgetData = widgetData;
        this.rowIterator = rowIterator;
        this.columnIterator = columnIterator;
        this.pageSpinner = pageSpinner;
    }

    public T getData() {
        return widgetData;
    }

    public NodeDimensionIterator getRowIterator() {
        return rowIterator;
    }

    public NodeDimensionIterator getColumnIterator() {
        return columnIterator;
    }

    public int[] getPageSpinner(){
        return pageSpinner;
    }
}
