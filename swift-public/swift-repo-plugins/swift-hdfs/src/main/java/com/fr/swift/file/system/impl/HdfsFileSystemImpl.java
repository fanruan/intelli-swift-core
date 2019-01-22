package com.fr.swift.file.system.impl;

import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.AbstractFileSystem;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.factory.SwiftFileSystemFactory;
import com.fr.swift.file.system.pool.RemoteFileSystemFactoryCreator;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.config.HdfsRepositoryConfig;
import com.fr.swift.repository.config.HdfsSystemType;
import com.fr.swift.util.Util;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author yee
 * @date 2018/5/28
 */
public class HdfsFileSystemImpl extends AbstractFileSystem<HdfsRepositoryConfig> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(HdfsFileSystemImpl.class);

            private KeyedObjectPool<String, FileSystem> keyedObjectPool;
    private SwiftFileSystemFactory<HdfsFileSystemImpl, HdfsRepositoryConfig> systemPool;

    public HdfsFileSystemImpl(final HdfsRepositoryConfig config, String uri, KeyedObjectPool keyedObjectPool) {
        super(config, uri);
        this.keyedObjectPool = keyedObjectPool;
        this.systemPool = RemoteFileSystemFactoryCreator.creator().getFactory(HdfsSystemType.HDFS);
    }

    @Override
    public void write(String remote, InputStream is) throws SwiftFileException {
        FileSystem fileSystem = borrowFileSystem(remote);
        try {
            fileSystem.delete(new Path(remote), true);
            OutputStream os = fileSystem.create(new Path(remote), true);
            IOUtils.copyBytes(is, os, 2048, true);
        } catch (IOException e) {
            throw new SwiftFileException(e);
        } finally {
            returnFileSystem(remote, fileSystem);
        }
    }

    private FileSystem borrowFileSystem(String uri) throws SwiftFileException {
        try {
            return keyedObjectPool.borrowObject(uri);
        } catch (Exception e) {
            throw new SwiftFileException(e);
        }
    }

    private FileSystem borrowFileSystem() throws SwiftFileException {
        return borrowFileSystem(getResourceURI());
    }

    private void returnFileSystem(String uri, FileSystem fileSystem) throws SwiftFileException {
        try {
            keyedObjectPool.returnObject(uri, fileSystem);
        } catch (Exception e) {
            throw new SwiftFileException(e);
        }
    }

    @Override
    public SwiftFileSystem read(String remote) throws SwiftFileException {
        SwiftFileSystem fileSystem;
        if (Util.equals(remote, getResourceURI())) {
            fileSystem = this;
        } else {
            fileSystem = systemPool.createFileSystem(config, remote);
        }
        if (fileSystem.isExists()) {
            return fileSystem;
        }
        throw new SwiftFileException(String.format("File path '%s' not exists!", remote));
    }

    @Override
    public SwiftFileSystem parent() {
        return systemPool.createFileSystem(config, getParentURI());
    }

    @Override
    public boolean remove(String remote) throws SwiftFileException {
        FileSystem fileSystem = borrowFileSystem(remote);
        try {
            return fileSystem.delete(new Path(remote), true);
        } catch (IOException e) {
            throw new SwiftFileException(e);
        } finally {
            returnFileSystem(remote, fileSystem);
        }
    }

    @Override
    public boolean renameTo(String src, String dest) throws SwiftFileException {
        FileSystem fileSystem = borrowFileSystem(src);
        try {
            return fileSystem.rename(new Path(src), new Path(dest));
        } catch (IOException e) {
            throw new SwiftFileException(e);
        } finally {
            returnFileSystem(src, fileSystem);
        }
    }

    @Override
    public boolean copy(String src, String dest) throws SwiftFileException {
        FileSystem fileSystem = borrowFileSystem(src);
        try {
            FileStatus fileStatus = fileSystem.getFileStatus(new Path(src));
            if (fileStatus.isDirectory()) {
                FileStatus[] children = fileSystem.listStatus(new Path(src));
                boolean mkdir = fileSystem.mkdirs(new Path(dest));
                if (mkdir) {
                    for (FileStatus child : children) {
                        systemPool.createFileSystem(config, child.getPath().toUri().getPath()).copy(resolve(dest, child.getPath().getName()));
                    }
                }
            } else if (fileStatus.isFile()) {
                FSDataOutputStream dos = fileSystem.create(new Path(dest));
                FSDataInputStream dis = fileSystem.open(new Path(src));
                IOUtils.copyBytes(dis, dos, 2048, true);
            }
            return true;
        } catch (IOException e) {
            throw new SwiftFileException(e);
        } finally {
            returnFileSystem(src, fileSystem);
        }
    }

    @Override
    public boolean isExists() {
        FileSystem fileSystem = null;
        try {
            fileSystem = borrowFileSystem();
            return fileSystem.exists(new Path(getResourceURI()));
        } catch (IOException e) {
            return false;
        } finally {
            if (null != fileSystem) {
                try {
                    returnFileSystem(getResourceURI(), fileSystem);
                } catch (SwiftFileException e) {
                    LOGGER.error(e);
                }
            }
        }
    }

    @Override
    public boolean isDirectory() {
        FileSystem fileSystem = null;
        try {
            fileSystem = borrowFileSystem();
            return fileSystem.getFileStatus(new Path(getResourceURI())).isDirectory();
        } catch (IOException e) {
            return false;
        } finally {
            if (null != fileSystem) {
                try {
                    returnFileSystem(getResourceURI(), fileSystem);
                } catch (SwiftFileException e) {
                    LOGGER.error(e);
                }
            }
        }
    }

    @Override
    public InputStream toStream() throws SwiftFileException {
        FileSystem fileSystem = borrowFileSystem();
        try {
            return fileSystem.open(new Path(getResourceURI()));
        } catch (IOException e) {
            throw new SwiftFileException(e);
        } finally {
            returnFileSystem(getResourceURI(), fileSystem);
        }
    }

    @Override
    public String getResourceName() {
        return new Path(getResourceURI()).getName();
    }

    @Override
    public void mkdirs() {
        FileSystem fileSystem = null;
        try {
            fileSystem = borrowFileSystem();
            fileSystem.mkdirs(new Path(getResourceURI()));
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            if (null != fileSystem) {
                try {
                    returnFileSystem(getResourceURI(), fileSystem);
                } catch (SwiftFileException e) {
                    LOGGER.error(e);
                }
            }
        }
    }

    @Override
    public void close() throws SwiftFileException {
        try {
            keyedObjectPool.clear();
        } catch (Exception e) {
            throw new SwiftFileException(e);
        }
    }

    @Override
    public void testConnection() {

    }

    @Override
    protected SwiftFileSystem[] list() {
        FileSystem fileSystem = null;
        try {
            fileSystem = borrowFileSystem();
            FileStatus[] statuses = fileSystem.listStatus(new Path(getResourceURI()));
            SwiftFileSystem[] fileSystems = new SwiftFileSystem[statuses.length];
            for (int i = 0; i < statuses.length; i++) {
                fileSystems[i] = systemPool.createFileSystem(config, statuses[i].getPath().toUri().getPath());
            }
            return fileSystems;
        } catch (IOException e) {
            LOGGER.error(e);
            return new SwiftFileSystem[0];
        } finally {
            if (null != fileSystem) {
                try {
                    returnFileSystem(getResourceURI(), fileSystem);
                } catch (SwiftFileException e) {
                    LOGGER.error(e);
                }
            }
        }
    }

    @Override
    protected long fileSize() {
        FileSystem fileSystem = null;
        try {
            fileSystem = borrowFileSystem();
            FileStatus status = fileSystem.getFileStatus(new Path(getResourceURI()));
            return status.getLen();
        } catch (IOException e) {
            LOGGER.error(e);
            return 0;
        } finally {
            if (null != fileSystem) {
                try {
                    returnFileSystem(getResourceURI(), fileSystem);
                } catch (SwiftFileException e) {
                    LOGGER.error(e);
                }
            }
        }
    }
}
