package com.finebi.cube.exception;

/**
 * 资源路径存在异常
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class IllegalCubeResourceLocationException extends Exception {
    public IllegalCubeResourceLocationException(String message) {
        super(message);
    }

    public IllegalCubeResourceLocationException() {
    }
}
