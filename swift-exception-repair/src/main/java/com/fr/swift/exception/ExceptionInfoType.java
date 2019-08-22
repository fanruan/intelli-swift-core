package com.fr.swift.exception;

import com.fr.swift.exception.ExceptionInfo.Type;
import com.fr.swift.util.Strings;

/**
 * @author anchore
 * @date 2019/8/16
 */
public enum ExceptionInfoType implements Type {
    //
    UPLOAD_SEGMENT(0),
    DOWNLOAD_SEGMENT(1),
    CORRUPTED_SEGMENT(2);

    ExceptionInfoType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    ExceptionInfoType(int code) {
        this(code, Strings.EMPTY);
    }

    private final int code;
    private final String desc;

    @Override
    public int getExceptionCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return desc;
    }
}