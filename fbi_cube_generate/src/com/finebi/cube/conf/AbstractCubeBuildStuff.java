package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;

/**
 * Created by wuk on 16/7/11.
 */
public abstract class AbstractCubeBuildStuff implements CubeBuildStuff {
    @Override
    public ICubeConfiguration getCubeConfiguration() {
        return null;
    }
}
