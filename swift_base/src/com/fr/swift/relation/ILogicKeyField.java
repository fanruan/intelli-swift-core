package com.fr.swift.relation;

//import com.fr.bi.stable.data.BIFieldID;
import com.fr.json.JSONTransform;

import java.io.Serializable;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/16
 */
public interface ILogicKeyField<T, F> extends JSONTransform, Serializable {
    T belongTo();

    List<F> getKeyFields();

    String getFieldName();

    //BIFieldID getFieldID();
}
