package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.AbstractConnector;
import com.fineio.storage.Connector;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.org.apache.commons.io.FileUtils;


import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author yee
 * @date 2018/5/14
 * @description
 */
public class ZipConnector extends AbstractConnector {

    private ZipConnector() {
    }

    public static Connector newInstance() {
        return new ZipConnector();
    }

    public InputStream read(FileBlock file) throws IOException {
        ZipFile zipFile = new ZipFile(toFile(file, false));
        return zipFile.getInputStream(zipFile.getEntry("0"));
    }

    public void write(FileBlock file, InputStream inputStream) {
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(toFile(file, true))));
            zipOutputStream.putNextEntry(new ZipEntry("0"));
            int len;
            byte[] by = new byte[1024];
            while ((len = inputStream.read(by)) != -1) {
                zipOutputStream.write(by, 0, len);
            }
            zipOutputStream.closeEntry();
//            zipOutputStream.flush();
            zipOutputStream.close();
        } catch (IOException e) {
            SwiftLoggers.getLogger(ZipConnector.class).error(e);
        }
    }

    public boolean delete(FileBlock block) {
        return toFile(block, false).delete();
    }

    public boolean exists(FileBlock block) {
        File file = toFile(block, false);
        return file.exists() && file.length() > 0;
    }

    @Override
    public boolean copy(FileBlock srcBlock, FileBlock destBlock) throws IOException {
        if (exists(srcBlock) && !exists(destBlock)) {
            File srcFile = toFile(srcBlock, false);
            File destFile = toFile(destBlock, true);
            FileUtils.copyFile(srcFile, destFile);
            return true;
        }
        return false;
    }

    private static File toFile(FileBlock block, boolean mkdirs) {
        File dir = new File(block.getParentUri().toString());
        if (mkdirs) {
            dir.mkdirs();
        }
        return new File(dir, block.getFileName());
    }
}