package com.fr.swift.cube.nio.read;

import com.fr.swift.cube.nio.NIOConstant;

import java.io.File;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class IntNIOReader extends AbstractNIOReader<Integer> {
    private Map<Long, IntBuffer> intBuffers = new ConcurrentHashMap<Long, IntBuffer>();


    public IntNIOReader(File cacheFile) {
        super(cacheFile);
    }

    @Override
    protected Integer getValue(Long index, int l) {
        return intBuffers.get(index).get(l);
    }

    @Override
    protected void releaseChild() {
        Iterator<Entry<Long, IntBuffer>> iter = intBuffers.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Long, IntBuffer> entry = iter.next();
            entry.getValue().clear();
            iter.remove();
        }
    }

    @Override
    protected void initChild(Long index, MappedByteBuffer buffer) {
        intBuffers.put(index, buffer.asIntBuffer());
    }

    @Override
    protected long getPageStep() {
        return NIOConstant.INTEGER.PAGE_STEP;
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.INTEGER.PAGE_MODE_TO_AND_READ_VALUE;
    }
}