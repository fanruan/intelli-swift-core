package com.fr.swift.file.system.impl;

import com.fr.swift.file.CloudOssUtils;
import com.fr.swift.file.OssClientPool;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.AbstractFileSystem;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.factory.SwiftFileSystemFactory;
import com.fr.swift.repository.config.OssRepositoryConfig;
import com.fr.swift.repository.utils.SwiftRepositoryUtils;

import java.io.InputStream;
import java.util.List;

/**
 * @author yee
 * @date 2019-01-21
 */
public class OssFileSystemImpl extends AbstractFileSystem<OssRepositoryConfig> {
    private OssClientPool clientPool;
    private SwiftFileSystemFactory<OssFileSystemImpl, OssRepositoryConfig> systemFactory;

    public OssFileSystemImpl(OssRepositoryConfig config, String uri, OssClientPool clientPool, SwiftFileSystemFactory<OssFileSystemImpl, OssRepositoryConfig> systemFactory) {
        super(config, uri);
        this.clientPool = clientPool;
        this.systemFactory = systemFactory;
    }

    @Override
    protected SwiftFileSystem[] list() throws SwiftFileException {
        try {
            List<String> names = CloudOssUtils.listNames(clientPool, getResourceURI());
            SwiftFileSystem[] result = new SwiftFileSystem[names.size()];
            for (int i = 0; i < names.size(); i++) {
                result[i] = systemFactory.createFileSystem(config, names.get(i));
            }
        } catch (Exception e) {
            throw new SwiftFileException(e);
        }
        return new SwiftFileSystem[0];
    }

    @Override
    protected long fileSize() {
        return 0;
    }

    @Override
    public void write(String remote, InputStream inputStream) throws SwiftFileException {
        try {
            CloudOssUtils.upload(clientPool, remote, inputStream);
        } catch (Exception e) {
            throw new SwiftFileException(e);
        }
    }

    @Override
    public SwiftFileSystem read(String remote) throws SwiftFileException {
        return null;
    }

    @Override
    public SwiftFileSystem parent() {
        String parent = SwiftRepositoryUtils.getParent(getResourceURI());
        return systemFactory.createFileSystem(config, parent);
    }

    @Override
    public boolean remove(String remote) throws SwiftFileException {
        try {
            CloudOssUtils.delete(clientPool, remote);
            return true;
        } catch (Exception e) {
            throw new SwiftFileException(e);
        }
    }

    @Override
    public boolean renameTo(String src, String dest) throws SwiftFileException {
        throw new SwiftFileException(new UnsupportedOperationException("renameTo is unsupported"));
    }

    @Override
    public boolean copy(String src, String dest) throws SwiftFileException {
        try {
            return CloudOssUtils.copy(clientPool, src, dest);
        } catch (Exception e) {
            throw new SwiftFileException(e);
        }
    }

    @Override
    public boolean isExists() {
        try {
            return CloudOssUtils.exists(clientPool, getResourceURI());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * TODO 这个方法这么判断有问题，但是OSS没有文件夹的概念，待优化
     *
     * @return
     */
    @Override
    public boolean isDirectory() {
        return getResourceURI().endsWith("/");
    }

    @Override
    public InputStream toStream() throws SwiftFileException {
        try {
            return CloudOssUtils.getObjectStream(clientPool, getResourceURI());
        } catch (Exception e) {
            throw new SwiftFileException(e);
        }
    }

    @Override
    public String getResourceName() {
        return SwiftRepositoryUtils.getName(getResourceURI());
    }

    @Override
    public void mkdirs() {

    }

    @Override
    public void close() throws SwiftFileException {
        clientPool.close();
    }

    @Override
    public void testConnection() throws Exception {
    }
}
