package com.fr.swift.api.rpc.exception;

/**
 * @author yee
 * @date 2018/9/6
 */
public class AddressAbsentException extends RuntimeException {
    public AddressAbsentException() {
        super("Target address is null");
    }
}
