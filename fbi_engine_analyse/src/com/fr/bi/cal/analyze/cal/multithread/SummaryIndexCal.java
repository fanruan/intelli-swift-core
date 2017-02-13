package com.fr.bi.cal.analyze.cal.multithread;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.cal.analyze.cal.index.loader.TargetAndKey;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by 小灰灰 on 2017/1/5.
 */
public class SummaryIndexCal extends SummaryCall {
    public SummaryIndexCal(ICubeTableService ti, Node node, TargetAndKey targetAndKey, GroupValueIndex gvi, ICubeDataLoader loader) {
        super(ti, node, targetAndKey, gvi, loader);
    }

    @Override
    public void cal() {
        super.cal();
        node.setTargetIndex(targetAndKey.getTargetGettingKey(), gvi);
    }
}
