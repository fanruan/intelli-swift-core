package com.fr.swift.generate;

import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.source.db.TestConnectionProvider;
import junit.framework.TestCase;

public abstract class BaseTest extends TestCase {
    @Override
    public void setUp() throws Exception {
        new LocalSwiftServerService().start();
        TestConnectionProvider.createConnection();
    }
}
