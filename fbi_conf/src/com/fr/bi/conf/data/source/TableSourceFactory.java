package com.fr.bi.conf.data.source;

import com.fr.bi.stable.data.source.AbstractTableSource;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by 小灰灰 on 2015/12/21.
 */
public class TableSourceFactory {
    public static AbstractTableSource createTableSource(JSONObject jo, long userId) throws Exception {
        String connectionName = jo.optString("connection_name", StringUtils.EMPTY);
        AbstractTableSource tableSource;
        if(TableSourceMap.SOURCES.get(connectionName) != null) {
            tableSource = (AbstractTableSource) TableSourceMap.SOURCES.get(connectionName).newInstance();
            tableSource.parseJSON(jo, userId);
        } else {
            tableSource = new DBTableSource();
            tableSource.parseJSON(jo, userId);
        }
        return tableSource;
    }
}