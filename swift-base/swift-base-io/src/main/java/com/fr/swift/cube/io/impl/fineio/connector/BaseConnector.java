package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.accessor.Block;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.AbstractConnector;
import com.fineio.storage.v3.Connector;
import com.fr.swift.util.Strings;

import java.io.File;
import java.io.IOException;

/**
 * @author anchore
 * @date 2018/8/17
 */
public abstract class BaseConnector extends AbstractConnector implements Connector {
    String parentPath;

    public BaseConnector(String path) {
        initParentPath(path);
    }

    private void initParentPath(String path) {
        parentPath = Strings.unifySlash("/" + path + "/");
    }

    public File getFolderPath(FileBlock block) {
        String path = Strings.unifySlash(block.getParentUri().getPath());
        return new File(path);
    }

    @Override
    public boolean copy(FileBlock srcBlock, FileBlock destBlock) throws IOException {
        if (!exists(srcBlock) || exists(destBlock)) {
            return false;
        }
        write(destBlock, read(srcBlock));
        return true;
    }

    @Override
    public boolean delete(FileBlock fileBlock) {
        return delete((Block) fileBlock);
    }

    @Override
    public boolean exists(FileBlock fileBlock) {
        return exists((Block) fileBlock);
    }
}