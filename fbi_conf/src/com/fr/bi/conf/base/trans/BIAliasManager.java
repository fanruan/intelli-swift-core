package com.fr.bi.conf.base.trans;

import com.fr.bi.conf.provider.BIAliasManagerProvider;
import com.fr.bi.stable.utils.program.BIConstructorUtils;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by GUY on 2015/3/31.
 */
public class BIAliasManager implements BIAliasManagerProvider {
    private Map<Long, SingleUserBITransManager> userMap = new ConcurrentHashMap<Long, SingleUserBITransManager>();

    @Override
    public SingleUserBITransManager getTransManager(long userId) {
        return  BIConstructorUtils.constructObject(userId, SingleUserBITransManager.class, userMap);
    }

    @Override
    public void setAliasName(String id, String name, long userId) {
        getTransManager(userId).setTransName(id, name);
    }

    @Override
    public String getAliasName(String id, long userId) {
        return getTransManager(userId).getTransName(id);
    }

    /**
     * 更新
     */
    @Override
    public void envChanged() {
        userMap.clear();
    }
}