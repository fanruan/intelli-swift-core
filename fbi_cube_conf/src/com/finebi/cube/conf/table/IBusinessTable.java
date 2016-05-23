package com.finebi.cube.conf.table;

import com.fr.bi.stable.data.BITableID;
import com.fr.json.JSONTransform;

/**
 * This class created on 2016/5/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface IBusinessTable extends JSONTransform {
    BITableID getID();

    Object clone() throws CloneNotSupportedException;

    String getTableName();
}
