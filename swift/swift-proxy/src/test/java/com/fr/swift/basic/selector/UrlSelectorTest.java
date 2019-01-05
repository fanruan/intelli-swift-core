package com.fr.swift.basic.selector;

import com.fr.swift.basics.UrlFactory;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.local.LocalUrlFactory;
import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class UrlSelectorTest extends TestCase {

    public void testUrlSelector() {
        assertNotNull(UrlSelector.getInstance().getFactory());
        assertTrue(UrlSelector.getInstance().getFactory() instanceof LocalUrlFactory);
        UrlFactory urlFactory = EasyMock.createMock(UrlFactory.class);
        UrlSelector.getInstance().switchFactory(urlFactory);
        assertFalse(UrlSelector.getInstance().getFactory() instanceof LocalUrlFactory);
    }
}
