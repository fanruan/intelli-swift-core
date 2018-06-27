package com.fr.swift.file.system.impl;

import com.fr.swift.file.conf.impl.HdfsRepositoryConfigImpl;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.AbstractFileSystem;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @date 2018/5/28
 */
public class HdfsFileSystemImpl extends AbstractFileSystem<HdfsRepositoryConfigImpl> {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(HdfsFileSystemImpl.class);

//    private FileSystem fileSystem;

    public HdfsFileSystemImpl(HdfsRepositoryConfigImpl config, URI uri) {
        super(config, uri);
//        fileSystem = getHdfsFileSystem(uri);
    }

    @Override
    public void write(URI remote, InputStream inputStream) {

    }

//    private FileSystem getHdfsFileSystem(URI uri) {
//        if (null == fileSystem) {
//            uri = uri == null ? getResourceURI() : uri;
//            Configuration conf = new Configuration();
//            conf.set(config.getFsName(), URI.create(config.getFullAddress()).resolve(uri).getPath());
//            try {
//                return FileSystem.get(conf);
//            } catch (IOException e) {
//                return Crasher.crash("Can not switch to hdfs! ", e);
//            }
//        }
//        return fileSystem;
//    }

//    private FileSystem getHdfsFileSystem() {
//        return getHdfsFileSystem(getResourceURI());
//    }

    @Override
    public SwiftFileSystem read(URI remote) throws SwiftFileException {
//        SwiftFileSystem fileSystem;
//        if (ComparatorUtils.equals(remote, getResourceURI())) {
//            fileSystem = this;
//        } else {
//            fileSystem = new HdfsFileSystemImpl(getConfig(), remote);
//        }
//        if (fileSystem.isExists()) {
//            return fileSystem;
//        }
        throw new SwiftFileException(String.format("File path '%s' not exists!", remote.getPath()));
    }

    @Override
    public boolean remove(URI remote) {
//        try {
//            return getHdfsFileSystem().deleteOnExit(new Path(remote.getPath()));
//        } catch (IOException e) {
//            throw new SwiftFileException(e);
//        }
        return true;
    }

    @Override
    public boolean renameTo(URI src, URI dest) {
//        try {
//            return getHdfsFileSystem().rename(new Path(src.getPath()), new Path(dest.getPath()));
//        } catch (IOException e) {
//            throw new SwiftFileException(e);
//        }
        return true;
    }

    @Override
    public boolean copy(URI src, URI dest) {
//        try {
//            if (ComparatorUtils.equals(src, getResourceURI())) {
//                FileStatus fileStatus = getHdfsFileSystem().getFileStatus(new Path(src.getPath()));
//                if (fileStatus.isDirectory()) {
//                    FileStatus[] children = getHdfsFileSystem().listStatus(new Path(src.getPath()));
//                    boolean mkdir = getHdfsFileSystem().mkdirs(new Path(dest.getPath()));
//                    if (mkdir) {
//                        for (FileStatus child : children) {
//                            new HdfsFileSystemImpl(getConfig(), child.getPath().toUri()).copy(dest.resolve(child.getPath().getName()));
//                        }
//                    }
//                } else if (fileStatus.isFile()) {
//                    FSDataOutputStream dos = getHdfsFileSystem().create(new Path(dest));
//                    FSDataInputStream dis = getHdfsFileSystem().open(new Path(src.getPath()));
//                    int len = 0;
//                    byte[] bytes = new byte[1024];
//                    while ((len = dis.read(bytes, 0, 1024)) != -1) {
//                        dos.write(bytes, 0, len);
//                    }
//                    dis.close();
//                    dos.close();
//                }
//                return true;
//            } else {
//                return new HdfsFileSystemImpl(getConfig(), src).copy(dest);
//            }
//        } catch (IOException e) {
//            throw new SwiftFileException(e);
//        }
        return true;
    }

    @Override
    public boolean isExists() {
//        try {
//            return getHdfsFileSystem().exists(new Path(getResourceURI().getPath()));
//        } catch (IOException e) {
//            return false;
//        }
        return true;
    }

    @Override
    public boolean isDirectory() {
//        try {
//            return getHdfsFileSystem().getFileStatus(new Path(getResourceURI().getPath())).isDirectory();
//        } catch (IOException e) {
//            return false;
//        }
        return false;
    }

    @Override
    public InputStream toStream() {
//        try {
//            return getHdfsFileSystem().open(new Path(getResourceURI().getPath()));
//        } catch (IOException e) {
//            throw new SwiftFileException(e);
//        }
        return null;
    }

    @Override
    public String getResourceName() {
//        return new Path(getResourceURI().getPath()).getName();
        return "";
    }

    @Override
    public void mkdirs() {
//        try {
//            getHdfsFileSystem().mkdirs(new Path(getResourceURI().getPath()));
//        } catch (IOException e) {
//            LOGGER.error(e);
//        }
    }

    @Override
    public void close() {
//        try {
//            getHdfsFileSystem().close();
//        } catch (IOException e) {
//            throw new SwiftFileException(e);
//        }
    }

    @Override
    protected SwiftFileSystem[] list() {
//        try {
//            FileStatus[] statuses = getHdfsFileSystem().listStatus(new Path(getResourceURI().getPath()));
//            SwiftFileSystem[] fileSystems = new SwiftFileSystem[statuses.length];
//            for (int i = 0; i < statuses.length; i++) {
//                fileSystems[i] = new HdfsFileSystemImpl(getConfig(), statuses[i].getPath().toUri());
//            }
//            return fileSystems;
//        } catch (IOException e) {
//            LOGGER.error(e);
//        }
        return new SwiftFileSystem[0];
    }
}
