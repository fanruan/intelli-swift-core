package com.fr.swift.task.service;

/**
 * This class created on 2018/7/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public enum ServiceTaskType {
    //
    INSERT,
    DELETE,
    COLLATE,
    RECOVERY,
    PERSIST,
    UPLOAD,
    DOWNLOAD,
    CLEAR_LOCAL,
    QUERY;

    public boolean isEdit() {
        return this != QUERY;
    }
}
