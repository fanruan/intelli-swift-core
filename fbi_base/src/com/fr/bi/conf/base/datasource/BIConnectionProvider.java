package com.fr.bi.conf.base.datasource;

import com.fr.data.impl.Connection;
import com.fr.data.impl.JDBCDatabaseConnection;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by wang on 2016/12/5.
 */
public interface BIConnectionProvider {
    String XML_TAG = "BIConnection";
    void updateAvailableConnection();

    String getSchema(String name);

    Connection getConnection(String name);

    void envChanged();

    void updateConnection(String linkData, String oldName) throws Exception;

    void removeConnection(String name);

    JSONObject createJSON() throws JSONException;

    boolean isMicrosoftAccessDatabase(JDBCDatabaseConnection c);

    boolean needSchema(Connection c);
}
