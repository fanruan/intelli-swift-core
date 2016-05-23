package com.finebi.cube.conf;


import com.finebi.cube.conf.trans.UserAliasManager;
import com.fr.json.JSONObject;

/**
 * Created by GUY on 2015/3/31.
 */
public interface BIAliasManagerProvider {

    String XML_TAG = "BITransManager";

    UserAliasManager getTransManager(long userId);

    void setAliasName(String id, String name, long userId);

    String getAliasName(String id, long userId);

    JSONObject getAliasJSON(long userID);

    void envChanged();
}