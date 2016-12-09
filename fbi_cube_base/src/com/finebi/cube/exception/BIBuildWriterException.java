package com.finebi.cube.exception;

/**
 * 构建写接口异常，可能原因是可写的文件或者对象不存在
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public class BIBuildWriterException extends Exception {
    private static final long serialVersionUID = -3171740014604547612L;

    public BIBuildWriterException(String message) {
        super(message);
    }

    public BIBuildWriterException() {
    }
}
