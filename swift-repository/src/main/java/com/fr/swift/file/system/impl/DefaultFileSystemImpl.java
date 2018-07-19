package com.fr.swift.file.system.impl;

import com.fr.general.ComparatorUtils;
import com.fr.io.utils.ResourceIOUtils;
import com.fr.swift.config.bean.SwiftFileSystemConfig;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.AbstractFileSystem;
import com.fr.swift.file.system.SwiftFileSystem;

import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @date 2018/5/28
 */
public class DefaultFileSystemImpl extends AbstractFileSystem {

    public DefaultFileSystemImpl(SwiftFileSystemConfig config, URI uri) {
        super(config, uri);
    }

    @Override
    protected SwiftFileSystem[] list() {
        String[] list = ResourceIOUtils.list(getResourceURI().getPath());
        SwiftFileSystem[] result = new SwiftFileSystem[list.length];
        for (int i = 0; i < list.length; i++) {
            result[i] = new DefaultFileSystemImpl(getConfig(), URI.create(list[i]));
        }
        return result;
    }

    @Override
    public void write(URI remote, InputStream inputStream) throws SwiftFileException {
        try {
            ResourceIOUtils.write(remote.getPath(), inputStream);
        } catch (Exception e) {
            throw new SwiftFileException(e);
        }
    }

    @Override
    public SwiftFileSystem read(URI remote) throws SwiftFileException {
        SwiftFileSystem fileSystem;
        if (ComparatorUtils.equals(remote, getResourceURI())) {
            fileSystem = this;
        } else {
            fileSystem = new DefaultFileSystemImpl(getConfig(), remote);
        }
        if (fileSystem.isExists()) {
            return fileSystem;
        }
        throw new SwiftFileException(String.format("File path '%s' not exists!", remote.getPath()));
    }

    @Override
    public SwiftFileSystem parent() {
        return new DefaultFileSystemImpl(getConfig(), getParentURI());
    }

    @Override
    public boolean remove(URI remote) throws SwiftFileException {
        if (ComparatorUtils.equals(remote, getResourceURI())) {
            if (isExists()) {
                return ResourceIOUtils.delete(remote.getPath());
            }
        } else {
            SwiftFileSystem system = new DefaultFileSystemImpl(getConfig(), remote);
            if (system.isExists()) {
                return system.remove();
            }
        }
        return true;
    }

    @Override
    public boolean renameTo(URI src, URI dest) throws SwiftFileException {
        SwiftFileSystem fileSystem = read(src);
        return ResourceIOUtils.renameTo(fileSystem.getResourceURI().getPath(), dest.getPath());
    }

    @Override
    public boolean copy(URI src, URI dest) throws SwiftFileException {
        SwiftFileSystem fileSystem = read(src);
        try {
            ResourceIOUtils.copy(fileSystem.getResourceURI().getPath(), dest.getPath());
            return true;
        } catch (Exception e) {
            throw new SwiftFileException(e);
        }
    }

    @Override
    public boolean isExists() {
        return ResourceIOUtils.exist(getResourceURI().getPath());
    }

    @Override
    public boolean isDirectory() {
        return ResourceIOUtils.isDirectory(getResourceURI().getPath());
    }

    @Override
    public InputStream toStream() {
        return ResourceIOUtils.read(getResourceURI().getPath());
    }

    @Override
    public String getResourceName() {
        return ResourceIOUtils.getName(getResourceURI().getPath());
    }

    @Override
    public void mkdirs() {
        ResourceIOUtils.createDirectory(getResourceURI().getPath());
    }

    @Override
    public void close() {

    }

}
