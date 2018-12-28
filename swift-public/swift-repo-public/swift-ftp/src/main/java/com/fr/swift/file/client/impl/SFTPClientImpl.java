package com.fr.swift.file.client.impl;

import com.fr.swift.file.client.SwiftFTPClient;
import com.fr.swift.repository.config.FtpRepositoryConfig;
import com.fr.swift.repository.utils.SwiftRepositoryUtils;
import com.fr.swift.util.Strings;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * @author yee
 * @date 2018-12-03
 */
public class SFTPClientImpl implements SwiftFTPClient {
    private String host;
    private int port;
    private String username;
    private String pass;
    private int connectTimeout;
    private Channel channel;
    private ChannelSftp channelSftp;
    private Session session;
    private String privateKey;
    private String passPhrase;

    public SFTPClientImpl(FtpRepositoryConfig config) {
        host = config.getHost();
        port = Integer.parseInt(config.getPort());
        username = config.getUsername();
        pass = config.getPassword();
        this.connectTimeout = Integer.parseInt(config.getConnectTimeout());
        this.privateKey = config.getPrivateKey();
        this.passPhrase = config.getPassPhrase();
    }

    @Override
    public String[] listNames(String path) throws SftpException {
        Vector<ChannelSftp.LsEntry> vector = channelSftp.ls(path);
        List<String> names = new ArrayList<String>();
        for (ChannelSftp.LsEntry lsEntry : vector) {
            String fileName = lsEntry.getFilename();
            if (fileName.equals(".") || fileName.equals("..")) {
                continue;
            }
            names.add(fileName);
        }
        return names.toArray(new String[names.size()]);
    }

    @Override
    public long getSize(String path) throws Exception {
        if (!isDirectory(path)) {
            String parent = SwiftRepositoryUtils.getParent(path);
            String name = SwiftRepositoryUtils.getName(path);
            if (Strings.isNotEmpty(parent)) {
                Vector<ChannelSftp.LsEntry> entryVector = channelSftp.ls(parent);
                for (ChannelSftp.LsEntry lsEntry : entryVector) {
                    if (lsEntry.getFilename().equals(name)) {
                        return lsEntry.getAttrs().getSize();
                    }
                }
                return 0;
            }
            return 0;
        } else {
            long size = 0;
            Vector<ChannelSftp.LsEntry> vector = channelSftp.ls(path);
            for (ChannelSftp.LsEntry lsEntry : vector) {
                size += lsEntry.getAttrs().getSize();
            }
            return size;
        }
    }

    @Override
    public void write(String path, InputStream inputStream) throws Exception {
        OutputStream os = channelSftp.put(path);
        try {
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(bytes, 0, 1024)) != -1) {
                os.write(bytes, 0, len);
            }
        } finally {
            if (null != inputStream) {
                inputStream.close();
            }
            if (null != os) {
                os.close();
            }
        }

    }

    @Override
    public boolean delete(String path) throws SftpException {
        if (isDirectory(path)) {
            channelSftp.rmdir(path);
        } else {
            channelSftp.rm(path);
        }
        return true;
    }

    @Override
    public boolean rename(String src, String dest) throws SftpException {
        channelSftp.rename(src, dest);
        return true;
    }

    @Override
    public boolean exists(String path) {
        try {
            Vector vector = channelSftp.ls(path);
            return !vector.isEmpty();
        } catch (SftpException e) {
            return false;
        }
    }

    @Override
    public boolean isDirectory(String path) throws SftpException {
        SftpATTRS attrs = channelSftp.lstat(path);
        return attrs.isDir();
    }

    @Override
    public InputStream toStream(String path) throws SftpException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        channelSftp.get(path, bao);
        ByteArrayInputStream bai = new ByteArrayInputStream(bao.toByteArray());
        try {
            bao.close();
        } catch (IOException ignore) {
        }
        return bai;
    }

    @Override
    public boolean mkdirs(String path) throws SftpException {
        try {
            channelSftp.mkdir(path);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    @Override
    public void close() {
        if (null != channelSftp) {
            channelSftp.quit();
        }
        if (null != channel) {
            channel.disconnect();
        }
        if (null != session) {
            session.disconnect();
        }
    }

    @Override
    public boolean login() throws JSchException {
        JSch jsch = new JSch();
        try {
            if (Strings.isNotEmpty(this.privateKey)) {
                jsch.addIdentity(this.privateKey, this.passPhrase);
            }
            session = jsch.getSession(username, host, port);
            session.setPassword(pass);
            session.setTimeout(connectTimeout);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            return !channelSftp.isClosed();
        } catch (JSchException e) {
            close();
            throw e;
        }
    }

    @Override
    public void connect() throws JSchException {
        JSch jsch = new JSch();
        Session session = null;
        try {
            if (Strings.isNotEmpty(this.privateKey)) {
                jsch.addIdentity(this.privateKey, this.passPhrase);
            }
            session = jsch.getSession(username, host, port);
            session.setPassword(pass);
            session.setTimeout(connectTimeout);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
        } finally {
            if (null != session) {
                session.disconnect();
            }
        }
    }

    @Override
    public boolean isConnected() {
        return channelSftp.isConnected();
    }

    @Override
    public void disconnect() {
        close();
    }
}
