package com.fr.swift.util;

import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author yee
 * @date 2018/7/16
 */
public class ReflectUtilsTest {

    @Test
    public void set() throws Exception {
        TestClass bean = new TestClass();
        ObjectMapper objectMapper = new ObjectMapper();
        ReflectUtils.set(TestClass.class.getDeclaredField("string"), bean, "change");
        ReflectUtils.set(TestClass.class.getDeclaredField("int1"), bean, "10");
        ReflectUtils.set(TestClass.class.getDeclaredField("int2"), bean, "100");
        ReflectUtils.set(TestClass.class.getDeclaredField("boolen1"), bean, "false");
        ReflectUtils.set(TestClass.class.getDeclaredField("boolen2"), bean, "false");
        TestBean tmp = new TestBean();
        tmp.setUsername("root1");
        tmp.setPassword("password1");
        tmp.setDriverClass("driver1");
        tmp.setDialectClass("dialect1");
        tmp.setUrl("url1");
        ReflectUtils.set(TestClass.class.getDeclaredField("object"), bean, objectMapper.writeValueAsString(tmp));


        assertEquals(bean.string, "change");
        assertEquals(bean.int1, 10);
        assertEquals(bean.int2, new Integer(100));
        assertEquals(bean.boolen1, false);
        assertEquals(bean.boolen2, false);
        assertEquals(bean.object, tmp);
    }

    @Test
    public void get() throws Exception {
        TestClass bean = new TestClass();
        ObjectMapper objectMapper = new ObjectMapper();
        assertEquals(bean.string, ReflectUtils.getString(TestClass.class.getDeclaredField("string"), bean));
        assertEquals(String.valueOf(bean.int1), ReflectUtils.getString(TestClass.class.getDeclaredField("int1"), bean));
        assertEquals(bean.int2.toString(), ReflectUtils.getString(TestClass.class.getDeclaredField("int2"), bean));
        assertEquals(bean.boolen1.toString(), ReflectUtils.getString(TestClass.class.getDeclaredField("boolen1"), bean));
        assertEquals(String.valueOf(bean.boolen2), ReflectUtils.getString(TestClass.class.getDeclaredField("boolen2"), bean));
        assertEquals(objectMapper.writeValueAsString(bean.object), ReflectUtils.getString(TestClass.class.getDeclaredField("object"), bean));
    }

    class TestClass {
        private String string = "hello";
        private int int1 = 100;
        private Integer int2 = 200;
        private Boolean boolen1 = true;
        private boolean boolen2 = true;
        private TestBean object;

        public TestClass() {
            object = new TestBean();
            object.setUsername("root");
            object.setPassword("password");
            object.setDriverClass("driver");
            object.setDialectClass("dialect");
            object.setUrl("url");
        }
    }

    @Test
    public void testNewInstance() throws Exception {
        TestBean bean = ReflectUtils.newInstance(TestBean.class);
        assertNotNull(bean);
        bean = ReflectUtils.newInstance(TestBean.class, "username", "password", "driver", "dialect", null);
        assertNotNull(bean);
    }
}