package com.fr.bi.stable.engine.cal;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.structure.CubeValueEntryNode;

/**
 * Created by loy on 16/6/28.
 */
public class SortDimensionCalculatorDealer implements NodeResultDealer {
    private DimensionCalculator dc;

    private ICubeTableService ti;

    private boolean asc;

    private NodeResultDealer next;

    public SortDimensionCalculatorDealer(DimensionCalculator dc, boolean asc, ICubeTableService ti){
        this.dc = dc;
        this.asc = asc;
        this.ti = ti;
    }

    public void setNext(NodeResultDealer next){
        this.next = next;
    }

    @Override
    public void dealWithNode(GroupValueIndex currentIndex, CubeValueEntryNode node) {
        AllSingleDimensionGroup.runWithSort(currentIndex, ti, dc, next, node, asc);
    }
}
