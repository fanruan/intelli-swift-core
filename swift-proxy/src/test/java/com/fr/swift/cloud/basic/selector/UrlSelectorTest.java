package com.fr.swift.cloud.basic.selector;

import com.fr.swift.cloud.basics.UrlFactory;
import com.fr.swift.cloud.basics.base.selector.UrlSelector;
import com.fr.swift.cloud.local.LocalUrlFactory;
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
