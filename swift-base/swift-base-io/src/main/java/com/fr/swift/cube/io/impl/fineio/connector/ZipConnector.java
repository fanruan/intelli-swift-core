package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.io.file.FileBlock;
import com.fr.swift.util.IoUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author yee
 * @date 2018/5/14
 * @description
 */
public class ZipConnector extends BaseConnector {
    private ZipConnector(String path) {
        super(path);
    }

    public static ZipConnector newInstance(String path) {
        return new ZipConnector(path);
    }

    @Override
    public InputStream read(FileBlock block) throws IOException {
        FileInputStream fis = new FileInputStream(this.getPath(block, false));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];

        int len;
        while ((len = fis.read(bytes, 0, 1024)) > 0) {
            bos.write(bytes, 0, len);
        }

        byte[] data = this.decompress(bos.toByteArray());
        if (fis != null) {
            fis.close();
        }

        return new ByteArrayInputStream(data);
    }

    @Override
    public void write(FileBlock block, InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileOutputStream fos = null;
        try {
            byte[] bytes = new byte[1024];

            int len;
            while ((len = is.read(bytes, 0, 1024)) > 0) {
                bos.write(bytes, 0, len);
            }

            byte[] data = this.compress(bos.toByteArray());
            fos = new FileOutputStream(this.getPath(block, true));
            fos.write(data);
        } finally {
            IoUtil.close(is, fos, bos);
        }
    }

    public byte[] decompress(byte[] ready4Decompress) {
        byte[] data;
        Inflater inflater = new Inflater();
        inflater.reset();
        inflater.setInput(ready4Decompress);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(ready4Decompress.length);

        try {
            byte[] tmp = new byte[1024];

            while (!inflater.finished()) {
                int len = inflater.inflate(tmp);
                bos.write(tmp, 0, len);
            }

            data = bos.toByteArray();
        } catch (Exception e) {
            data = ready4Decompress;
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
            }
        }
        inflater.end();
        return data;
    }

    public byte[] compress(byte[] ready4Compress) {
        byte[] data;
        Deflater deflater = new Deflater();
        deflater.reset();
        deflater.setInput(ready4Compress);
        deflater.setLevel(6);
        deflater.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(ready4Compress.length);

        try {
            byte[] buffer = new byte[1024];

            while (!deflater.finished()) {
                int len = deflater.deflate(buffer);
                bos.write(buffer, 0, len);
            }

            data = bos.toByteArray();
        } catch (Exception e) {
            data = ready4Compress;
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
            }

        }

        deflater.end();
        return data;
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
//        if (this.exists(src) && !this.exists(dest)) {
//            File srcFile = this.getPath(src, false);
//            File destFile = this.getPath(dest, false);
//            FileUtils.copyFile(srcFile, destFile);
//            return true;
//        } else {
//            return false;
//        }
        return false;
    }

    private File getPath(FileBlock block, boolean mkdir) {
        File parent = this.getFolderPath(block);
        if (!parent.exists() && mkdir) {
            parent.mkdirs();
        }

        return new File(parent, block.getFileName());
    }
}