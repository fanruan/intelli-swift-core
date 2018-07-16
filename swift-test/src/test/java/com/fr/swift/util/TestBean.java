package com.fr.swift.util;

/**
 * @author yee
 * @date 2018/7/16
 */
public class TestBean {
    private String username;
    private String password;
    private String driverClass;
    private String dialectClass;
    private String url;

    public TestBean() {
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
