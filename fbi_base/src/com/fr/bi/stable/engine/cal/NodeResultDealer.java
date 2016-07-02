package com.fr.bi.stable.engine.cal;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.structure.CubeValueEntryNode;

/**
 * Created by loy on 16/6/28.
 */
public interface NodeResultDealer {
    void dealWithNode(ICubeTableService ti, GroupValueIndex currentIndex, CubeValueEntryNode node);
}
