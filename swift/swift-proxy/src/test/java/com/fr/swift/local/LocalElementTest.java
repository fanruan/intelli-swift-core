package com.fr.swift.local;

import com.fr.swift.basic.Destination;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.UrlFactory;
import junit.framework.TestCase;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class LocalElementTest extends TestCase {
    public void testLocalUrl() {
        URL url = new LocalUrl();
        assertNull(url.getDestination());
    }

    public void testDestination() {
        Destination destination = new LocalDestination();
        assertNull(destination.getId());
    }

    public void testLocalUrlFactory() {
        UrlFactory urlFactory = new LocalUrlFactory();
        assertNull(urlFactory.getURL("test"));
    }

    public void testLocalResult() {
        LocalResult localResult = new LocalResult("test", null);
        assertEquals(localResult.get(), "test");
        assertNull(localResult.getException());
        localResult.setResult(null);
        localResult.setException(new Exception());
        assertNull(localResult.get());
        assertNotNull(localResult.getException());
    }
}
