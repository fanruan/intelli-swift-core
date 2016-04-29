package com.fr.bi.module;

/**
 * Created by 小灰灰 on 2015/12/15.
 */
public abstract class AbstractModule implements BIModule {
    @Override
    public String getModuleName() {
        return getClass().getName();
    }

    @Override
    public boolean isAllAdmin() {
        return true;
    }
}