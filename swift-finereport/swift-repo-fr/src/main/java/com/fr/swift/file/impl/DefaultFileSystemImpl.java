package com.fr.swift.file.impl;

import com.fr.general.ComparatorUtils;
import com.fr.io.context.ResourceModuleContext;
import com.fr.io.repository.FineFileEntry;
import com.fr.io.utils.ResourceIOUtils;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.AbstractFileSystem;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.log.SwiftLoggers;

import java.io.InputStream;

/**
 * @author yee
 * @date 2018/5/28
 */
public class DefaultFileSystemImpl extends AbstractFileSystem {

    public DefaultFileSystemImpl(String uri) {
        super(null, uri);
    }

    @Override
    protected SwiftFileSystem[] list() {
        String[] list = ResourceIOUtils.list(getResourceURI());
        SwiftFileSystem[] result = new SwiftFileSystem[list.length];
        for (int i = 0; i < list.length; i++) {
            result[i] = new DefaultFileSystemImpl(getResourceURI() + "/" + list[i]);
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
            int available = inputStream.available();
            ResourceIOUtils.write(remote, inputStream);
            SwiftLoggers.getLogger().debug("ResourceIOUtils wrote {} bytes of {}", available, remote);
        } catch (Exception e) {
            SwiftLoggers.getLogger().debug("ResourceIOUtils wrote failed caused by {}", remote, e);
            throw new SwiftFileException(e);
        }
    }

    @Override
    public SwiftFileSystem read(String remote) throws SwiftFileException {
        SwiftLoggers.getLogger().debug("ResourceIOUtils read from remote {}", remote);
        SwiftFileSystem fileSystem;
        if (ComparatorUtils.equals(remote, getResourceURI())) {
            fileSystem = this;
        } else {
            fileSystem = new DefaultFileSystemImpl(remote);
        }
        if (fileSystem.isExists()) {
            return fileSystem;
        }
        throw new SwiftFileException(String.format("File path '%s' not exists!", remote));
    }

    @Override
    public SwiftFileSystem parent() {
        return new DefaultFileSystemImpl(getParentURI());
    }

    @Override
    public boolean remove(String remote) throws SwiftFileException {
        if (ComparatorUtils.equals(remote, getResourceURI())) {
            if (isExists()) {
                SwiftLoggers.getLogger().debug("ResourceIOUtils remove remote {}", remote);
                return ResourceIOUtils.delete(remote);
            }
        } else {
            SwiftFileSystem system = new DefaultFileSystemImpl(remote);
            if (system.isExists()) {
                return system.remove();
            }
        }
        return true;
    }

    @Override
    public boolean renameTo(String src, String dest) throws SwiftFileException {
        SwiftLoggers.getLogger().debug("ResourceIOUtils rename src {} to dest {}", src, dest);
        SwiftFileSystem fileSystem = read(src);
        return ResourceIOUtils.renameTo(fileSystem.getResourceURI(), dest);
    }

    @Override
    public boolean copy(String src, String dest) throws SwiftFileException {
        SwiftLoggers.getLogger().debug("ResourceIOUtils copy src {} to dest {}", src, dest);
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

    @Override
    public void testConnection() {

    }

    @Override
    public long getSize() {
        long size = 0;
        if (ResourceModuleContext.getRealCurrentRepo().isAccurateDiskSize()) {
            FineFileEntry[] entries = ResourceIOUtils.listEntry(getResourceURI());

            for (FineFileEntry entry : entries) {
                size += entry.getSize();
            }
        }
        return size;
    }
}
