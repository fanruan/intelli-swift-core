package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.AbstractConnector;
import com.fineio.storage.Connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * 最简单的Connector，直接使用FileInputStream读，使用FileOutputStream写
 */
public class FileConnector extends AbstractConnector {
    private FileConnector() {
    }

    public static Connector newInstance() {
        return new FileConnector();
    }

    private static File toFile(FileBlock block, boolean mkdirs) {
        File dir = new File(block.getParentUri().toString());
        if (mkdirs) {
            dir.mkdirs();
        }
        return new File(dir, block.getFileName());
    }

    @Override
    public InputStream read(FileBlock block) throws IOException {
        File f = toFile(block, false);
        return new FileInputStream(f);
    }

    @Override
    public void write(FileBlock block, InputStream is) throws IOException {
        File f = toFile(block, true);
        FileOutputStream fos = new FileOutputStream(f);
        byte[] bytes = new byte[1024];
        for (int len; (len = is.read(bytes)) != -1; ) {
            fos.write(bytes, 0, len);
        }
        fos.close();
        is.close();
    }

    @Override
    public boolean delete(FileBlock block) {
        File f = toFile(block, false);
        return f.delete();
    }

    @Override
    public boolean exists(FileBlock block) {
        File f = toFile(block, false);
        return f.exists() && f.length() > 0;
    }

    @Override
    public boolean copy(FileBlock srcBlock, FileBlock destBlock) throws IOException {
        if (!exists(srcBlock) || exists(destBlock)) {
            return false;
        }
        write(destBlock, read(srcBlock));
        return true;
    }

}
