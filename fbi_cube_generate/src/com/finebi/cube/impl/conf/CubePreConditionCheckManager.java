package com.finebi.cube.impl.conf;

import com.finebi.cube.conf.CubePreConditionCheck;

/**
 * Created by kary on 2016/6/20.
 */
public class CubePreConditionCheckManager implements CubePreConditionCheck {
    @Override
    public boolean HDSpaceCheck() {
        return true;
    }

    @Override
    public boolean ConnectionCheck() {
        return true;
    }
}
