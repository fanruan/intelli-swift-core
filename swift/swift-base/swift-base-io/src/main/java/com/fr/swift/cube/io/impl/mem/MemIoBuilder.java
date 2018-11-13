package com.fr.swift.cube.io.impl.mem;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Util;

/**
 * @author anchore
 * @date 2017/11/24
 */
public final class MemIoBuilder {
    public static MemIo build(BuildConf conf) {
        if (Util.in(conf.getIoType(), IoType.READ, IoType.WRITE)) {
            switch (conf.getDataType()) {
                case INT:
                    return new IntMemIo();
                case LONG:
                    return new LongMemIo();
                case DOUBLE:
                    return new DoubleMemIo();
                case STRING:
                    return new StringMemIo();
                case BITMAP:
                    return new BitMapMemIo();
                case LONG_ARRAY:
                    return new LongArrayMemIo();
                case REALTIME_COLUMN:
                    return new SwiftObjectMemIo<Object>();
                default:
            }
        }
        return Crasher.crash(String.format("illegal cube build config: %s", conf));
    }
}