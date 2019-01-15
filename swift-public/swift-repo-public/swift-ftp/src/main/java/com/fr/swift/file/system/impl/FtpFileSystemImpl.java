package com.fr.swift.file.system.impl;

import com.fr.swift.file.client.SwiftFTPClient;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.AbstractFileSystem;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.factory.SwiftFileSystemFactory;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.config.FtpRepositoryConfig;
import com.fr.swift.repository.utils.SwiftRepositoryUtils;
import com.fr.swift.util.Strings;
import org.apache.commons.pool2.ObjectPool;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

/**
 * @author yee
 * @date 2018/6/26
 */
public class FtpFileSystemImpl extends AbstractFileSystem<FtpRepositoryConfig> {

    private ObjectPool<SwiftFTPClient> clientPool;
    private SwiftFileSystemFactory<FtpFileSystemImpl, FtpRepositoryConfig> systemFactory;
    private String rootURI;

    public FtpFileSystemImpl(FtpRepositoryConfig config, String uri, ObjectPool<SwiftFTPClient> clientPool, SwiftFileSystemFactory<FtpFileSystemImpl, FtpRepositoryConfig> systemFactory) {
        super(config, uri);
        this.systemFactory = systemFactory;
        this.clientPool = clientPool;
        rootURI = Strings.trimSeparator(config.getRootPath() + "/", "/");
    }

    private SwiftFTPClient acquireClient() {
        try {
            return this.clientPool.borrowObject();
        } catch (Exception e) {
            throw new RuntimeException("No FineFTP available, Please check configuration or network state!", e);
        }
    }

    private void returnClient(SwiftFTPClient fineFTP) {
        try {
            this.clientPool.returnObject(fineFTP);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage());
        }

    }

    @Override
    protected SwiftFileSystem[] list() throws SwiftFileException {
        SwiftFTPClient ftp = acquireClient();
        try {
            String[] children = ftp.listNames(resolve(rootURI, getResourceURI()));
            if (null != children) {
                SwiftFileSystem[] childFileSystem = new SwiftFileSystem[children.length];
                for (int i = 0; i < children.length; i++) {
                    childFileSystem[i] = systemFactory.createFileSystem(config, resolve(getResourceURI(), children[i]));
                }
                return childFileSystem;
            }
            return new SwiftFileSystem[0];
        } catch (Exception e) {
            throw new SwiftFileException(e);
        } finally {
            returnClient(ftp);
        }

    }

    @Override
    protected long fileSize() {
        SwiftFTPClient ftp = acquireClient();
        try {
            return ftp.getSize(getResourceURI());
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return 0;
        } finally {
            returnClient(ftp);
        }
    }

    @Override
    public void write(String remote, InputStream inputStream) throws SwiftFileException {
        SwiftFTPClient ftp = acquireClient();
        try {
            ftp.write(resolve(rootURI, remote), inputStream);
        } catch (Exception e) {
            throw new SwiftFileException(e);
        } finally {
            returnClient(ftp);
        }
    }

    @Override
    public SwiftFileSystem read(String remote) throws SwiftFileException {
        SwiftFileSystem fileSystem;
        if (remote.equals(getResourceURI())) {
            fileSystem = this;
        } else {
            fileSystem = systemFactory.createFileSystem(config, remote);
        }
        if (fileSystem.isExists()) {
            return fileSystem;
        }
        throw new SwiftFileException(String.format("File path '%s' not exists!", remote));
    }

    @Override
    public SwiftFileSystem parent() {
        return systemFactory.createFileSystem(config, getParentURI());
    }

    @Override
    public boolean remove(String remote) throws SwiftFileException {
        SwiftFTPClient ftp = acquireClient();
        try {
            return remove(ftp, resolve(rootURI, remote));
        } catch (Exception e) {
            throw new SwiftFileException(e);
        } finally {
            returnClient(ftp);
        }
    }

    private boolean remove(SwiftFTPClient ftp, String path) throws Exception {
        if (ftp.isDirectory(path)) {
            for (String name : ftp.listNames(path)) {
                remove(ftp, resolve(path, name));
            }
        }
        return ftp.delete(path);
    }

    @Override
    public boolean renameTo(String src, String dest) throws SwiftFileException {
        SwiftFTPClient ftp = acquireClient();
        try {
            return ftp.rename(resolve(rootURI, src), resolve(rootURI, dest));
        } catch (Exception e) {
            throw new SwiftFileException(e);
        } finally {
            returnClient(ftp);
        }
    }

    @Override
    public boolean copy(String src, String dest) {
        return false;
    }

    @Override
    public boolean isExists() {
        SwiftFTPClient ftp = acquireClient();
        try {
            return ftp.exists(resolve(rootURI, getResourceURI()));
        } catch (Exception e) {
            return false;
        } finally {
            returnClient(ftp);
        }
    }

    @Override
    public boolean isDirectory() {
        SwiftFTPClient ftp = acquireClient();
        try {
            return ftp.isDirectory(resolve(rootURI, getResourceURI()));
        } catch (Exception e) {
            return false;
        } finally {
            returnClient(ftp);
        }
    }

    @Override
    public InputStream toStream() throws SwiftFileException {
        SwiftFTPClient ftp = acquireClient();
        try {
            return ftp.toStream(resolve(rootURI, getResourceURI()));
        } catch (Exception e) {
            throw new SwiftFileException(e);
        } finally {
            returnClient(ftp);
        }
    }

    @Override
    public String getResourceName() {
        String resourceUri = getResourceURI();
        return resourceUri.substring(resourceUri.lastIndexOf("/") + 1);
    }

    @Override
    public void mkdirs() {
        SwiftFTPClient ftp = acquireClient();
        try {
            createDirectory(ftp, resolve(rootURI, getResourceURI()));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            returnClient(ftp);
        }
    }

    @Override
    public void close() throws SwiftFileException {
        try {
            clientPool.clear();
        } catch (Exception e) {
            throw new SwiftFileException(e);
        }
    }

    @Override
    public void testConnection() throws Exception {
        SwiftFTPClient ftp = acquireClient();
        try {
            ftp.connect();
        } finally {
            returnClient(ftp);
        }
    }

    private boolean createDirectory(SwiftFTPClient client, String path) throws Exception {
        if (client.exists(path)) {
            return false;
        }
        try {
            boolean result = client.mkdirs(path);
            if (!result) {
                result = createDirectory(client, SwiftRepositoryUtils.getParent(path)) && client.mkdirs(path);
            }

            return result;
        } catch (SocketException var3) {
            throw new IOException();
        } catch (Exception var4) {
            SwiftLoggers.getLogger().error("Failed to create directory " + path, var4);
            return false;
        }
    }
}
