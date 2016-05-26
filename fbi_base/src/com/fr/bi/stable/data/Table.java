package com.fr.bi.stable.data;

import com.fr.json.JSONTransform;

/**
 * This class created on 2016/3/9.
 *
 * @author Connery
 * @since 4.0
 */
public interface Table extends Cloneable, JSONTransform {
    BITableID getID();

    void setID(BITableID id);

    String getTableName();

    void setTableName(String tableName);


    Object clone() throws CloneNotSupportedException;
}
