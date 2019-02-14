package com.fr.swift.cube.io;

import com.fr.swift.cube.io.Types.WriteType;
import com.fr.swift.cube.io.impl.fineio.FineIoWriters;
import com.fr.swift.cube.io.impl.mem.MemIoBuilder;
import com.fr.swift.cube.io.impl.nio.NioConf;
import com.fr.swift.cube.io.impl.nio.Nios;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.util.Crasher;

/**
 * @author anchore
 */
public final class Writers {

    /**
     * delegate to FineIoWriters, MemIoBuilder
     *
     * @see FineIoWriters
     * @see MemIoBuilder
     */
    public static Writer build(IResourceLocation location, BuildConf conf) {
        switch (location.getStoreType()) {
            case FINE_IO:
                return FineIoWriters.build(location.getUri(), conf);
            case MEMORY:
                return MemIoBuilder.build(conf);
            case NIO:
                return Nios.of(new NioConf(location.getAbsolutePath(),
                        conf.getWriteType() == WriteType.APPEND ? NioConf.IoType.APPEND : NioConf.IoType.OVERWRITE), conf.getDataType());
            default:
        }
        return Crasher.crash(String.format("illegal cube build config: %s%nlocation: %s", conf, location));
    }
}