package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;

/**
 * Created by wuk on 16/7/11.
 */
public abstract class AbstractCubeBuild implements CubeBuild {
    private long userId;
    public AbstractCubeBuild(long userId) {
        this.userId=userId;
    }

    @Override
    public ICubeConfiguration getCubeConfiguration() {
        return  BICubeConfiguration.getTempConf(Long.toString(userId));
    }
}
