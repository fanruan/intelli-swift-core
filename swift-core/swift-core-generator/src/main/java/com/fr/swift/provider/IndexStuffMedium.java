package com.fr.swift.provider;

/**
 * This class created on 2018/5/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class IndexStuffMedium {

    private IndexStuffType indexStuffType;

    private Object data;

    public IndexStuffMedium(IndexStuffType indexStuffType, Object data) {
        this.indexStuffType = indexStuffType;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public IndexStuffType getIndexStuffType() {
        return indexStuffType;
    }
}
