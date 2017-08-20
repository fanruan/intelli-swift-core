package com.fr.bi.property;

/**
 * This class created on 2017/8/20.
 *
 * @author Each.Zhang
 */
public enum AvaliableType {
    TRUE("是"),FASLE("否");

    private String value;

    private AvaliableType (String value) {
        this.value = value;
    }

    @Override
    public String toString(){
        return value;
    }
}
