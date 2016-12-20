package com.fr.bi.stable.gvi;

import java.io.IOException;

/**
 * Created by 小灰灰 on 2016/11/7.
 */
public class ByteArrayDataInput implements BIByteDataInput {
    private static final String UNSUPPORTED = "ByteArrayDataInput Not Support";
    private int index = 0;
    private byte[] data;

    public ByteArrayDataInput(byte[] data) {
        this.data = data;
    }

    public long size(){
        return data.length;
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        for (int i = 0; i < b.length; i++){
            b[i] = data[index++];
        }
    }

    @Override
    @Deprecated
    public void readFully(byte[] b, int off, int len) throws IOException {
        throw new RuntimeException(UNSUPPORTED);
    }

    @Override
    @Deprecated
    public int skipBytes(int n) throws IOException {
        throw new RuntimeException(UNSUPPORTED);
    }

    @Override
    @Deprecated
    public boolean readBoolean() throws IOException {
        throw new RuntimeException(UNSUPPORTED);
    }

    @Override
    public byte readByte() throws IOException {
        return data[index++];
    }

    @Override
    @Deprecated
    public int readUnsignedByte() throws IOException {
        throw new RuntimeException(UNSUPPORTED);
    }

    @Override
    @Deprecated
    public short readShort() throws IOException {
        throw new RuntimeException(UNSUPPORTED);
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
        return (((data[index++]) << 24) |
                ((data[index++] & 0xff) << 16) |
                ((data[index++] & 0xff) <<  8) |
                ((data[index++] & 0xff)      ));
    }

    @Override
    @Deprecated
    public long readLong() throws IOException {
        throw new RuntimeException(UNSUPPORTED);
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

