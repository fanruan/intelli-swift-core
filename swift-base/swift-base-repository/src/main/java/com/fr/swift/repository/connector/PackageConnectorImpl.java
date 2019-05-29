package com.fr.swift.repository.connector;

import com.fineio.accessor.Block;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.connector.PackageConnector;
import com.fineio.v3.file.DirectoryBlock;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.utils.SwiftRepositoryUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

/**
 * @author yee
 * @date 2019-05-27
 */
public class PackageConnectorImpl implements PackageConnector {
    private Connector connector;

    public PackageConnectorImpl(Connector connector) {
        this.connector = connector;
    }

    @Override
    public void write(String path, InputStream is) throws IOException {
        String parent = SwiftRepositoryUtils.getParent(path);
        String name = SwiftRepositoryUtils.getName(path);
        connector.write(new FileBlock(parent, name + getSuffix()), is);
    }

    @Override
    public InputStream read(String path) throws IOException {
        String parent = SwiftRepositoryUtils.getParent(path);
        String name = SwiftRepositoryUtils.getName(path);
        return connector.read(new FileBlock(parent, name + getSuffix()));
    }

    @Override
    public Block list(String dir) {
        Block list = null;
        try {
            list = connector.list(dir);
        } catch (IOException e) {
            SwiftLoggers.getLogger().warn(e);
        }
        if (null == list) {
            return new FileBlock(SwiftRepositoryUtils.getParent(dir), SwiftRepositoryUtils.getName(dir) + getSuffix());
        }
        return list;
    }

    @Override
    public String getSuffix() {
        return ".cubes";
    }

    @Override
    public boolean delete(String dir) {
        return connector.delete(new DirectoryBlock(dir, Collections.<Block>emptyList()));
    }

    @Override
    public long size(String dir) {
        return connector.size(list(dir));
    }

    @Override
    public boolean exist(String dir) {
        return connector.exists(new DirectoryBlock(dir, Collections.<Block>emptyList()));
    }
}
