package com.finebi.cube.conf.field;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.json.JSONTransform;

/**
 * This class created on 2016/5/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface BusinessField extends JSONTransform, Cloneable {
    String getFieldName();

    int getFieldType();

    int getFieldSize();

    BIFieldID getFieldID();

    int getClassType();

    boolean isUsable();

    boolean isCanSetUsable();

    BusinessTable getTableBelongTo();

    Object clone() throws CloneNotSupportedException;

}
