package com.fr.bi.conf.base.datasource;

import com.fr.data.impl.Connection;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by wang on 2016/12/5.
 */
public interface BIConnectionProvider {
    String XML_TAG = "BIConnection";

    void updateAvailableConnection();

    String getSchema(String name);

    BIConnection getBIConnection(String name);

    Connection getConnection(String name);

    void envChanged();

    void updateConnection(String linkData, String oldName, long userId) throws Exception;

    void removeConnection(String name);

    JSONObject createJSON() throws JSONException;

    boolean isMicrosoftAccessDatabase(String driver, String url);

    boolean idNeedSchema(Connection c);
}
