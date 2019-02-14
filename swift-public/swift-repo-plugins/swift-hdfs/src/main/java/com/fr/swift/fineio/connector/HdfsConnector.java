package com.fr.swift.fineio.connector;

import com.fineio.io.file.FileBlock;
import com.fr.swift.cube.io.impl.fineio.connector.BaseConnector;
import com.fr.swift.file.exception.SwiftFileException;
import com.fr.swift.file.system.SwiftFileSystem;
import com.fr.swift.file.system.pool.HdfsFileSystemPool;
import com.fr.swift.repository.config.HdfsRepositoryConfig;
import com.fr.swift.util.Strings;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2018-12-17
 */
public class HdfsConnector extends BaseConnector {
    private HdfsFileSystemPool pool;

    public HdfsConnector(HdfsRepositoryConfig config) {
        super(Strings.EMPTY);
        this.pool = new HdfsFileSystemPool(config);
    }

    @Override
    public InputStream read(FileBlock file) throws IOException {
        String path = getPath(file, false);
        SwiftFileSystem fileSystem = pool.borrowObject(path);
        try {
            return fileSystem.toStream();
        } finally {
            pool.returnObject(path, fileSystem);
        }
    }

    @Override
    public void write(FileBlock file, InputStream inputStream) throws IOException {
        String path = getPath(file, true);
        SwiftFileSystem fileSystem = pool.borrowObject(path);
        try {
            fileSystem.write(inputStream);
        } finally {
            pool.returnObject(path, fileSystem);
        }
    }

    @Override
    public boolean delete(FileBlock block) {
        String path = getPath(block, false);
        SwiftFileSystem fileSystem = pool.borrowObject(path);
        try {
            return fileSystem.remove();
        } catch (SwiftFileException e) {
            return false;
        } finally {
            pool.returnObject(path, fileSystem);
        }
    }

    @Override
    public boolean exists(FileBlock block) {
        String path = getPath(block, false);
        SwiftFileSystem fileSystem = pool.borrowObject(path);
        try {
            return fileSystem.isExists();
        } finally {
            pool.returnObject(path, fileSystem);
        }
    }

    @Override
    public boolean copy(FileBlock srcBlock, FileBlock destBlock) throws IOException {
        String src = getPath(srcBlock, false);
        SwiftFileSystem fileSystem = pool.borrowObject(src);
        try {
            return fileSystem.copy(getPath(destBlock, true));
        } catch (SwiftFileException e) {
            return false;
        } finally {
            pool.returnObject(src, fileSystem);
        }
    }

    private String getPath(FileBlock fileBlock, boolean mkdir) {
        String path = getFolderPath(fileBlock).getPath();
        SwiftFileSystem fileSystem = pool.borrowObject(path);
        try {
            if (mkdir && !fileSystem.isExists()) {
                fileSystem.mkdirs();
            }
            return path + "/" + fileBlock.getFileName();
        } finally {
            pool.returnObject(path, fileSystem);
        }
    }
}
