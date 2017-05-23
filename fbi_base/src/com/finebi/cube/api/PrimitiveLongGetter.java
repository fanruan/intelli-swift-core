package com.finebi.cube.api;

import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by 小灰灰 on 2016/7/20.
 */
public interface PrimitiveLongGetter extends PrimitiveDetailGetter{

    long getValue(int row);

    GroupValueIndex getNullIndex();

}
