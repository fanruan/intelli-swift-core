package com.fr.swift.cube.io;

import com.fr.swift.cube.io.impl.fineio.FineIoReaders;
import com.fr.swift.cube.io.impl.mem.MemIoBuilder;
import com.fr.swift.cube.io.impl.nio.NioConf;
import com.fr.swift.cube.io.impl.nio.Nios;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.util.Crasher;

/**
 * @author anchore
 */
public final class Readers {

    /**
     * delegate to FineIoReaders, MemIoBuilder
     *
     * @see FineIoReaders
     * @see MemIoBuilder
     */
    public static Reader build(IResourceLocation location, BuildConf conf) {
        switch (location.getStoreType()) {
            case FINE_IO:
                return FineIoReaders.build(location.getUri(), conf);
            case MEMORY:
                return MemIoBuilder.build(conf);
            case NIO:
                return Nios.of(new NioConf(location.getAbsolutePath(),
                        NioConf.IoType.READ), conf.getDataType());
            default:
        }
        return Crasher.crash(String.format("illegal cube build config: %s%nlocation: %s", conf, location));
    }
}