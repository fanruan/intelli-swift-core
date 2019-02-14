package com.fr.swift.cube.nio.read;

import com.fr.swift.cube.nio.NIOConstant;

import java.io.File;
import java.nio.DoubleBuffer;
import java.nio.MappedByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class DoubleNIOReader extends AbstractNIOReader<Double> {

    private Map<Long, DoubleBuffer> doubleBuffers = new ConcurrentHashMap<Long, DoubleBuffer>();

    public DoubleNIOReader(File cacheFile) {
        super(cacheFile);
    }

    @Override
    protected Double getValue(Long index, int l) {
        return doubleBuffers.get(index).get(l);
    }


    @Override
    protected long getPageStep() {
        return NIOConstant.DOUBLE.PAGE_STEP;
    }

    @Override
    protected void releaseChild() {
        Iterator<Entry<Long, DoubleBuffer>> iter = doubleBuffers.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Long, DoubleBuffer> entry = iter.next();
            entry.getValue().clear();
            iter.remove();
        }
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.DOUBLE.PAGE_MODE_TO_AND_READ_VALUE;
    }

    @Override
    protected void initChild(Long index, MappedByteBuffer buffer) {
        doubleBuffers.put(index, buffer.asDoubleBuffer());
    }

}