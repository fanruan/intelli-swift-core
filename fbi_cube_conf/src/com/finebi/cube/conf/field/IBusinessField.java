package com.finebi.cube.conf.field;

import com.finebi.cube.conf.table.IBusinessTable;
import com.fr.json.JSONTransform;

/**
 * This class created on 2016/5/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface IBusinessField extends JSONTransform {
    String getFieldName();

    int getFieldType();

    int getFieldSize();

    boolean isUsable();

    boolean isCanSetUsable();

    IBusinessTable getTableBelongTo();
}
