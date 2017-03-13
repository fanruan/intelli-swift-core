package com.fr.bi.cal.analyze.cal.sssecret;


<<<<<<< HEAD
public class PageIteratorGroup {

    private NodeDimensionIterator rowIterator;
=======
import com.fr.bi.cal.analyze.cal.store.GroupKey;
import com.fr.bi.stable.report.key.TargetGettingKey;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PageIteratorGroup implements Serializable {

    private static final long serialVersionUID = -5143454742110936232L;
    private Map<GroupKey, IRootDimensionGroup> rowMap = new ConcurrentHashMap<GroupKey, IRootDimensionGroup>();
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092

    private NodeDimensionIterator columnIterator;

    public PageIteratorGroup(){

    }

    public NodeDimensionIterator getRowIterator() {
        return rowIterator;
    }

    public NodeDimensionIterator getColumnIterator() {
        return columnIterator;
    }

    public void setRowIterator(NodeDimensionIterator rowIterator) {
        this.rowIterator = rowIterator;
    }

    public void setColumnIterator(NodeDimensionIterator columnIterator) {
        this.columnIterator = columnIterator;
    }
}