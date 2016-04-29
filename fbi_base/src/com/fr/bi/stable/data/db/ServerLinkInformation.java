package com.fr.bi.stable.data.db;

import com.fr.data.impl.DBTableData;
import com.fr.data.impl.NameDatabaseConnection;


/**
 * Created with IntelliJ IDEA.
 * User: Sheldon
 * Date: 13-10-31
 * Time: 下午3:59
 * To change this template use File | Settings | File Templates.
 */
public class ServerLinkInformation {
    private String dataLinkName;
    private String query;

    public ServerLinkInformation(String dataLinkName, String query) throws Exception {

        this.dataLinkName = dataLinkName;
        this.query = query;
    }

    public DBTableData createDBTableData() {
        DBTableData dbTableData = new DBTableData();

        if (dataLinkName != null) {
            dbTableData.setDatabase(new NameDatabaseConnection(dataLinkName));
        }

        if (query != null) {
            dbTableData.setQuery(query);
        }

        return dbTableData;
    }
}