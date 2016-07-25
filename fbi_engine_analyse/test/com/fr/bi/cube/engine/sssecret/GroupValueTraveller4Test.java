package com.fr.bi.cube.engine.sssecret;

import com.fr.bi.cal.analyze.cal.result.NewRootNodeChild;
import com.fr.bi.cal.analyze.cal.sssecret.IGroupValueTraveller;
import com.fr.bi.cube.engine.index.GroupValueIndex;
import com.fr.bi.cube.engine.result.NewRootNodeChild;
import com.fr.bi.cube.engine.store.ColumnNameKey;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Connery on 2014/12/14.
 */
public class GroupValueTraveller4Test implements IGroupValueTraveller {
    public static final int NODE_AMOUNT = 2000;
    private ArrayList<Integer> content;

    public GroupValueTraveller4Test() {
        content = new ArrayList<Integer>(NODE_AMOUNT);
        for (int i = 0; i < NODE_AMOUNT; i++) {
            content.add(i);
        }
    }

    @Override
    public Iterator getIterator() {

        return content.iterator();
    }

    @Override
    public NewRootNodeChild getCurrentNodeChild(GroupValueIndex parentIndex, Map.Entry entry, boolean usePeriodFilter) {
        return new NewRootNodeChild(new ColumnNameKey(""), new byte[2]);
    }

    @Override
    public boolean isNodeChildVisiable(GroupValueIndex parentIndex, NewRootNodeChild childNode) {
        return true;
    }

    @Override
    public void addNodeChild(NewRootNodeChild childNode) {

    }
}