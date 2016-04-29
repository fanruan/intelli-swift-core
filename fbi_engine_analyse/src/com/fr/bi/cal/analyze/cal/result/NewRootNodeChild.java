package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;

/**
 * Created by 小灰灰 on 14-3-4.
 */
public class NewRootNodeChild extends Node {

    /**
     *
     */
    private static final long serialVersionUID = 2195477256564513817L;

    public NewRootNodeChild(DimensionCalculator key, Object data) {
        super(key, data);
    }

    public NewRootNodeChild(DimensionCalculator key, Object data, GroupValueIndex gvi) {
        super(key, data);
        setGroupValueIndex(gvi);
    }
}