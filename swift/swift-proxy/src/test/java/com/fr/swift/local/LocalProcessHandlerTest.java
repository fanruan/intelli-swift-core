package com.fr.swift.local;

import com.fr.swift.basic.Destination;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.annotation.Target;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.Test;

/**
 * This class created on 2019/1/4
 *
 * @author Lucifer
 * @description
 */
public class LocalProcessHandlerTest extends TestCase {

    InvokerCreator invokerCreator;

    @Override
    public void setUp() throws Exception {
        invokerCreator = EasyMock.createMock(InvokerCreator.class);
        URL url = EasyMock.createMock(URL.class);
        Destination destination = EasyMock.createMock(Destination.class);
        EasyMock.expect(url.getDestination()).andReturn(destination).anyTimes();
        EasyMock.expect(destination.getId()).andReturn("127.0.0.1:8080").anyTimes();
        EasyMock.expect(invokerCreator.createSyncInvoker(LocalProcessHandlerTest.class, null))
                .andReturn(new LocalInvoker(new LocalProcessHandlerTest(), LocalProcessHandlerTest.class, null)).anyTimes();
        EasyMock.replay(invokerCreator, destination, url);
    }

    @Test
    public void testProcessUrl() {
        LocalProcessHandler localProcessHandler = new LocalProcessHandler(invokerCreator);
        assertNull(localProcessHandler.processUrl(new Target[]{Target.HISTORY}));
    }

    @Test
    public void testProcessResult() throws Throwable {
        LocalProcessHandler localProcessHandler = new LocalProcessHandler(invokerCreator);
        String name = (String) localProcessHandler.processResult(LocalProcessHandlerTest.class.getMethod("getName"), new Target[]{Target.HISTORY});
        assertEquals(name, LocalProcessHandlerTest.class.getName());
    }

    public String getName() {
        return LocalProcessHandlerTest.class.getName();
    }
}
