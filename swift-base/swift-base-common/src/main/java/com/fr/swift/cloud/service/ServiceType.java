package com.fr.swift.cloud.service;

/**
 * @author pony
 * @date 2017/11/8
 */
public enum ServiceType {
    //
    REAL_TIME((byte) 0), ANALYSE((byte) 1), HISTORY((byte) 2), COLLATE((byte) 3),
    DELETE((byte) 4), CONTEXT((byte) 5), MIGRATE((byte) 6), DISTRIBUTE((byte) 7);

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
