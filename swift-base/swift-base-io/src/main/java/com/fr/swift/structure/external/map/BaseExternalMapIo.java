package com.fr.swift.structure.external.map;

import com.fr.swift.cube.nio.NIOReader;
import com.fr.swift.cube.nio.NIOWriter;

import java.io.File;

/**
 * @author anchore
 * @date 2018/2/7
 */
public abstract class BaseExternalMapIo<K, V> implements ExternalMapIO<K, V> {
    protected File keyFile;
    protected NIOWriter<K> keyWriter;
    protected Position writePos = new Position();

    protected File valueFile;
    protected NIOReader<K> keyReader;
    protected Position readPos = new Position();
    protected int size;

    protected BaseExternalMapIo(String id) {
        keyFile = newFile(id + "_key");
        valueFile = newFile(id + "_value");
    }

    private static File newFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    protected abstract void initKeyWriter();

    protected abstract void initKeyReader();

    protected void writeKey(long pos, K key) {
        initKeyWriter();
        keyWriter.add(pos, key);
    }

    protected K readKey(long pos) {
        initKeyReader();
        return keyReader.get(pos);
    }

    @Override
    public void close() {
        if (keyWriter != null) {
            keyWriter.release();
        }
        if (keyReader != null) {
            keyReader.release();
        }
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    public static class Position {
        public long keyPos;
        public long valuePos;
    }
}