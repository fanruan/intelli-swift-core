package com.finebi.cube.conf;


import com.finebi.cube.conf.trans.UserAliasManager;
import com.fr.json.JSONObject;
/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface BIAliasManagerProvider {

    String XML_TAG = "BITransManager";

    UserAliasManager getTransManager(long userId);

    void setAliasName(String id, String name, long userId);

    String getAliasName(String id, long userId);

    JSONObject getAliasJSON(long userID);

    void envChanged();
}