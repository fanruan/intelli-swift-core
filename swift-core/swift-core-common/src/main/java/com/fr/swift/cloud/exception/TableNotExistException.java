package com.fr.swift.cloud.exception;

import com.fr.swift.cloud.source.SourceKey;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class TableNotExistException extends Exception {

    public TableNotExistException(SourceKey sourceKey) {
        super(sourceKey.getId() + " not exist!");
    }

    public TableNotExistException(SourceKey sourceKey, Throwable cause) {
        super(sourceKey.getId() + " not exist!", cause);
    }
}

