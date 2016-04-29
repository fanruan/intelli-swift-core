package com.fr.bi.stable.data;

import com.fr.json.JSONTransform;
import com.fr.stable.xml.XMLable;

import java.io.Serializable;

/**
 * Created by Connery on 2016/1/19.
 */
public interface BIFieldOperation extends JSONTransform, Cloneable, Serializable, XMLable {
    String XML_TAG = "BIBusiField";

    void setTableBelongTo(Table tableBelongTo);

    Table getTableBelongTo();

    BITableID getTableID();

    String getFieldName();

    void setFieldName(String fieldName);
}