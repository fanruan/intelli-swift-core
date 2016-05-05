package com.fr.bi.stable.io.io.read;

import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.read.ByteNIOReader;
import com.fr.bi.stable.io.newio.read.IntNIOReader;
import com.fr.bi.stable.io.newio.read.LongNIOReader;
import com.fr.bi.common.inter.Release;

import java.io.File;

public class ByteReadMappedList implements NIOReader<byte[]>, Release {

    private LongNIOReader index_array;

    private IntNIOReader size_array;

    private ByteNIOReader byteList;

    public ByteReadMappedList(String path) {
        index_array = new LongNIOReader(new File(BIPathUtils.createIndexPath(path)));
        size_array = new IntNIOReader(new File(BIPathUtils.createSizePath(path)));
        byteList = new ByteNIOReader(new File(path));
    }


    public ByteReadMappedList(File f) {
        String path = f.getAbsolutePath();
        index_array = new LongNIOReader(new File(BIPathUtils.createIndexPath(path)));
        size_array = new IntNIOReader(new File(BIPathUtils.createSizePath(path)));
        byteList = new ByteNIOReader(f);
    }
    @Override
    public byte[] get(final long row) {
        long start = index_array.get(row);
        int size = size_array.get(row);
        if (size == 0) {
            return new byte[]{};
        }
        byte[] b = new byte[size];
        for (int i = 0; i < size; i++) {
            b[i] = byteList.get(start + i);
        }
        return b;
    }

    @Override
	public long getLastPos(long row){
        if (row == 0){
            return 0;
        }
        long start = index_array.get(row - 1);
        int size = size_array.get(row - 1);
        return start + size;
    }

    @Override
    public void releaseResource() {
        index_array.releaseResource();
        size_array.releaseResource();
        byteList.releaseResource();
    }

}