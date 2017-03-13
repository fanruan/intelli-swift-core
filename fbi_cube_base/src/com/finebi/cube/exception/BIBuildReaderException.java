package com.finebi.cube.exception;

/**
 * 构建读接口异常，可能原因是可读的文件或者对象不存在
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIBuildReaderException extends Exception {
    private static final long serialVersionUID = 7335176294029810372L;

    public BIBuildReaderException(String message) {
        super(message);
    }

    public BIBuildReaderException() {
    }
}
