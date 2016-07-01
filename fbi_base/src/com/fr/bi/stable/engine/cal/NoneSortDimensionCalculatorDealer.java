package com.fr.bi.stable.engine.cal;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.structure.CubeValueEntryNode;

/**
 * Created by loy on 16/6/30.
 */
public class NoneSortDimensionCalculatorDealer implements NodeResultDealer {
    private BIKey key;

    private NodeResultDealer next;

    public NoneSortDimensionCalculatorDealer(BIKey key){
        this.key = key;
    }

    public void setNext(NodeResultDealer next){
        this.next = next;
    }

    @Override
    public void dealWithNode(ICubeTableService ti, GroupValueIndex currentIndex, CubeValueEntryNode node) {
        AllSingleDimensionGroup.run(currentIndex, ti, key, next, node);
    }
}
