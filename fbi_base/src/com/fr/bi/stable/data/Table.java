package com.fr.bi.stable.data;

import com.fr.json.JSONObject;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public interface Table extends Cloneable {
    BITableID getID();

    void setID(BITableID id);

    String getTableName();

    void setTableName(String tableName);

    JSONObject createJSON() throws Exception;

    void parseJSON(JSONObject jo) throws Exception;

    Object clone() throws CloneNotSupportedException;
}
