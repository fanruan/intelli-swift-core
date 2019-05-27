package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.accessor.Block;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.connector.PackageConnector;
import com.fr.swift.repository.utils.SwiftRepositoryUtils;

import java.io.IOException;
import java.io.InputStream;

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
        return null;
    }

    @Override
    public String getSuffix() {
        return ".cubes";
    }

    @Override
    public boolean delete(String dir) {
        return false;
    }

    @Override
    public long size(String dir) {
        return 0;
    }

    @Override
    public boolean exist(String dir) {
        return false;
    }
}
