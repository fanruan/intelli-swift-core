package com.finebi.cube.fun.impl;

import com.finebi.cube.fun.CubeConditionProvider;
import com.fr.stable.fun.mark.API;

/**
 * Created by richie on 2016/10/8.
 */
@API(level = CubeConditionProvider.CURRENT_LEVEL)
public abstract class AbstractCubeConditionProvider implements CubeConditionProvider {

    @Override
    public int currentAPILevel() {
        return CURRENT_LEVEL;
    }

    @Override
    public String mark4Provider() {
        return getClass().getName();
    }
}
