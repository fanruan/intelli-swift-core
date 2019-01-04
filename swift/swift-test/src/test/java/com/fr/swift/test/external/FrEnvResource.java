package com.fr.swift.test.external;

import com.fr.swift.test.TestResource;
import com.fr.workspace.simple.SimpleWork;
import org.junit.rules.ExternalResource;

/**
 * @author anchore
 * @date 2018/10/30
 */
public class FrEnvResource extends ExternalResource {

    @Override
    protected void before() {
        SimpleWork.checkIn(TestResource.getTestDir());
    }

    @Override
    protected void after() {
        SimpleWork.checkOut();
    }
}