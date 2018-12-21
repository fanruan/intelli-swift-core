package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * Created by pony on 2017/11/14.
 */
public class LocalSwiftServerServiceTest {
    private LocalSwiftServerService localSwiftServerService;

    @Rule
    public void prepareContext() throws Exception {
        Class.forName("com.fr.swift.test.external.ContextResource").newInstance();
    }

    @Before
    public void setUp() throws Exception {
        localSwiftServerService = new LocalSwiftServerService();
        localSwiftServerService.start();
        SwiftContext.get().getBean("swiftAnalyseService", AnalyseService.class).start();
    }

    @Test
    public void testRegisterService() throws Exception {
        Class c = LocalSwiftServerService.class;
        Field indexingService = c.getDeclaredField("indexingService");
        indexingService.setAccessible(true);
        Field analyseService = c.getDeclaredField("analyseService");
        analyseService.setAccessible(true);
        Assert.assertNotNull(analyseService.get(localSwiftServerService));
        Assert.assertNull(indexingService.get(localSwiftServerService));
    }

}