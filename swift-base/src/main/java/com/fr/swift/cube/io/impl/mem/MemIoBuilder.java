package com.fr.swift.cube.io.impl.mem;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.segment.column.Column;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Util;

/**
 * @author anchore
 * @date 2017/11/24
 */
public final class MemIoBuilder {
    public static MemIo build(IResourceLocation location, BuildConf conf) {
        if (Util.in(conf.ioType, Util.toArray(IoType.READ, IoType.WRITE)) &&
                StoreType.MEMORY == location.getStoreType()) {
            switch (conf.dataType) {
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
                    return new SwiftObjectMemIo<Column>();
                default:
            }
        }
        return Crasher.crash(String.format("illegal cube build config: %s", conf));
    }
}