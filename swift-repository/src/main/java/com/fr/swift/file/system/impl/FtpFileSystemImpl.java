package com.fr.swift.file.system.impl;

import com.fr.ftp.FTPUtils;
import com.fr.ftp.client.FineFTP;
import com.fr.general.ComparatorUtils;
import com.fr.io.utils.ResourceIOUtils;
import com.fr.stable.Filter;
import com.fr.swift.file.SwiftRemoteFileSystemType;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.AbstractFileSystem;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.factory.SwiftFileSystemFactory;
import com.fr.swift.file.system.pool.RemoteFactoryCreator;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.config.FtpRepositoryConfig;
import com.fr.swift.util.Strings;
import com.fr.third.org.apache.commons.pool2.ObjectPool;

import java.io.InputStream;

/**
 * @author yee
 * @date 2018/6/26
 */
public class FtpFileSystemImpl extends AbstractFileSystem<FtpRepositoryConfig> {

    private ObjectPool<FineFTP> clientPool;
    private SwiftFileSystemFactory<FtpFileSystemImpl, FtpRepositoryConfig> systemFactory;
    private String rootURI;

    public FtpFileSystemImpl(FtpRepositoryConfig config, String uri, ObjectPool<FineFTP> clientPool) {
        super(config, uri);
        this.systemFactory = RemoteFactoryCreator.creator().getFactory(SwiftRemoteFileSystemType.FTP);
        this.clientPool = clientPool;
        rootURI = Strings.trimSeparator(config.getRootPath() + "/", "/");
    }

    private FineFTP acquireClient() {
        try {
            return this.clientPool.borrowObject();
        } catch (Exception e) {
            throw new RuntimeException("No FineFTP available, Please check configuration or network state!", e);
        }
    }

    private void returnClient(FineFTP fineFTP) {
        try {
            this.clientPool.returnObject(fineFTP);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage());
        }

    }

    @Override
    protected SwiftFileSystem[] list() throws SwiftFileException {
        FineFTP ftp = acquireClient();
        try {
            String[] children = FTPUtils.list(ftp, resolve(rootURI, getResourceURI()), new Filter<String>() {
                @Override
                public boolean accept(String s) {
                    return true;
                }
            });
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
    public void write(String remote, InputStream inputStream) throws SwiftFileException {
        FineFTP ftp = acquireClient();
        try {
            FTPUtils.write(ftp, resolve(rootURI, remote), inputStream);
        } catch (Exception e) {
            throw new SwiftFileException(e);
        } finally {
            returnClient(ftp);
        }
    }

    @Override
    public SwiftFileSystem read(String remote) throws SwiftFileException {
        SwiftFileSystem fileSystem;
        if (ComparatorUtils.equals(remote, getResourceURI())) {
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
        FineFTP ftp = acquireClient();
        try {
            return FTPUtils.delete(ftp, resolve(rootURI, remote));
        } catch (Exception e) {
            throw new SwiftFileException(e);
        } finally {
            returnClient(ftp);
        }
    }

    @Override
    public boolean renameTo(String src, String dest) throws SwiftFileException {
        FineFTP ftp = acquireClient();
        try {
            return FTPUtils.rename(ftp, resolve(rootURI, src), resolve(rootURI, dest));
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
        FineFTP ftp = acquireClient();
        try {
            return FTPUtils.exist(ftp, resolve(rootURI, getResourceURI()));
        } finally {
            returnClient(ftp);
        }
    }

    @Override
    public boolean isDirectory() {
        FineFTP ftp = acquireClient();
        try {
            return FTPUtils.isDirectory(ftp, resolve(rootURI, getResourceURI()));
        } finally {
            returnClient(ftp);
        }
    }

    @Override
    public InputStream toStream() throws SwiftFileException {
        FineFTP ftp = acquireClient();
        try {
            return FTPUtils.read(ftp, resolve(rootURI, getResourceURI()));
        } catch (Exception e) {
            throw new SwiftFileException(e);
        } finally {
            returnClient(ftp);
        }
    }

    @Override
    public String getResourceName() {
        return ResourceIOUtils.getName(getResourceURI());
    }

    @Override
    public void mkdirs() {
        FineFTP ftp = acquireClient();
        try {
            FTPUtils.createDirectory(ftp, resolve(rootURI, getResourceURI()));
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
}
