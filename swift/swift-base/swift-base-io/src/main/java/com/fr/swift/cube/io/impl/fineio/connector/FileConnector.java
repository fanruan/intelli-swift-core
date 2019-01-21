package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.IoUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * 最简单的Connector，直接使用FileInputStream读，使用FileOutputStream写
 */
public class FileConnector extends BaseConnector {

    private FileConnector(String path) {
        super(path);
    }

    public static Connector newInstance(String path) {
        return new FileConnector(path);
    }

    private File toFile(FileBlock block, boolean mkdirs) {
        File dir = new File(parentURI + "/" + block.getParentUri().getPath());
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
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            byte[] bytes = new byte[1024];
            for (int len; (len = is.read(bytes)) != -1; ) {
                fos.write(bytes, 0, len);
            }
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
            throw e;
        } finally {
            IoUtil.close(fos, is);
        }
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
}
