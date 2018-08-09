package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.AbstractConnector;
import com.fr.swift.util.Strings;
import com.fr.third.org.apache.commons.io.FileUtils;
import com.fr.third.org.apache.commons.io.IOUtils;
import net.jpountz.lz4.LZ4FrameInputStream;
import net.jpountz.lz4.LZ4FrameOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * @author yee
 * @date 2018/8/9
 */
public class Lz4Connector extends AbstractConnector {
    //    private static final int BLOCK_SIZE = 32 * 1024 * 1024;
//    private int compressedLevel = 7;
    private URI parentURI;

    private Lz4Connector(String path) {
        initParentPath(path);
    }

    public static Lz4Connector newInstance(String path) {
        return new Lz4Connector(path);
    }

    private void initParentPath(String path) {
        path = Strings.trimSeparator(path, "\\", "/");
        path = "/" + path + "/";
        path = Strings.trimSeparator(path, "/");
        parentURI = URI.create(path);
    }


    @Override
    public InputStream read(FileBlock block) throws IOException {
//        LZ4FastDecompressor decompressor = LZ4Factory.fastestInstance().fastDecompressor();
//        LZ4BlockInputStream zis = new LZ4BlockInputStream(is, decompressor);
        FileInputStream is = new FileInputStream(getPath(block, false));
        return new LZ4FrameInputStream(is);
    }

    @Override
    public void write(FileBlock block, InputStream is)
            throws IOException {
//        LZ4Compressor compressor = LZ4Factory.nativeInstance().fastCompressor();
//        if (compressedLevel > 17 || compressedLevel <=0) {
//            compressor = LZ4Factory.fastestInstance().highCompressor(7);
//        } else {
//            compressor = LZ4Factory.fastestInstance().highCompressor(compressedLevel);
//        }
        FileOutputStream fos = new FileOutputStream(getPath(block, true));
        BufferedOutputStream bos = new BufferedOutputStream(fos);
//        LZ4BlockOutputStream zos = new LZ4BlockOutputStream(bos, BLOCK_SIZE, compressor);
        LZ4FrameOutputStream zos = new LZ4FrameOutputStream(bos);
        IOUtils.copy(is, zos);
        zos.flush();
        if (is != null) {
            is.close();
        }
        if (null != bos) {
            bos.close();
        }
        if (fos != null) {
            fos.close();
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

    private File getFolderPath(FileBlock block) {
        return new File(parentURI.resolve(block.getParentUri()).getPath());
    }

    private File getPath(FileBlock block, boolean mkdir) {
        File parent = this.getFolderPath(block);
        if (!parent.exists() && mkdir) {
            parent.mkdirs();
        }

        return new File(parent, block.getFileName());
    }
}
