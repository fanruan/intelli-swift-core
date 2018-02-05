package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.cube.io.input.ByteDataInput;
import com.fr.swift.cube.io.input.ByteReader;

import java.io.IOException;

/**
 * @author 小灰灰
 * @date 2016/11/4
 */
public class ByteDataInputStream implements ByteDataInput {
    private static final String UNSUPPORTED = "ByteDataInputStream Not Support";

    private ByteReader reader;
    protected long index;
    protected long size;

    public ByteDataInputStream(ByteReader reader, long index, long size) {
        this.reader = reader;
        this.index = index;
        this.size = size;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        for (int i = 0; i < b.length; i++) {
            b[i] = reader.get(index++);
        }
    }

    @Override
    @Deprecated
    public void readFully(byte[] b, int off, int len) throws IOException {
        throw new RuntimeException(UNSUPPORTED);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        index += n;
        return n;
    }

    @Override
    @Deprecated
    public boolean readBoolean() throws IOException {
        throw new RuntimeException(UNSUPPORTED);
    }

    @Override
    public byte readByte() throws IOException {
        return reader.get(index++);
    }

    @Override
    @Deprecated
    public int readUnsignedByte() throws IOException {
        throw new RuntimeException(UNSUPPORTED);
    }

    @Override
    public short readShort() throws IOException {
        return (short) ((reader.get(index++) << 8) | (reader.get(index++) & 0xff));
    }

    @Override
    @Deprecated
    public int readUnsignedShort() throws IOException {
        throw new RuntimeException(UNSUPPORTED);
    }

    @Override
    @Deprecated
    public char readChar() throws IOException {
        throw new RuntimeException(UNSUPPORTED);
    }

    @Override
    @Deprecated
    public int readInt() throws IOException {
        return (((reader.get(index++)) << 24) |
                ((reader.get(index++) & 0xff) << 16) |
                ((reader.get(index++) & 0xff) << 8) |
                ((reader.get(index++) & 0xff)));
    }

    @Override
    public long readLong() throws IOException {
        return ((((long) reader.get(index++)) << 56) |
                (((long) reader.get(index++) & 0xff) << 48) |
                (((long) reader.get(index++) & 0xff) << 40) |
                (((long) reader.get(index++) & 0xff) << 32) |
                (((long) reader.get(index++) & 0xff) << 24) |
                (((long) reader.get(index++) & 0xff) << 16) |
                (((long) reader.get(index++) & 0xff) << 8) |
                (((long) reader.get(index++) & 0xff)));
    }

    @Override
    @Deprecated
    public float readFloat() throws IOException {
        throw new RuntimeException(UNSUPPORTED);
    }

    @Override
    @Deprecated
    public double readDouble() throws IOException {
        throw new RuntimeException(UNSUPPORTED);
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        throw new RuntimeException(UNSUPPORTED);
    }

    @Override
    @Deprecated
    public String readUTF() throws IOException {
        throw new RuntimeException(UNSUPPORTED);
    }
}
