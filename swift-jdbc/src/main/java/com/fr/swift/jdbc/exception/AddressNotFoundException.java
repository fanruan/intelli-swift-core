package com.fr.swift.jdbc.exception;

/**
 * @author yee
 * @date 2018-12-12
 */
class AddressNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -5133099624355457612L;

    AddressNotFoundException(String addressType) {
        super(String.format("%s address not found", addressType));
    }
}
