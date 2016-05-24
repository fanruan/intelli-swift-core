package com.fr.bi.stable.data.db;

import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.json.JSONTransform;

import java.io.Serializable;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeFieldSource extends Cloneable, Serializable, JSONTransform {
    String getFieldName();

    int getFieldType();

    void setTableBelongTo(ICubeTableSource tableBelongTo);

    int getClassType();

    boolean isUsable();

    int getFieldSize();

    ICubeTableSource getTableBelongTo();
}
