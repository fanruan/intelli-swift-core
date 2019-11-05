package com.fr.swift.util;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author yee
 * @date 2018/7/16
 */
public class ReflectUtilsTest {

    @Test
    @Ignore
    public void set() throws Exception {
//        TestClass bean = new TestClass();
//        ObjectMapper objectMapper = new ObjectMapper();
//        ReflectUtils.set(TestClass.class.getDeclaredField("string"), bean, "change");
//        ReflectUtils.set(TestClass.class.getDeclaredField("int1"), bean, "10");
//        ReflectUtils.set(TestClass.class.getDeclaredField("int2"), bean, "100");
//        ReflectUtils.set(TestClass.class.getDeclaredField("boolen1"), bean, "false");
//        ReflectUtils.set(TestClass.class.getDeclaredField("boolen2"), bean, "false");
//        TestBean tmp = new TestBean();
//        tmp.setUsername("root1");
//        tmp.setPassword("password1");
//        tmp.setDriverClass("driver1");
//        tmp.setDialectClass("dialect1");
//        tmp.setUrl("url1");
//        ReflectUtils.set(TestClass.class.getDeclaredField("object"), bean, objectMapper.writeValueAsString(tmp));
//
//
//        assertEquals(bean.string, "change");
//        assertEquals(bean.int1, 10);
//        assertEquals(bean.int2, new Integer(100));
//        assertEquals(bean.boolen1, false);
//        assertEquals(bean.boolen2, false);
//        assertEquals(bean.object, tmp);
    }

    @Test
    @Ignore
    public void get() throws Exception {
//        TestClass bean = new TestClass();
//        ObjectMapper objectMapper = new ObjectMapper();
//        assertEquals(bean.string, ReflectUtils.getString(TestClass.class.getDeclaredField("string"), bean));
//        assertEquals(String.valueOf(bean.int1), ReflectUtils.getString(TestClass.class.getDeclaredField("int1"), bean));
//        assertEquals(bean.int2.toString(), ReflectUtils.getString(TestClass.class.getDeclaredField("int2"), bean));
//        assertEquals(bean.boolen1.toString(), ReflectUtils.getString(TestClass.class.getDeclaredField("boolen1"), bean));
//        assertEquals(String.valueOf(bean.boolen2), ReflectUtils.getString(TestClass.class.getDeclaredField("boolen2"), bean));
//        assertEquals(objectMapper.writeValueAsString(bean.object), ReflectUtils.getString(TestClass.class.getDeclaredField("object"), bean));
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

    /**
     * @author yee
     * @date 2018/7/16
     */
    public static class TestBean {
        private String username;
        private String password;
        private String driverClass;
        private String dialectClass;
        private String url;

        public TestBean() {
        }

        public TestBean(String username, String password, String driverClass, String dialectClass, String url) {
            this.username = username;
            this.password = password;
            this.driverClass = driverClass;
            this.dialectClass = dialectClass;
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriverClass() {
            return driverClass;
        }

        public void setDriverClass(String driverClass) {
            this.driverClass = driverClass;
        }

        public String getDialectClass() {
            return dialectClass;
        }

        public void setDialectClass(String dialectClass) {
            this.dialectClass = dialectClass;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestBean testBean = (TestBean) o;

            if (username != null ? !username.equals(testBean.username) : testBean.username != null) return false;
            if (password != null ? !password.equals(testBean.password) : testBean.password != null) return false;
            if (driverClass != null ? !driverClass.equals(testBean.driverClass) : testBean.driverClass != null)
                return false;
            if (dialectClass != null ? !dialectClass.equals(testBean.dialectClass) : testBean.dialectClass != null)
                return false;
            return url != null ? url.equals(testBean.url) : testBean.url == null;
        }

        @Override
        public int hashCode() {
            int result = username != null ? username.hashCode() : 0;
            result = 31 * result + (password != null ? password.hashCode() : 0);
            result = 31 * result + (driverClass != null ? driverClass.hashCode() : 0);
            result = 31 * result + (dialectClass != null ? dialectClass.hashCode() : 0);
            result = 31 * result + (url != null ? url.hashCode() : 0);
            return result;
        }
    }
}