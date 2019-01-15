package com.fr.swift.file.client.impl;

import com.fr.swift.file.client.SwiftFTPClient;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.repository.config.FtpRepositoryConfig;
import com.fr.swift.repository.utils.SwiftRepositoryUtils;
import com.fr.swift.util.Strings;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2018-12-03
 */
public class FTPClientImpl implements SwiftFTPClient {
    private FTPClient client;
    private String host;
    private int port;
    private String username = "anonymous";
    private String password = "anonymous@fanruan.com";
    private String charset;
    private boolean passive;
    private int connectTimeout;
    private int soTimeout;
    private int dataTimeout;

    public FTPClientImpl(FtpRepositoryConfig config) {
        this.host = config.getHost();
        this.port = Integer.parseInt(config.getPort());
        if (Strings.isNotEmpty(config.getUsername())) {
            this.username = config.getUsername();
            this.password = config.getPassword();
        }
        this.charset = config.getCharset();
        this.connectTimeout = Integer.parseInt(config.getConnectTimeout());
        this.soTimeout = Integer.parseInt(config.getSoTimeout());
        this.dataTimeout = Integer.parseInt(config.getDataTimeout());
        this.passive = Boolean.parseBoolean(config.getPassive());
    }

    @Override
    public String[] listNames(String path) throws IOException {
        FTPFile[] files = client.listFiles(path, new FTPFileFilter() {
            @Override
            public boolean accept(FTPFile ftpFile) {
                return !(ftpFile.getName().endsWith(".") || ftpFile.getName().endsWith(".."));
            }
        });
        String[] names = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            names[i] = files[i].getName();
        }
        return names;
    }

    @Override
    public long getSize(String path) throws IOException {
        FTPFile file = getFtpFile(path);
        if (null != file) {
            return file.getSize();
        }
        return 0;

    }

    private FTPFile getFtpFile(String path) throws IOException {
        FTPFile file = null;
        try {
            file = client.mlistFile(path);
        } catch (Exception ignore) {
        }
        if (null != file) {
            return file;
        }
        String parent = SwiftRepositoryUtils.getParent(path);
        final String name = SwiftRepositoryUtils.getName(path);
        if (Strings.isNotEmpty(parent)) {
            FTPFile[] files = client.listFiles(parent, new FTPFileFilter() {
                @Override
                public boolean accept(FTPFile ftpFile) {
                    return ftpFile.getName().equals(name);
                }
            });
            if (files.length > 0) {
                return files[0];
            }
        }
        return null;
    }

    @Override
    public void write(String path, InputStream inputStream) throws IOException {
        client.storeFile(path, inputStream);
    }

    @Override
    public boolean delete(String path) throws IOException {
        if (isDirectory(path)) {
            return client.removeDirectory(path);
        }
        return client.deleteFile(path);
    }

    @Override
    public boolean rename(String src, String dest) throws IOException {
        return client.rename(src, dest);
    }

    @Override
    public boolean exists(String path) throws IOException {
        FTPFile ftpFile = getFtpFile(path);
        return null != ftpFile;
    }

    @Override
    public boolean isDirectory(String path) throws IOException {
        FTPFile ftpFile = getFtpFile(path);
        if (null != ftpFile) {
            return ftpFile.isDirectory();
        }
        throw new SwiftFileException(String.format("%s is not exists", path));
    }

    @Override
    public InputStream toStream(String path) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ByteArrayInputStream bis;
        try {
            this.client.retrieveFile(path, bos);
            bis = new ByteArrayInputStream(bos.toByteArray());
        } finally {
            bos.flush();
            bos.close();
        }
        return bis;
    }

    @Override
    public boolean mkdirs(String path) throws IOException {
        return client.makeDirectory(path);
    }

    @Override
    public void close() throws IOException {
        if (null != client && client.isConnected()) {
            client.logout();
            client.disconnect();
        }
    }

    @Override
    public boolean login() throws IOException {
        this.client = new FTPClient();
        this.client.setConnectTimeout(this.connectTimeout);
        this.client.setControlEncoding(this.charset);
        this.client.connect(this.host, this.port);
        this.client.setDataTimeout(this.dataTimeout);
        this.client.setSoTimeout(this.soTimeout);
        boolean result = this.client.login(this.username, this.password);
        if (result) {
            this.client.setFileType(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
            if (this.passive) {
                this.client.enterLocalPassiveMode();
            }
        }
        return result;
    }

    @Override
    public void connect() throws IOException {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(host, port);
        } finally {
            ftp.disconnect();
        }
    }

    @Override
    public boolean isConnected() throws IOException {
        return client.isConnected() && this.client.sendNoOp();
    }

    @Override
    public void disconnect() throws IOException {
        client.disconnect();
    }
}
