package com.fr.swift.fineio;

import com.fineio.io.file.FileBlock;
import com.fr.swift.cube.io.impl.fineio.connector.BaseConnector;
import com.fr.swift.file.CloudOssUtils;
import com.fr.swift.file.OssClientPool;
import com.fr.swift.util.Strings;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yee
 * @date 2018-12-20
 */
public class OssConnector extends BaseConnector {

    private OssClientPool pool;

    public OssConnector(OssClientPool pool) {
        super(Strings.EMPTY);
        this.pool = pool;
    }

    @Override
    public InputStream read(FileBlock fileBlock) throws IOException {
        try {
            return CloudOssUtils.getObjectStream(pool, fileBlock.getBlockURI().getPath());
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
            CloudOssUtils.upload(pool, fileBlock.getBlockURI().getPath(), inputStream);
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
            CloudOssUtils.delete(pool, fileBlock.getBlockURI().getPath());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean exists(FileBlock fileBlock) {
        try {
            return CloudOssUtils.exists(pool, fileBlock.getBlockURI().getPath());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean copy(FileBlock fileBlock, FileBlock fileBlock1) throws IOException {
        try {
            return CloudOssUtils.copy(pool, fileBlock.getBlockURI().getPath(), fileBlock1.getBlockURI().getPath());
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException(e);
        }
    }
}
