package com.fr.swift.service;

import com.fr.swift.exception.SwiftServiceException;

/**
 * This class created on 2018/6/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface SwiftRegister {
    void serviceRegister() throws SwiftServiceException;

    void serviceUnregister() throws SwiftServiceException;
}
