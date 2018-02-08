package com.fr.swift.structure.external.map.intpairs;

import com.fr.swift.cube.nio.read.IntNIOReader;
import com.fr.swift.cube.nio.write.IntNIOWriter;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.external.map.BaseExternalMapIo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/1/4
 */
abstract class BaseIntPairsExtMapIo<K> extends BaseExternalMapIo<K, List<Pair<Integer, Integer>>> {
    private IntNIOWriter valueWriter;

    private IntNIOReader valueReader;
    private int size;

    BaseIntPairsExtMapIo(String id) {
        super(id);
    }

    @Override
    public void write(K key, List<Pair<Integer, Integer>> value) {
        writeKey(writePos.keyPos++, key);

        initValueWriter();
        // 写list大小
        valueWriter.add(writePos.valuePos++, value.size());
        // 紧跟list各个值
        for (Pair<Integer, Integer> intPair : value) {
            valueWriter.add(writePos.valuePos++, intPair.key());
            valueWriter.add(writePos.valuePos++, intPair.value());
        }
    }

    @Override
    public Pair<K, List<Pair<Integer, Integer>>> read() {
        if (readPos.keyPos >= size) {
            return null;
        }
        try {
            K key = readKey(readPos.keyPos++);

            if (getEndCookie().equals(key)) {
                return null;
            }

            initValueReader();
            int listSize = valueReader.get(readPos.valuePos++);
            List<Pair<Integer, Integer>> pairs = new ArrayList<Pair<Integer, Integer>>(listSize);
            for (int i = 0; i < listSize; i++) {
                pairs.add(Pair.of(
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

    protected abstract K getEndCookie();

    private void writeEndFlag() {
        write(getEndCookie(), Collections.<Pair<Integer, Integer>>emptyList());
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