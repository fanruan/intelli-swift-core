package com.fr.swift.file.system.impl;

import com.fr.general.ComparatorUtils;
import com.fr.io.utils.ResourceIOUtils;
import com.fr.swift.config.bean.HdfsRepositoryConfigBean;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.AbstractFileSystem;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.pool.BaseRemoteSystemPool;
import com.fr.swift.file.system.pool.RemotePoolCreator;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.org.apache.commons.pool2.KeyedObjectPool;

import java.io.InputStream;
import java.net.URI;

//import org.apache.hadoop.fs.FSDataInputStream;
//import org.apache.hadoop.fs.FSDataOutputStream;
//import org.apache.hadoop.fs.FileStatus;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.io.IOUtils;
//import java.io.IOException;
//import java.io.OutputStream;

/**
 * @author yee
 * @date 2018/5/28
 */
public class HdfsFileSystemImpl extends AbstractFileSystem<HdfsRepositoryConfigBean> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(HdfsFileSystemImpl.class);

    //    private KeyedObjectPool<URI, FileSystem> keyedObjectPool;
    private BaseRemoteSystemPool<HdfsFileSystemImpl> systemPool;

    public HdfsFileSystemImpl(final HdfsRepositoryConfigBean config, URI uri, KeyedObjectPool keyedObjectPool) {
        super(config, uri);
//        this.keyedObjectPool = keyedObjectPool;
        this.systemPool = (BaseRemoteSystemPool<HdfsFileSystemImpl>) RemotePoolCreator.creator().getPool(config);
    }

    @Override
    public void write(URI remote, InputStream is) {
//        FileSystem fileSystem = borrowFileSystem(remote);
//        try {
//            fileSystem.delete(new Path(remote.getPath()), true);
//            OutputStream os = fileSystem.create(new Path(remote.getPath()), true);
//            IOUtils.copyBytes(is, os, 2048, true);
//        } catch (IOException e) {
//            throw new SwiftFileException(e);
//        } finally {
//            returnFileSystem(remote, fileSystem);
//        }
    }

//    private FileSystem borrowFileSystem(URI uri) throws SwiftFileException {
//        try {
//            return keyedObjectPool.borrowObject(uri);
//        } catch (Exception e) {
//            throw new SwiftFileException(e);
//        }
//    }
//
//    private FileSystem borrowFileSystem() throws SwiftFileException {
//        return borrowFileSystem(getResourceURI());
//    }

//    private void returnFileSystem(URI uri, FileSystem fileSystem) throws SwiftFileException {
//        try {
//            keyedObjectPool.returnObject(uri, fileSystem);
//        } catch (Exception e) {
//            throw new SwiftFileException(e);
//        }
//    }

    @Override
    public SwiftFileSystem read(URI remote) throws SwiftFileException {
        SwiftFileSystem fileSystem;
        if (ComparatorUtils.equals(remote, getResourceURI())) {
            fileSystem = this;
        } else {
            fileSystem = systemPool.borrowObject(remote);
        }
        if (fileSystem.isExists()) {
            return fileSystem;
        }
        throw new SwiftFileException(String.format("File path '%s' not exists!", remote.getPath()));
    }

    @Override
    public SwiftFileSystem parent() {
        return systemPool.borrowObject(getParentURI());

    }

    @Override
    public boolean remove(URI remote) {
//        FileSystem fileSystem = borrowFileSystem(remote);
//        try {
//            return fileSystem.delete(new Path(remote.getPath()), true);
//        } catch (IOException e) {
//            throw new SwiftFileException(e);
//        } finally {
//            returnFileSystem(remote, fileSystem);
//        }
        return false;
    }

    @Override
    public boolean renameTo(URI src, URI dest) {
//        FileSystem fileSystem = borrowFileSystem(src);
//        try {
//            return fileSystem.rename(new Path(src.getPath()), new Path(dest.getPath()));
//        } catch (IOException e) {
//            throw new SwiftFileException(e);
//        } finally {
//            returnFileSystem(src, fileSystem);
//        }
        return false;
    }

    @Override
    public boolean copy(URI src, URI dest) {
//        FileSystem fileSystem = borrowFileSystem(src);
//        try {
//            FileStatus fileStatus = fileSystem.getFileStatus(new Path(src.getPath()));
//            if (fileStatus.isDirectory()) {
//                FileStatus[] children = fileSystem.listStatus(new Path(src.getPath()));
//                boolean mkdir = fileSystem.mkdirs(new Path(dest.getPath()));
//                if (mkdir) {
//                    for (FileStatus child : children) {
//                        systemPool.borrowObject(child.getPath().toUri()).copy(dest.resolve(child.getPath().getName()));
//                    }
//                }
//            } else if (fileStatus.isFile()) {
//                FSDataOutputStream dos = fileSystem.create(new Path(dest));
//                FSDataInputStream dis = fileSystem.open(new Path(src.getPath()));
//                IOUtils.copyBytes(dis, dos, 2048, true);
//            }
//            return true;
//        } catch (IOException e) {
//            throw new SwiftFileException(e);
//        } finally {
//            returnFileSystem(src, fileSystem);
//        }
        return false;
    }

    @Override
    public boolean isExists() {
//        FileSystem fileSystem = null;
//        try {
//            fileSystem = borrowFileSystem();
//            return fileSystem.exists(new Path(getResourceURI().getPath()));
//        } catch (IOException e) {
//            return false;
//        } finally {
//            if (null != fileSystem) {
//                try {
//                    returnFileSystem(getResourceURI(), fileSystem);
//                } catch (SwiftFileException e) {
//                    LOGGER.error(e);
//                }
//            }
//        }
        return false;
    }

    @Override
    public boolean isDirectory() {
//        FileSystem fileSystem = null;
//        try {
//            fileSystem = borrowFileSystem();
//            return fileSystem.getFileStatus(new Path(getResourceURI().getPath())).isDirectory();
//        } catch (IOException e) {
//            return false;
//        } finally {
//            if (null != fileSystem) {
//                try {
//                    returnFileSystem(getResourceURI(), fileSystem);
//                } catch (SwiftFileException e) {
//                    LOGGER.error(e);
//                }
//            }
//        }
        return false;
    }

    @Override
    public InputStream toStream() {
//        FileSystem fileSystem = borrowFileSystem();
//        try {
//            return fileSystem.open(new Path(getResourceURI().getPath()));
//        } catch (IOException e) {
//            throw new SwiftFileException(e);
//        } finally {
//            returnFileSystem(getResourceURI(), fileSystem);
//        }
        return null;
    }

    @Override
    public String getResourceName() {
//        return new Path(getResourceURI().getPath()).getName();
        return ResourceIOUtils.getName(getResourceURI().getPath());
    }

    @Override
    public void mkdirs() {
//        FileSystem fileSystem = null;
//        try {
//            fileSystem = borrowFileSystem();
//            fileSystem.mkdirs(new Path(getResourceURI().getPath()));
//        } catch (IOException e) {
//            LOGGER.error(e);
//        } finally {
//            if (null != fileSystem) {
//                try {
//                    returnFileSystem(getResourceURI(), fileSystem);
//                } catch (SwiftFileException e) {
//                    LOGGER.error(e);
//                }
//            }
//        }
    }

    @Override
    public void close() {
//        try {
//            keyedObjectPool.clear();
//        } catch (Exception e) {
//            throw new SwiftFileException(e);
//        }
    }

    @Override
    protected SwiftFileSystem[] list() {
//        FileSystem fileSystem = null;
//        try {
//            fileSystem = borrowFileSystem();
//            FileStatus[] statuses = fileSystem.listStatus(new Path(getResourceURI().getPath()));
//            SwiftFileSystem[] fileSystems = new SwiftFileSystem[statuses.length];
//            for (int i = 0; i < statuses.length; i++) {
//                fileSystems[i] = systemPool.borrowObject(statuses[i].getPath().toUri());
//            }
//            return fileSystems;
//        } catch (IOException e) {
//            LOGGER.error(e);
//            return new SwiftFileSystem[0];
//        } finally {
//            if (null != fileSystem) {
//                try {
//                    returnFileSystem(getResourceURI(), fileSystem);
//                } catch (SwiftFileException e) {
//                    LOGGER.error(e);
//                }
//            }
//        }
        return new SwiftFileSystem[0];
    }
}
