package com.fr.swift.file.conf.impl;

import com.fr.security.SecurityToolbox;
import com.fr.stable.StringUtils;
import com.fr.swift.file.conf.AbstractSwiftFileSystemConfig;
import com.fr.swift.file.system.SwiftFileSystemType;

/**
 * @author yee
 * @date 2018/6/15
 */
public class FtpRepositoryConfigImpl extends AbstractSwiftFileSystemConfig {

    private String protocol = "FTP";
    private String host = "";
    private int port = 21;
    private String username = "";
    private String password = "";
    private String privateKey = "";
    private String passPhrase = "";
    private int connectTimeout = 10000;
    private String charset = "";
    private boolean passive = true;
    private int soTimeout = 10000;
    private int dataTimeout = 10000;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return StringUtils.isEmpty(this.password) ? null : SecurityToolbox.decrypt(this.password);
    }

    public void setPassword(String password) {
        this.password = SecurityToolbox.encrypt(password);
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPassPhrase() {
        return passPhrase;
    }

    public void setPassPhrase(String passPhrase) {
        this.passPhrase = passPhrase;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getCharset() {
        return StringUtils.isEmpty(charset) ? "UTF-8" : charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isPassive() {
        return passive;
    }

    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getDataTimeout() {
        return dataTimeout;
    }

    public void setDataTimeout(int dataTimeout) {
        this.dataTimeout = dataTimeout;
    }

    @Override
    public SwiftFileSystemType getType() {
        return SwiftFileSystemType.FTP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FtpRepositoryConfigImpl that = (FtpRepositoryConfigImpl) o;

        if (port != that.port) return false;
        if (connectTimeout != that.connectTimeout) return false;
        if (passive != that.passive) return false;
        if (soTimeout != that.soTimeout) return false;
        if (dataTimeout != that.dataTimeout) return false;
        if (protocol != null ? !protocol.equals(that.protocol) : that.protocol != null) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (privateKey != null ? !privateKey.equals(that.privateKey) : that.privateKey != null) return false;
        if (passPhrase != null ? !passPhrase.equals(that.passPhrase) : that.passPhrase != null) return false;
        return charset != null ? charset.equals(that.charset) : that.charset == null;
    }

    @Override
    public int hashCode() {
        int result = protocol != null ? protocol.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (privateKey != null ? privateKey.hashCode() : 0);
        result = 31 * result + (passPhrase != null ? passPhrase.hashCode() : 0);
        result = 31 * result + connectTimeout;
        result = 31 * result + (charset != null ? charset.hashCode() : 0);
        result = 31 * result + (passive ? 1 : 0);
        result = 31 * result + soTimeout;
        result = 31 * result + dataTimeout;
        return result;
    }
}
