package com.fr.bi.stable.io.io.write;

import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.write.ByteNIOWriter;
import com.fr.bi.stable.io.newio.write.IntNIOWriter;
import com.fr.bi.stable.io.newio.write.LongNIOWriter;

import java.io.File;

/**
 * Created by 小灰灰 on 14-1-7.
 */
public class ByteWriteMappedList implements NIOWriter<byte[]> {
    protected LongNIOWriter index_array;

    protected IntNIOWriter size_array;

    protected ByteNIOWriter byteList;

    protected long pos = 0;

    public ByteWriteMappedList(String path) {
        index_array = new LongNIOWriter(new File(BIPathUtils.createIndexPath(path)));
        size_array = new IntNIOWriter(new File(BIPathUtils.createSizePath(path)));
        byteList = new ByteNIOWriter(new File(path));
    }


    @Override
    public void add(long row, byte[] v) {
        if (v == null) {
            v = CubeConstant.NULLBYTES;
        }
        int len = v.length;
        long start = pos;
        index_array.add(row, new Long(start));
        size_array.add(row, new Integer(len));
        for (int i = 0; i < len; i++) {
            byteList.add(start + i, v[i]);
        }
        pos += len;
    }

    @Override
	public void setPos(long p){
        pos = p;
    }

    @Override
    public void releaseResource() {
        index_array.releaseResource();
        size_array.releaseResource();
        byteList.releaseResource();
    }

    @Override
    public void save() {
        index_array.save();
        size_array.save();
        byteList.save();
    }
}