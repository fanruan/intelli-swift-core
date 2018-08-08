package com.fr.swift.service.register;

import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.service.LocalSwiftServerService;

/**
 * This class created on 2018/6/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class LocalSwiftRegister extends AbstractSwiftRegister {

    @Override
    public void serviceRegister() throws SwiftServiceException {
        new LocalSwiftServerService().start();
        localServiceRegister();
    }

    @Override
    public void serviceUnregister() {
    }
}
