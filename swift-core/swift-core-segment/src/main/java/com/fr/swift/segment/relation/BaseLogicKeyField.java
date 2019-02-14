package com.fr.swift.segment.relation;

import com.fr.swift.util.Crasher;
import com.fr.swift.util.Util;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/16
 */
public abstract class BaseLogicKeyField<T, F> implements ILogicKeyField<T, F> {

    protected List<F> keyFields;

    public BaseLogicKeyField(List<F> keyFields) {
        Util.requireNonNull(keyFields);
        if (keyFields.isEmpty()) {
            Crasher.crash("fields cannot be empty");
        }
        this.keyFields = keyFields;
    }

    @Override
    public List<F> getKeyFields() {
        return keyFields;
    }

}
