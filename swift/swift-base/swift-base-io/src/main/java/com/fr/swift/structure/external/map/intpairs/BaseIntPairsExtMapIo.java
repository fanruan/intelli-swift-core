package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.cube.nio.read.IntNIOReader;
import com.fr.swift.cube.nio.write.IntNIOWriter;
import com.fr.swift.structure.IntPair;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.external.map.BaseExternalMapIo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/4
 */
abstract class BaseIntPairsExtMapIo<K> extends BaseExternalMapIo<K, List<IntPair>> {
    private IntNIOWriter valueWriter;

    private IntNIOReader valueReader;
    private int size;

    BaseIntPairsExtMapIo(String id) {
        super(id);
    }

    @Override
    public void write(K key, List<IntPair> value) {
        writeKey(writePos.keyPos++, key);

        initValueWriter();
        // 写list大小
        valueWriter.add(writePos.valuePos++, value.size());
        // 紧跟list各个值
        for (IntPair intPair : value) {
            valueWriter.add(writePos.valuePos++, intPair.getKey());
            valueWriter.add(writePos.valuePos++, intPair.getValue());
        }
    }

    @Override
    public Pair<K, List<IntPair>> read() {
        if (readPos.keyPos >= size) {
            return null;
        }
        try {
            K key = readKey(readPos.keyPos++);

            if (getEndFlag().equals(key)) {
                return null;
            }

            initValueReader();
            int listSize = valueReader.get(readPos.valuePos++);
            List<IntPair> pairs = new ArrayList<IntPair>(listSize);
            for (int i = 0; i < listSize; i++) {
                pairs.add(IntPair.of(
                        valueReader.get(readPos.valuePos++),
                        valueReader.get(readPos.valuePos++)
                ));
            }
            return Pair.of(key, pairs);
        } catch (Exception e) {
            return null;
        }
    }

    private void initValueWriter() {
        if (valueWriter == null) {
            valueWriter = new IntNIOWriter(valueFile);
        }
    }

    private void initValueReader() {
        if (valueReader == null) {
            valueReader = new IntNIOReader(valueFile);
        }
    }

    protected abstract K getEndFlag();

    private void writeEndFlag() {
        write(getEndFlag(), Collections.<IntPair>emptyList());
    }

    @Override
    public void close() {
        writeEndFlag();

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
}