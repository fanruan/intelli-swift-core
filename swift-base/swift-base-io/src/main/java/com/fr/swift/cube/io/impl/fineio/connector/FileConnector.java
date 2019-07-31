package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.accessor.Block;
import com.fineio.io.file.FileBlock;
import com.fineio.storage.Connector;
import com.fineio.v3.file.DirectoryBlock;
import com.fr.swift.log.SwiftLoggers;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * 最简单的Connector，直接使用FileInputStream读，使用FileOutputStream写
 */
public class FileConnector extends BaseConnector {

    FileConnector(String path) {
        super(path);
    }

    public static Connector newInstance(String path) {
        return new FileConnector(path);
    }

    private File toFile(String path, String name, boolean mkdirs) {
        File dir = new File(parentPath + "/" + path);
        if (mkdirs) {
            dir.mkdirs();
        }
        return new File(dir, name);
    }

    @Override
    public InputStream read(FileBlock block) throws IOException {
        File f = toFile(block.getParentUri().getPath(), block.getFileName(), false);
        return new FileInputStream(f);
    }

    @Override
    public void write(FileBlock block, InputStream is) throws IOException {
        File f = toFile(block.getParentUri().getPath(), block.getFileName(), true);
        try (FileOutputStream fos = new FileOutputStream(f)) {
            byte[] bytes = new byte[1024];
            for (int len; (len = is.read(bytes)) != -1; ) {
                fos.write(bytes, 0, len);
            }
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
            throw e;
        }
    }

    @Override
    public boolean delete(Block block) {
        File f = toFile(block.getPath(), block.getName(), false);
        return f.delete();
    }

    @Override
    public boolean exists(Block block) {
        File f = toFile(block.getPath(), block.getName(), false);
        return f.exists() && f.length() > 0;
    }

    @Override
    public Block list(String dir) {
        File f = new File(parentPath + "/" + dir);
        if (f.isDirectory()) {
            List<Block> blocks = new ArrayList<Block>();
            File[] list = f.listFiles(new FileFilter() {
                @Override
                public boolean accept(File name) {
                    return !(".".equals(name.getName()) || "..".equals(name.getName()));
                }
            });
            if (null != list) {
                for (File s : list) {
                    String path = s.getAbsolutePath().replaceAll(parentPath, "");
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    blocks.add(list(path));
                }
            }
            return new DirectoryBlock(dir, blocks);
        } else {
            String path = f.getParent().replaceAll(parentPath, "");
            path = path.startsWith("/") ? path.substring(1) : path;
            return new FileBlock(path, f.getName());
        }
    }

    @Override
    public long size(Block block) {
        long size = 0;
        if (block instanceof FileBlock) {
            size += new File(block.getPath()).length();
        } else {
            for (Block file : ((DirectoryBlock) block).getFiles()) {
                size += size(file);
            }
        }
        return size;
    }
}
