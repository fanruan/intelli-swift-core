package com.fr.bi.stable.data.key;

import com.fr.json.JSONTransform;

/**
 * Created by GUY on 2015/3/10.
 */
public interface IPersistentField extends JSONTransform {
    int getSqlType();

    String getFieldName();

    boolean isUsable();

}