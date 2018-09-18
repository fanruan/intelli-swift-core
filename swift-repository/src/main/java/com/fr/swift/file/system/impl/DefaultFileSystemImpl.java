package com.fr.swift.file.system.impl;

import com.fr.general.ComparatorUtils;
import com.fr.io.utils.ResourceIOUtils;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.AbstractFileSystem;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.repository.SwiftFileSystemConfig;

import java.io.InputStream;

/**
 * @author yee
 * @date 2018/5/28
 */
public class DefaultFileSystemImpl extends AbstractFileSystem {

    public DefaultFileSystemImpl(SwiftFileSystemConfig config, String uri) {
        super(config, uri);
    }

    @Override
    protected SwiftFileSystem[] list() {
        String[] list = ResourceIOUtils.list(getResourceURI());
        SwiftFileSystem[] result = new SwiftFileSystem[list.length];
        for (int i = 0; i < list.length; i++) {
            result[i] = new DefaultFileSystemImpl(getConfig(), getResourceURI() + "/" + list[i]);
        }
        return result;
    }

    @Override
    protected long fileSize() {
        return ResourceIOUtils.getLength(getResourceURI());
    }


    @Override
    public void write(String remote, InputStream inputStream) throws SwiftFileException {
        try {
            ResourceIOUtils.write(remote, inputStream);
        } catch (Exception e) {
            throw new SwiftFileException(e);
        }
    }

    @Override
    public SwiftFileSystem read(String remote) throws SwiftFileException {
        SwiftFileSystem fileSystem;
        if (ComparatorUtils.equals(remote, getResourceURI())) {
            fileSystem = this;
        } else {
            fileSystem = new DefaultFileSystemImpl(getConfig(), remote);
        }
        if (fileSystem.isExists()) {
            return fileSystem;
        }
        throw new SwiftFileException(String.format("File path '%s' not exists!", remote));
    }

    @Override
    public SwiftFileSystem parent() {
        return new DefaultFileSystemImpl(getConfig(), getParentURI());
    }

    @Override
    public boolean remove(String remote) throws SwiftFileException {
        if (ComparatorUtils.equals(remote, getResourceURI())) {
            if (isExists()) {
                return ResourceIOUtils.delete(remote);
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
    public boolean renameTo(String src, String dest) throws SwiftFileException {
        SwiftFileSystem fileSystem = read(src);
        return ResourceIOUtils.renameTo(fileSystem.getResourceURI(), dest);
    }

    @Override
    public boolean copy(String src, String dest) throws SwiftFileException {
        SwiftFileSystem fileSystem = read(src);
        try {
            ResourceIOUtils.copy(fileSystem.getResourceURI(), dest);
            return true;
        } catch (Exception e) {
            throw new SwiftFileException(e);
        }
    }

    @Override
    public boolean isExists() {
        return ResourceIOUtils.exist(getResourceURI());
    }

    @Override
    public boolean isDirectory() {
        return ResourceIOUtils.isDirectory(getResourceURI());
    }

    @Override
    public InputStream toStream() {
        return ResourceIOUtils.read(getResourceURI());
    }

    @Override
    public String getResourceName() {
        return ResourceIOUtils.getName(getResourceURI());
    }

    @Override
    public void mkdirs() {
        ResourceIOUtils.createDirectory(getResourceURI());
    }

    @Override
    public void close() {

    }

}
