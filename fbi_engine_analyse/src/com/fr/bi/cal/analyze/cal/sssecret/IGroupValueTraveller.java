package com.fr.bi.cal.analyze.cal.sssecret;


import com.fr.bi.cal.analyze.cal.result.NewRootNodeChild;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Connery on 2014/12/12.
 */
public interface IGroupValueTraveller {
    Iterator getIterator();

    NewRootNodeChild getCurrentNodeChild(GroupValueIndex parentIndex, Map.Entry entry, boolean usePeriodFilter);

    boolean isNodeChildVisiable(GroupValueIndex parentIndex, NewRootNodeChild childNode);

    void addNodeChild(NewRootNodeChild childNode);


}