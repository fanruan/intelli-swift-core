package com.finebi.cube.data.disk.reader;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.input.primitive.ICubeByteReader;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.stable.gvi.BIByteDataInput;
import com.fr.bi.stable.io.newio.NIOConstant;

import java.io.IOException;

/**
 * Created by 小灰灰 on 2016/11/4.
 */
public class ByteStreamDataInput implements BIByteDataInput {
    private static final String UNSUPPORTED = "ByteStreamDataInput Not Support";
    private ICubeByteReader reader;
    protected long index;
    protected long size;

    public ByteStreamDataInput(ICubeByteReader reader, long index, long size) {
        this.reader = reader;
        this.index = index;
        this.size = size;
    }

    public long size(){
        return size;
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        for (int i = 0; i < b.length; i++){
            try {
                b[i] = reader.getSpecificValue(index++);
            } catch (BIResourceInvalidException e) {
                BILoggerFactory.getLogger().error(e.getMessage());
            }
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
        try {
            return reader.getSpecificValue(index++);
        } catch (BIResourceInvalidException e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        return 0;
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
        try {
            return (((reader.getSpecificValue(index++)) << 24) |
                    ((reader.getSpecificValue(index++) & 0xff) << 16) |
                    ((reader.getSpecificValue(index++) & 0xff) <<  8) |
                    ((reader.getSpecificValue(index++) & 0xff)      ));
        } catch (BIResourceInvalidException e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        return NIOConstant.INTEGER.NULL_VALUE;
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
