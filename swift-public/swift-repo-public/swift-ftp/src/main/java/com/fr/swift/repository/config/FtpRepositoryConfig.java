package com.fr.swift.repository.config;

import com.fr.swift.config.annotation.ConfigField;
import com.fr.swift.file.SwiftRemoteFileSystemType;
import com.fr.swift.repository.SwiftFileSystemConfig;
import com.fr.swift.util.Strings;

/**
 * @author yee
 * @date 2018/6/15
 */
public class FtpRepositoryConfig implements SwiftFileSystemConfig {

    @ConfigField
    private String protocol = "FTP";
    @ConfigField
    private String host = "";
    @ConfigField
    private String port = "21";
    @ConfigField
    private String username = "";
    @ConfigField
    private String password = "";
    @ConfigField
    private String privateKey = "";
    @ConfigField
    private String passPhrase = "";
    @ConfigField
    private String connectTimeout = "30000";
    @ConfigField
    private String charset = "UTF-8";
    @ConfigField
    private String passive = "true";
    @ConfigField
    private String soTimeout = "30000";
    @ConfigField
    private String dataTimeout = "30000";
    @ConfigField
    private String rootPath = "/";

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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return Strings.isEmpty(this.password) ? null : this.password/*SecurityToolbox.decrypt(this.password)*/;
    }

    public void setPassword(String password) {
        this.password = password/*SecurityToolbox.encrypt(password)*/;
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

    public String getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(String connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getCharset() {
        return Strings.isEmpty(charset) ? "UTF-8" : charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getPassive() {
        return passive;
    }

    public void setPassive(String passive) {
        this.passive = passive;
    }

    public String getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(String soTimeout) {
        this.soTimeout = soTimeout;
    }

    public String getDataTimeout() {
        return dataTimeout;
    }

    public void setDataTimeout(String dataTimeout) {
        this.dataTimeout = dataTimeout;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public SwiftRemoteFileSystemType getType() {
        return SwiftRemoteFileSystemType.FTP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FtpRepositoryConfig that = (FtpRepositoryConfig) o;

        if (protocol != null ? !protocol.equals(that.protocol) : that.protocol != null) {
            return false;
        }
        if (host != null ? !host.equals(that.host) : that.host != null) {
            return false;
        }
        if (port != null ? !port.equals(that.port) : that.port != null) {
            return false;
        }
        if (username != null ? !username.equals(that.username) : that.username != null) {
            return false;
        }
        if (password != null ? !getPassword().equals(that.getPassword()) : that.password != null) {
            return false;
        }
        if (privateKey != null ? !privateKey.equals(that.privateKey) : that.privateKey != null) {
            return false;
        }
        if (passPhrase != null ? !passPhrase.equals(that.passPhrase) : that.passPhrase != null) {
            return false;
        }
        if (connectTimeout != null ? !connectTimeout.equals(that.connectTimeout) : that.connectTimeout != null) {
            return false;
        }
        if (charset != null ? !charset.equals(that.charset) : that.charset != null) {
            return false;
        }
        if (passive != null ? !passive.equals(that.passive) : that.passive != null) {
            return false;
        }
        if (soTimeout != null ? !soTimeout.equals(that.soTimeout) : that.soTimeout != null) {
            return false;
        }
        if (dataTimeout != null ? !dataTimeout.equals(that.dataTimeout) : that.dataTimeout != null) {
            return false;
        }
        return rootPath != null ? rootPath.equals(that.rootPath) : that.rootPath == null;
    }

    @Override
    public int hashCode() {
        int result = protocol != null ? protocol.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? getPassword().hashCode() : 0);
        result = 31 * result + (privateKey != null ? privateKey.hashCode() : 0);
        result = 31 * result + (passPhrase != null ? passPhrase.hashCode() : 0);
        result = 31 * result + (connectTimeout != null ? connectTimeout.hashCode() : 0);
        result = 31 * result + (charset != null ? charset.hashCode() : 0);
        result = 31 * result + (passive != null ? passive.hashCode() : 0);
        result = 31 * result + (soTimeout != null ? soTimeout.hashCode() : 0);
        result = 31 * result + (dataTimeout != null ? dataTimeout.hashCode() : 0);
        result = 31 * result + (rootPath != null ? rootPath.hashCode() : 0);
        return result;
    }
}
