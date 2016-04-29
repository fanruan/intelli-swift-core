package com.fr.bi.conf.provider;


import com.fr.bi.conf.base.trans.SingleUserBITransManager;

/**
 * Created by GUY on 2015/3/31.
 */
public interface BIAliasManagerProvider {

    String XML_TAG = "BITransManager";

    SingleUserBITransManager getTransManager(long userId);

    void setAliasName(String id, String name, long userId);

    String getAliasName(String id, long userId);

    void envChanged();
}