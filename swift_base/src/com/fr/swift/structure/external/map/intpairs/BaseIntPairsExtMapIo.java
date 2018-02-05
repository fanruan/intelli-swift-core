package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Readers;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Writers;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/4
 */
abstract class BaseIntPairsExtMapIo<K> implements ExternalMapIO<K, List<Pair<Integer, Integer>>> {
    IResourceLocation keyLocation;
    private IResourceLocation valueLocation;

    private Position writePos = new Position();

    private Position readPos = new Position();
    private int size;

    private IntWriter valueWriter;
    private IntReader valueReader;

    BaseIntPairsExtMapIo(String id) {
        IResourceLocation parent = new ResourceLocation(id);
        keyLocation = parent.buildChildLocation("key");
        valueLocation = parent.buildChildLocation("value");
    }

    abstract void writeKey(int pos, K key);

    @Override
    public void write(K key, List<Pair<Integer, Integer>> value) {
        writeKey(writePos.keyPos++, key);

        initValueWriter();
        // 写list大小
        valueWriter.put(writePos.valuePos++, value.size());
        // 紧跟list各个值
        for (Pair<Integer, Integer> intPair : value) {
            valueWriter.put(writePos.valuePos++, intPair.key());
            valueWriter.put(writePos.valuePos++, intPair.value());
        }
    }

    abstract K readKey(int pos);

    @Override
    public Pair<K, List<Pair<Integer, Integer>>> read() {
        if (readPos.keyPos >= size) {
            return null;
        }
        try {
            K key = readKey(readPos.keyPos++);

            initValueReader();
            int listSize = valueReader.get(readPos.valuePos++);
            List<Pair<Integer, Integer>> pairs = new ArrayList<Pair<Integer, Integer>>(listSize);
            for (int i = 0; i < listSize; i++) {
                pairs.add(new Pair<Integer, Integer>(
                        valueReader.get(readPos.valuePos++),
                        valueReader.get(readPos.valuePos++)
                ));
            }
            return new Pair<K, List<Pair<Integer, Integer>>>(key, pairs);
        } catch (Exception e) {
            return null;
        }
    }

    private void initValueWriter() {
        if (valueWriter == null) {
            valueWriter = (IntWriter) Writers.build(valueLocation, new BuildConf(IoType.WRITE, DataType.INT));
        }
    }

    private void initValueReader() {
        if (valueReader == null) {
            valueReader = (IntReader) Readers.build(valueLocation, new BuildConf(IoType.READ, DataType.INT));
        }
    }

    @Override
    public void close() {
        if (valueWriter != null) {
            valueWriter.release();
            valueWriter = null;
        }
        if (valueReader != null) {
            valueReader.release();
            valueReader = null;
        }
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    private static class Position {
        int keyPos;
        int valuePos;
    }
}