package com.fr.bi.stable.engine.cal;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.structure.CubeValueEntryNode;

/**
 * Created by loy on 16/6/28.
 */
public class SortDimensionCalculatorDealer implements NodeResultDealer {
    private BIKey key;

    private boolean asc;

    private NodeResultDealer next;

    public SortDimensionCalculatorDealer(BIKey key, boolean asc){
        this.key = key;
        this.asc = asc;
    }

    public void setNext(NodeResultDealer next){
        this.next = next;
    }

    @Override
    public void dealWithNode(ICubeTableService ti, GroupValueIndex currentIndex, CubeValueEntryNode node) {
        AllSingleDimensionGroup.runWithSort(currentIndex, ti, key, next, node, asc);
    }
}
