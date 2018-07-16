package com.fr.swift.task.service;

/**
 * This class created on 2018/7/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public enum ServiceTaskType {
    INSERT((byte) 0), DELETE((byte) 1), COLLATE((byte) 2), RECOVERY((byte) 3), QUERY((byte) 4);

    private byte type;

    ServiceTaskType(byte type) {
        this.type = type;
    }

    public ServiceTaskType getTaskType() {
        return ServiceTaskType.values()[type];
    }

    public byte getType() {
        return type;
    }

    public boolean isEdit() {
        return getTaskType() != QUERY;
    }
}
