package com.fr.swift.service;

/**
 * @author pony
 * @date 2017/11/8
 */
public enum ServiceType {
    //
    REAL_TIME((byte) 0, "realtime"), SERVER((byte) 1, "server"), ANALYSE((byte) 2, "analyse"), INDEXING((byte) 3, "indexing"), HISTORY((byte) 4, "history"), COLLATE((byte) 5, "collate"),
    DELETE((byte) 6, "delete"), UPLOAD((byte) 7, "upload"), EXCEPTION((byte) 8, "exception"), HEALTH_INSPECT((byte) 9, "healthinspect");

    private byte type;

    private String serviceName;

    ServiceType(byte type, String serviceName) {
        this.type = type;
        this.serviceName = serviceName;
    }

    public byte getType() {
        return type;
    }

    public String getName() {
        return serviceName;
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
