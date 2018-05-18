package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.AbstractConnector;
import com.fr.third.org.apache.commons.io.FileUtils;

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
public class ZipConnector extends AbstractConnector {

    private ZipConnector() {
    }

    public static ZipConnector newInstance() {
        return new ZipConnector();
    }

    public InputStream read(FileBlock block) throws IOException {
        FileInputStream fis = new FileInputStream(this.getPathForRead(block));
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
        byte[] var6 = new byte[1024];

        int len;
        while ((len = is.read(var6, 0, 1024)) > 0) {
            bos.write(var6, 0, len);
        }

        byte[] data = this.compress(bos.toByteArray());
        FileOutputStream fos = new FileOutputStream(this.getPath(block));
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
        } catch (Exception var15) {
            data = ready4Decompress;
        } finally {
            try {
                bos.close();
            } catch (IOException var14) {
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
        } catch (Exception var15) {
            data = ready4Compress;
        } finally {
            try {
                bos.close();
            } catch (IOException var14) {
            }

        }

        deflater.end();
        return data;
    }

    public boolean delete(FileBlock var1) {
        return this.getPath(var1).delete();
    }

    public boolean exists(FileBlock var1) {
        File var2 = this.getPath(var1);
        return var2.exists() && var2.length() > 0L;
    }

    private void deleteDir(File var1) {
        File[] var2 = var1.listFiles();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            File var5 = var2[var4];
            if (var5.isDirectory()) {
                this.deleteDir(var5);
                if (var5.listFiles().length == 0) {
                    var5.delete();
                }
            }
        }

    }

    public boolean copy(FileBlock var1, FileBlock var2) throws IOException {
        if (this.exists(var1) && !this.exists(var2)) {
            File var3 = this.getPath(var1);
            File var4 = this.getPath(var2);
            FileUtils.copyFile(var3, var4);
            return true;
        } else {
            return false;
        }
    }

    private File getFolderPath(FileBlock var1) {
        return new File(var1.getParentUri().getPath());
    }

    private File getPathForRead(FileBlock var1) {
        File var2 = this.getFolderPath(var1);
        return new File(var2, var1.getFileName());
    }

    private File getPath(FileBlock var1) {
        File var2 = this.getFolderPath(var1);
        if (!var2.exists()) {
            var2.mkdirs();
        }

        return new File(var2, var1.getFileName());
    }
}