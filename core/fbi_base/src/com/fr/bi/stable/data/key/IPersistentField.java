package com.fr.bi.stable.data.key;

import com.fr.json.JSONTransform;

import java.io.Serializable;

/**
 * Created by GUY on 2015/3/10.
 */
public interface IPersistentField extends JSONTransform,Serializable {
    int getSqlType();

    String getFieldName();

    boolean isUsable();

}