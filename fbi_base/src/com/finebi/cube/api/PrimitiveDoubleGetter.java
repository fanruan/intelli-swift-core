package com.finebi.cube.api;

import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by 小灰灰 on 2016/7/20.
 */
public interface PrimitiveDoubleGetter extends PrimitiveDetailGetter {

    double getValue(int row);

    GroupValueIndex getNullIndex();
}
