package com.fr.swift.provider;

/**
 * This class created on 2018/5/16
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public enum IndexStuffType {
    TABLE,
    PACKAGE,
    GLOABLE;

    private Object data;

    public IndexStuffType setData(Object data) {
        this.data = data;
        return this;
    }

    public Object getData() {
        return data;
    }
}
