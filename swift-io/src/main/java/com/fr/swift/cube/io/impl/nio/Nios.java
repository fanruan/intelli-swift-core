package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.cube.io.Io;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.util.Crasher;

/**
 * @author anchore
 * @date 2018/7/21
 */
public class Nios {
    @SuppressWarnings("unchecked")
    public static <IO extends Io> IO of(NioConf conf, DataType dataType) {
        switch (dataType) {
            case INT:
                return (IO) new IntNio(conf);
            case LONG:
                return (IO) new LongNio(conf);
            case DOUBLE:
                return (IO) new DoubleNio(conf);
            case STRING:
                return (IO) new StringNio(conf);
            case BITMAP:
                return (IO) new BitmapNio(conf);
            case BYTE:
                return (IO) new ByteNio(conf);
            case BYTE_ARRAY:
            case LONG_ARRAY:
            default:
                return Crasher.crash(String.format(
                        "illegal cube build config: %s as %s at %s", conf.isRead() ? "read" : "write", dataType, conf.getPath()));
        }
    }
}