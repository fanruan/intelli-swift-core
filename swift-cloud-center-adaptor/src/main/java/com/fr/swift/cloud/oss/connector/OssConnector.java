package com.fr.swift.cloud.oss.connector;

import com.fineio.io.file.FileBlock;
import com.fineio.storage.AbstractConnector;
import com.fr.swift.cloud.oss.CloudOssUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2018-12-20
 */
public class OssConnector extends AbstractConnector {

    @Override
    public InputStream read(FileBlock fileBlock) throws IOException {
        try {
            return CloudOssUtils.getObjectStream(fileBlock.getBlockURI().getPath());
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException(e);
        }
    }

    @Override
    public void write(FileBlock fileBlock, InputStream inputStream) throws IOException {
        try {
            CloudOssUtils.upload(fileBlock.getBlockURI().getPath(), inputStream);
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException(e);
        }
    }

    @Override
    public boolean delete(FileBlock fileBlock) {
        try {
            CloudOssUtils.delete(fileBlock.getBlockURI().getPath());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean exists(FileBlock fileBlock) {
        try {
            return CloudOssUtils.exists(fileBlock.getBlockURI().getPath());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean copy(FileBlock fileBlock, FileBlock fileBlock1) throws IOException {
        try {
            return CloudOssUtils.copy(fileBlock.getBlockURI().getPath(), fileBlock1.getBlockURI().getPath());
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException(e);
        }
    }
}
