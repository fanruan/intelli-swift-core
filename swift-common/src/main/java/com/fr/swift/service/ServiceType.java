package com.fr.swift.service;

/**
 * Created by pony on 2017/11/8.
 */
public enum ServiceType {
    REAL_TIME((byte) 0), SERVER((byte) 1), ANALYSE((byte) 2), INDEXING((byte) 3), HISTORY((byte) 4), COLLATE((byte) 5);
    private byte type;

    ServiceType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    /**
     * 没有匹配到服务就返回null
     *
     * @param name
     * @return
     */
    public static ServiceType getServiceType(String name) {
        try {
            return ServiceType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
