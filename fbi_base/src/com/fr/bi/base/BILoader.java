package com.fr.bi.base;

import com.finebi.cube.api.ICubeDataLoader;

/**
 * Created by GUY on 2015/4/28.
 */
public class BILoader {
    protected ICubeDataLoader loader;
    protected BIUser biUser;
    public BILoader(ICubeDataLoader loader, long userId) {
        biUser = new BIUser(userId);
        this.loader = loader;
    }

    public ICubeDataLoader getLoader() {
        return loader;
    }

    public void setLoader(ICubeDataLoader loader) {
        this.loader = loader;
    }
}