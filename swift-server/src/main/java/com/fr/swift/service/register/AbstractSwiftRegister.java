package com.fr.swift.service.register;

import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.SwiftRegister;
import com.fr.third.springframework.beans.factory.annotation.Autowired;

/**
 * This class created on 2018/6/1
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public abstract class AbstractSwiftRegister implements SwiftRegister {

    @Autowired
    protected SwiftProperty swiftProperty;
}
