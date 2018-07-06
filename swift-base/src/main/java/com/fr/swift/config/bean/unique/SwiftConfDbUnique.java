package com.fr.swift.config.bean.unique;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.utils.UniqueKey;
import com.fr.security.SecurityToolbox;
import com.fr.stable.StringUtils;
import com.fr.swift.config.bean.Convert;
import com.fr.swift.config.bean.SwiftConfDbBean;

/**
 * @author yee
 * @date 2018/6/30
 */
public class SwiftConfDbUnique extends UniqueKey implements Convert<SwiftConfDbBean> {
    private Conf<String> username = Holders.simple(StringUtils.EMPTY);
    private Conf<String> password = Holders.simple(StringUtils.EMPTY);
    private Conf<String> driverClass = Holders.simple(StringUtils.EMPTY);
    private Conf<String> dialectClass = Holders.simple(StringUtils.EMPTY);
    private Conf<String> url = Holders.simple(StringUtils.EMPTY);

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return SecurityToolbox.decrypt(password.get());
    }

    public void setPassword(String password) {
        this.password.set(SecurityToolbox.encrypt(password));
    }

    public String getDriverClass() {
        return driverClass.get();
    }

    public void setDriverClass(String driverClass) {
        this.driverClass.set(driverClass);
    }

    public String getDialectClass() {
        return dialectClass.get();
    }

    public void setDialectClass(String dialectClass) {
        this.dialectClass.set(dialectClass);
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    @Override
    public SwiftConfDbBean convert() {
        SwiftConfDbBean bean = new SwiftConfDbBean();
        bean.setUrl(getUrl());
        bean.setPassword(getPassword());
        bean.setUsername(getUsername());
        bean.setDialectClass(getDialectClass());
        bean.setDriverClass(getDriverClass());
        return bean;
    }
}
