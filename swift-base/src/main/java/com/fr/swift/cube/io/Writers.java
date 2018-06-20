package com.fr.swift.cube.io;

import com.fr.swift.cube.io.impl.fineio.FineIoWriters;
import com.fr.swift.cube.io.impl.mem.MemIoBuilder;
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
                return FineIoWriters.build(location, conf);
            case MEMORY:
                return MemIoBuilder.build(location, conf);
            default:
        }
        return Crasher.crash(String.format("illegal cube build config: %s\nlocation: %s", conf, location));
    }

}
