package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.AbstractConnector;
import com.fr.swift.util.Strings;
import com.fr.third.org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author yee
 * @date 2018/5/14
 * @description
 */
public class ZipConnector extends AbstractConnector {

    private URI parentURI;

    private ZipConnector(String path) {
        initParentPath(path);
    }

    public static ZipConnector newInstance(String path) {
        return new ZipConnector(path);
    }

    private void initParentPath(String path) {
        path = Strings.trimSeparator(path, "\\", "/");
        path = "/" + path + "/";
        path = Strings.trimSeparator(path, "/");
        parentURI = URI.create(path);
    }

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

    public void write(FileBlock block, InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];

        int len;
        while ((len = is.read(bytes, 0, 1024)) > 0) {
            bos.write(bytes, 0, len);
        }

        byte[] data = this.compress(bos.toByteArray());
        FileOutputStream fos = new FileOutputStream(this.getPath(block, true));
        fos.write(data);
        if (is != null) {
            is.close();
        }

        if (fos != null) {
            fos.close();
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

    public boolean delete(FileBlock block) {
        return this.getPath(block, false).delete();
    }

    public boolean exists(FileBlock block) {
        File file = this.getPath(block, false);
        return file.exists() && file.length() > 0L;
    }

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
        String path = parentURI.resolve(block.getParentUri()).getPath();
        try {
            return new File(URLDecoder.decode(path, "utf8"));
        } catch (UnsupportedEncodingException e) {
            return new File(path);
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