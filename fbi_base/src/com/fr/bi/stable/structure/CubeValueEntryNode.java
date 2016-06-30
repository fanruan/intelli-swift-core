package com.fr.bi.stable.structure;

import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.structure.object.CubeValueEntry;


/**
 * Created by loy on 16/6/28.
 */
public class CubeValueEntryNode<T> extends CubeValueEntry<T> {

    private CubeValueEntryNode[] childList;

    public CubeValueEntryNode(){
        super(null, null, 0);
    }

    public CubeValueEntryNode(T o, GroupValueIndex gvi, int index) {
        super(o, gvi, index);
    }

    public static CubeValueEntryNode fromParent(CubeValueEntry entry){
        return new CubeValueEntryNode(entry.getT(), entry.getGvi(), entry.getIndex());
    }

    public CubeValueEntryNode[] getChildList() {
        return childList;
    }

    public void setChildList(CubeValueEntryNode[] childList) {
        this.childList = childList;
    }
}
