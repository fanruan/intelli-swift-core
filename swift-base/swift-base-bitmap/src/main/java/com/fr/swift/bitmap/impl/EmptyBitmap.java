package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.IoUtil;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author anchore
 * @date 2018/7/6
 */
public class EmptyBitmap extends RangeBitmap {
    public EmptyBitmap() {
        super(0, 0);
    }

    @Override
    public byte[] toBytes() {
        return new byte[]{getType().getHead()};
    }

    @Override
    public void writeBytes(OutputStream output) {
        try {
            output.write(getType().getHead());
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            IoUtil.close(output);
        }
    }

    @Override
    public BitMapType getType() {
        return BitMapType.EMPTY;
    }

    @Override
    public String toString() {
        return "{}";
    }
}