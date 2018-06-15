package com.fr.swift.config.bean;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.security.SecurityToolbox;
import com.fr.stable.StringUtils;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.file.conf.impl.FtpRepositoryConfigImpl;

/**
 * @author yee
 * @date 2018/6/15
 */
public class FtpRepositoryConfBean implements RepositoryConfBean<FtpRepositoryConfigImpl> {
    private Conf<String> protocol = Holders.simple("FTP");
    private Conf<String> host = Holders.simple("");
    private Conf<Integer> port = Holders.simple(21);
    private Conf<String> username = Holders.simple("");
    private Conf<String> password = Holders.simple("");
    private Conf<String> privateKey = Holders.simple("");
    private Conf<String> passPhrase = Holders.simple("");
    private Conf<Integer> connectTimeout = Holders.simple(10000);
    private Conf<String> charset = Holders.simple("");
    private Conf<Boolean> passive = Holders.simple(true);
    private Conf<Integer> soTimeout = Holders.simple(10000);
    private Conf<Integer> dataTimeout = Holders.simple(10000);

    public FtpRepositoryConfBean() {
    }

    public boolean isPassive() {
        return this.passive.get();
    }

    public void setPassive(boolean passive) {
        this.passive.set(passive);
    }

    public int getSoTimeout() {
        return this.soTimeout.get();
    }

    public void setSoTimeout(Integer soTimeout) {
        this.soTimeout.set(soTimeout);
    }

    public int getDataTimeout() {
        return this.dataTimeout.get();
    }

    public void setDataTimeout(int dataTimeout) {
        this.dataTimeout.set(dataTimeout);
    }

    public String getProtocol() {
        return this.protocol.get();
    }

    public void setProtocol(String protocol) {
        this.protocol.set(protocol);
    }

    public String getHost() {
        return this.host.get();
    }

    public void setHost(String host) {
        this.host.set(host);
    }

    public int getPort() {
        return this.port.get();
    }

    public void setPort(int port) {
        this.port.set(port);
    }

    public String getUsername() {
        return this.username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return StringUtils.isEmpty(this.password.get()) ? null : SecurityToolbox.decrypt(this.password.get());
    }

    public void setPassword(String password) {
        this.password.set(SecurityToolbox.encrypt(password));
    }

    public int getConnectTimeout() {
        return this.connectTimeout.get();
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout.set(connectTimeout);
    }

    public String getCharset() {
        String var1 = this.charset.get();
        return StringUtils.isEmpty(var1) ? "UTF-8" : var1;
    }

    public void setCharset(String charset) {
        this.charset.set(charset);
    }

    public String getPrivateKey() {
        return this.privateKey.get();
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey.set(privateKey);
    }

    public String getPassPhrase() {
        return this.passPhrase.get();
    }

    public void setPassPhrase(String passPhrase) {
        this.passPhrase.set(passPhrase);
    }

    public Object clone() throws CloneNotSupportedException {
        FtpRepositoryConfBean bean = (FtpRepositoryConfBean) super.clone();
        bean.host = (Conf) this.host.clone();
        bean.port = (Conf) this.port.clone();
        bean.username = (Conf) this.username.clone();
        bean.password = (Conf) this.password.clone();
        bean.privateKey = (Conf) this.privateKey.clone();
        bean.passPhrase = (Conf) this.passPhrase.clone();
        bean.connectTimeout = (Conf) this.connectTimeout.clone();
        bean.charset = (Conf) this.charset.clone();
        bean.protocol = (Conf) this.protocol.clone();
        return bean;
    }

    @Override
    public String getNameSpace() {
        return SwiftConfigConstants.FRConfiguration.FTP_REPOSITORY_NAMESPACE;
    }

    @Override
    public FtpRepositoryConfigImpl convert() {
        FtpRepositoryConfigImpl config = new FtpRepositoryConfigImpl();
        config.setCharset(getCharset());
        config.setHost(getHost());
        config.setUsername(getUsername());
        config.setPassword(getPassword());
        config.setPrivateKey(getPrivateKey());
        config.setPassPhrase(getPassPhrase());
        config.setConnectTimeout(getConnectTimeout());
        config.setProtocol(getProtocol());
        return config;
    }
}
