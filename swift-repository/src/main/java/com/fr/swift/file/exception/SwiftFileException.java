package com.fr.swift.file.exception;

import java.io.IOException;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftFileException extends IOException {
    public SwiftFileException() {
    }

    public SwiftFileException(String message) {
        super(message);
    }

    public SwiftFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public SwiftFileException(Throwable cause) {
        super(cause);
    }
}
