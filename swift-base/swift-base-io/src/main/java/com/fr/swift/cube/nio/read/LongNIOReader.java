package com.fr.swift.cube.nio.read;

import com.fr.swift.cube.nio.NIOConstant;

import java.io.File;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class LongNIOReader extends AbstractNIOReader<Long> {
    private Map<Long, LongBuffer> longBuffers = new ConcurrentHashMap<Long, LongBuffer>();

    public LongNIOReader(File cacheFile) {
        super(cacheFile);
    }

    @Override
    protected Long getValue(Long index, int l) {
        return longBuffers.get(index).get(l);
    }

    @Override
    protected void releaseChild() {
        Iterator<Entry<Long, LongBuffer>> iter = longBuffers.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Long, LongBuffer> entry = iter.next();
            entry.getValue().clear();
            iter.remove();
        }
    }

    @Override
    protected void initChild(Long index, MappedByteBuffer buffer) {
        longBuffers.put(index, buffer.asLongBuffer());
    }

    @Override
    protected long getPageStep() {
        return NIOConstant.LONG.PAGE_STEP;
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.LONG.PAGE_MODE_TO_AND_READ_VALUE;
    }
}