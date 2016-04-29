package com.fr.bi.stable.engine.index;


import com.fr.bi.base.BIUser;

/**
 * Created by 小灰灰 on 2015/12/16.
 */
public abstract class AbstractTIPathLoader implements CubeTIPathLoader {
    protected BIUser user;

    public AbstractTIPathLoader(long userId) {

        user = new BIUser(userId);
    }

    @Override
    public void releaseCurrentThread() {

    }
}