package com.fr.swift.repository.impl;

import com.fineio.accessor.Block;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.connector.PackageManager;
import com.fineio.v3.connector.ZipPackageManager;
import com.fineio.v3.file.DirectoryBlock;
import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.impl.fineio.connector.SwiftConnectorCreator;
import com.fr.swift.repository.SwiftRepository;

import java.io.IOException;

/**
 * @author yee
 * @date 2018/5/28
 */
public class SwiftRepositoryImpl implements SwiftRepository {

    private PackageManager packageManager;

    public SwiftRepositoryImpl(FineIOConnectorConfig config) {
        String path = SwiftContext.get().getBean(SwiftCubePathService.class).getSwiftPath();
        packageManager = new ZipPackageManager(ConnectorManager.getInstance().getConnector(),
                SwiftConnectorCreator.create(config, path));
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
        Block block = connector.list(local);
        if (block instanceof DirectoryBlock) {
            for (Block file : ((DirectoryBlock) block).getFiles()) {
                if (file instanceof DirectoryBlock) {
                    packageManager.packageDir(file.getPath());
                } else {
                    packageManager.getPackageConnector().write(file.getPath(), connector.read((FileBlock) file));
                }
            }
        }
        return true;
    }

    @Override
    public boolean zipToRemote(String local, String remote) throws IOException {
        Connector connector = ConnectorManager.getInstance().getConnector();
        Block list = connector.list(local);
        packageManager.packageDir(list.getPath());
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
