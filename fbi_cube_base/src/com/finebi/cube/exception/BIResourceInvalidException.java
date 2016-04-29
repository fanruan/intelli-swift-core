package com.finebi.cube.exception;

/**
 * 构建读接口异常，可能原因是可读的文件或者对象不存在
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIResourceInvalidException extends Exception {
    public BIResourceInvalidException(String message) {
        super(message);
    }

    public BIResourceInvalidException() {
    }
}
