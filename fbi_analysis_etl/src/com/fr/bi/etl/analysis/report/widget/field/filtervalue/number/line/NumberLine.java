package com.fr.bi.etl.analysis.report.widget.field.filtervalue.number.line;

import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by 小灰灰 on 2016/5/20.
 */
public class NumberLine implements CalLineGetter {
    public NumberLine(Double value) {
        this.value = value;
    }

    private Double value;
    @Override
    public Double getCalLine(ICubeTableService ti, BIKey key, GroupValueIndex gvi) {
        return value;
    }
}
