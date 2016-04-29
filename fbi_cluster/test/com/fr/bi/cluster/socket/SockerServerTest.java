package com.fr.bi.cluster.socket;

import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

import java.lang.reflect.Method;

/**
 * Created by FineSoft on 2015/7/7.
 */
public class SockerServerTest extends TestCase {

    public void testDealWithPrimitiveParam() {
        try {
            Foo foo = new Foo();
            Class[] classes = new Class[2];
            classes[0] = int.class;
            classes[1] = String.class;
            Method method = BISocketServer.dealWithPrimitiveParam(foo.getClass(), "getValue", classes);
            assertEquals(1, method.invoke(foo));
        } catch (Exception ex) {
             BILogger.getLogger().error(ex.getMessage(), ex);
        }
    }
}