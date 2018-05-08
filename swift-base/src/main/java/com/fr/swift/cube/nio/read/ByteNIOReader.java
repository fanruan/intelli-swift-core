package com.fr.swift.cube.nio.read;

import com.fr.swift.cube.nio.NIOConstant;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ByteNIOReader extends AbstractNIOReader<Byte> {

    private Map<Long, ByteBuffer> byteBuffers = new ConcurrentHashMap<Long, ByteBuffer>();


    public ByteNIOReader(File cacheFile) {
        super(cacheFile);
    }

    @Override
    protected long getPageModeValue() {
        return NIOConstant.BYTE.PAGE_MODE_TO_AND_READ_VALUE;
    }

    @Override
    protected Byte getValue(Long index, int l) {
        return gotohell ? null : byteBuffers.get(index).get(l);
    }

    @Override
    protected long getPageStep() {
        return NIOConstant.BYTE.PAGE_STEP;
    }

    @Override
    protected void releaseChild() {
        Iterator<Entry<Long, ByteBuffer>> iter = byteBuffers.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Long, ByteBuffer> entry = iter.next();
            entry.getValue().clear();
            iter.remove();
        }
    }

    @Override
    protected void initChild(Long index, MappedByteBuffer buffer) {
        byteBuffers.put(index, buffer.asReadOnlyBuffer());
    }

}