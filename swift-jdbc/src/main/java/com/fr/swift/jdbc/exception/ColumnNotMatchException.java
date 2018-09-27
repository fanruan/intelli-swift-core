package com.fr.swift.jdbc.exception;

/**
 * @author yee
 * @date 2018/8/27
 */
public class ColumnNotMatchException extends RuntimeException {
    public ColumnNotMatchException(int insert, int got) {
        super(String.format("insert column count is %d but got %d", insert, got));
    }
}
