package com.fr.bi.stable.utils.program;


import com.fr.bi.common.persistent.BIBeanReaderWrapper;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Connery on 2015/12/18.
 */
public class BIBeanTest extends TestCase {
    public void testGetter() {
        try {

            ArrayList<Method> methods = BIBeanUtils.fetchGetterMethod(BIAuthor.class);
            assertEquals(methods.size(), 3);
            methods = BIBeanUtils.fetchPrefixMethod(BIAuthor.class, "get");
            assertEquals(methods.size(), 5);
            methods = BIBeanUtils.filterMethodByParaAmount(BIBeanUtils.fetchPrefixMethod(BIAuthor.class, "get"), 1);
            assertEquals(methods.size(), 1);
            methods = BIBeanUtils.filterMethodByParaAmount(BIBeanUtils.fetchPrefixMethod(BIAuthor.class, "get"), 0);
            assertEquals(methods.size(), 4);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);

        }
    }

    public void testSetter() {
        try {
            ArrayList<Method> methods = BIBeanUtils.fetchSetterMethod(BIAuthor.class);
            assertEquals(methods.size(), 3);
            methods = BIBeanUtils.fetchPrefixMethod(BIAuthor.class, "set");
            assertEquals(methods.size(), 5);
            methods = BIBeanUtils.filterMethodByParaAmount(BIBeanUtils.fetchPrefixMethod(BIAuthor.class, "set"), 1);
            assertEquals(methods.size(), 4);
            methods = BIBeanUtils.filterMethodByParaAmount(BIBeanUtils.fetchPrefixMethod(BIAuthor.class, "set"), 0);
            assertEquals(methods.size(), 1);

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);

        }
    }

    public void testFields() {
        try {
            ArrayList<Field> fields = BIBeanUtils.fetchAllAttributes(BIAuthor.class);
            assertEquals(fields.size(), 4);
            fields = BIBeanUtils.fetchAllAttributes(BIFamousAuthor.class);
            assertEquals(fields.size(), 5);

        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);

        }
    }

    public void testPrivateFields() {
        try {
            BIBean4Test test = new BIBean4Test();
            BIBeanReaderWrapper wrapper = new BIBeanReaderWrapper(test, test.getClass());
            wrapper.setOriginalValue("name", "hell");
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);

        }
    }
}