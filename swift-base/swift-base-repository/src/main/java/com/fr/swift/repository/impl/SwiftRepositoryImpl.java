package com.fr.swift.repository.impl;

import com.fineio.accessor.Block;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.connector.PackageManager;
import com.fineio.v3.connector.ZipPackageManager;
import com.fineio.v3.file.DirectoryBlock;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.impl.fineio.connector.SwiftConnectorCreator;
import com.fr.swift.repository.SwiftRepository;
import com.fr.swift.repository.exception.RepoNotFoundException;
import com.fr.swift.util.Crasher;

import java.io.IOException;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftRepositoryImpl implements SwiftRepository {

    private PackageManager packageManager;

    public SwiftRepositoryImpl(FineIOConnectorConfig config) {
        try {
            Connector connector = ConnectorManager.getInstance().getConnector();
            packageManager = new ZipPackageManager((com.fineio.storage.v3.Connector) connector,
                    SwiftConnectorCreator.createPackConnector(config));
        } catch (Crasher.CrashException e) {
            throw new RepoNotFoundException(e.getMessage());
        }
    }

    @Override
    public String copyFromRemote(String remote, String local) throws IOException {
        Block block = packageManager.getPackageConnector().list(remote);
        copyFromRemote(block, local, packageManager);
        return local;
    }

    private void copyFromRemote(Block block, String unPackPath, PackageManager packageManager) throws IOException {
        if (block instanceof FileBlock) {
            packageManager.unPackageDir(unPackPath, block.getPath());
        } else {
            for (Block file : ((DirectoryBlock) block).getFiles()) {
                copyFromRemote(file, unPackPath, packageManager);
            }
        }
    }

    @Override
    public boolean copyToRemote(String local, String remote) throws IOException {
        Connector connector = ConnectorManager.getInstance().getConnector();
        Block block = ((com.fineio.storage.v3.Connector) connector).list(local);
        if (block instanceof DirectoryBlock) {
            for (Block file : ((DirectoryBlock) block).getFiles()) {
                if (file instanceof DirectoryBlock) {
                    packageManager.packageDir(remote + "/" + file.getName(), file.getPath());
                } else {
                    packageManager.getPackageConnector().write(remote, connector.read((FileBlock) file));
                }
            }
        }
        return true;
    }

    @Override
    public boolean zipToRemote(String local, String remote) throws IOException {
        Connector connector = ConnectorManager.getInstance().getConnector();
        Block list = ((com.fineio.storage.v3.Connector) connector).list(local);
        packageManager.packageDir(remote, list.getPath());
        return true;
    }

    @Override
    public boolean delete(String remote) throws IOException {
        return packageManager.getPackageConnector().delete(remote);
    }

    @Override
    public long getSize(String path) throws IOException {
        return packageManager.getPackageConnector().size(path);
    }

    @Override
    public boolean exists(String path) {
        return packageManager.getPackageConnector().exist(path);
    }
}
