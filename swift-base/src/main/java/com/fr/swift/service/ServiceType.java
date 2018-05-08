package com.fr.swift.service;

/**
 * Created by pony on 2017/11/8.
 */
public enum ServiceType {
    REAL_TIME((byte)0), SERVER((byte)1), ANALYSE((byte)2), INDEXING((byte)3), HISTORY((byte)4);
    private byte type;

    ServiceType (byte type){
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
