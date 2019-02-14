package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.io.file.FileBlock;
import com.fr.swift.util.IoUtil;
import com.fr.third.net.jpountz.lz4.LZ4BlockInputStream;
import com.fr.third.net.jpountz.lz4.LZ4BlockOutputStream;
import com.fr.third.net.jpountz.lz4.LZ4Compressor;
import com.fr.third.net.jpountz.lz4.LZ4Factory;
import com.fr.third.net.jpountz.lz4.LZ4FastDecompressor;
import com.fr.third.org.apache.commons.io.FileUtils;
import com.fr.third.org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2018/8/9
 */
public class Lz4Connector extends BaseConnector {
    private static final int BLOCK_SIZE = 32 * 1024 * 1024;

    private Lz4Connector(String path) {
        super(path);
    }

    public static Lz4Connector newInstance(String path) {
        return new Lz4Connector(path);
    }

    @Override
    public InputStream read(FileBlock block) throws IOException {
        FileInputStream is = new FileInputStream(getPath(block, false));
        LZ4FastDecompressor decompressor = LZ4Factory.fastestInstance().fastDecompressor();
        return new LZ4BlockInputStream(is, decompressor);
    }

    @Override
    public void write(FileBlock block, InputStream is)
            throws IOException {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            LZ4Compressor compressor = LZ4Factory.nativeInstance().fastCompressor();
            fos = new FileOutputStream(getPath(block, true));
            bos = new BufferedOutputStream(fos);
            LZ4BlockOutputStream zos = new LZ4BlockOutputStream(bos, BLOCK_SIZE, compressor);
            IOUtils.copy(is, zos);
            zos.finish();
        } finally {
            IoUtil.close(is, bos, fos);
        }
    }

    @Override
    public boolean delete(FileBlock block) {
        return this.getPath(block, false).delete();
    }

    @Override
    public boolean exists(FileBlock block) {
        File file = this.getPath(block, false);
        return file.exists() && file.length() > 0L;
    }

    @Override
    public boolean copy(FileBlock src, FileBlock dest) throws IOException {
        if (this.exists(src) && !this.exists(dest)) {
            File srcFile = this.getPath(src, false);
            File destFile = this.getPath(dest, false);
            FileUtils.copyFile(srcFile, destFile);
            return true;
        } else {
            return false;
        }
    }

    private File getPath(FileBlock block, boolean mkdir) {
        File parent = this.getFolderPath(block);
        if (!parent.exists() && mkdir) {
            parent.mkdirs();
        }

        return new File(parent, block.getFileName());
    }
}
